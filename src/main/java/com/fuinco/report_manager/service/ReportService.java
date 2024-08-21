package com.fuinco.report_manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuinco.report_manager.dto.ApiResponse;
import com.fuinco.report_manager.report.entity.Entity;
import com.fuinco.report_manager.report.entity.Filter;
import com.fuinco.report_manager.report.entity.JoinRequest;
import com.fuinco.report_manager.report.entity.Report;
import com.fuinco.report_manager.repository.EntityRepository;
import com.fuinco.report_manager.repository.ReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ReportService {

    private final EntityManager entityManager;

    private final ReportRepository reportRepository;

    private final MongoTemplate mongoTemplate;

    private final EntityRepository entityRepository;

    public ReportService(ReportRepository reportRepository, EntityManager entityManager, MongoTemplate mongoTemplate, EntityRepository entityRepository) {
        this.entityManager = entityManager;
        this.reportRepository = reportRepository;
        this.mongoTemplate = mongoTemplate;
        this.entityRepository = entityRepository;
    }


    private Predicate createPredicate(CriteriaBuilder cb, Root<?> root, Filter filter) {
        String operator = filter.getOperator().toUpperCase();
        switch (operator) {
            case "=":
                return cb.equal(root.get(filter.getField()), filter.getValue());
            case "!=":
                return cb.notEqual(root.get(filter.getField()), filter.getValue());
            case ">":
                return cb.greaterThan(root.get(filter.getField()), (Comparable) filter.getValue());
            case "<":
                return cb.lessThan(root.get(filter.getField()), (Comparable) filter.getValue());
            case "LIKE":
                return cb.like(root.get(filter.getField()), "%" + filter.getValue() + "%");
            case "IN":
                return root.get(filter.getField()).in((Object[]) filter.getValue());
            case "OR":
                List<Predicate> orPredicates = new ArrayList<>();
                for (Filter nestedFilter : filter.getNestedFilters()) {
                    orPredicates.add(createPredicate(cb, root, nestedFilter));
                }
                return cb.or(orPredicates.toArray(new Predicate[0]));
            case "BETWEEN":
                return cb.between(root.get(filter.getField()), (Comparable) filter.getValue(), (Comparable) filter.getValue2());
            case ">=":
                return cb.greaterThanOrEqualTo(root.get(filter.getField()), (Comparable) filter.getValue());
            case "<=":
                return cb.lessThanOrEqualTo(root.get(filter.getField()), (Comparable) filter.getValue());
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }


    private List<Predicate> applyFilters(CriteriaBuilder cb, Root<?> root, List<Filter> filters) {
        List<Predicate> predicates = new ArrayList<>();

        for (Filter filter : filters) {
            if (filter.getValue() == null || filter.getValue().equals("")) {
                break;
            }
            Predicate predicate = createPredicate(cb, root, filter);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }
        return predicates;
    }


    private Class<?> getEntityClass(String entityName) {
        try {
            String word = entityName.toUpperCase();
            entityName = word.charAt(0) + word.substring(1).toLowerCase();
            System.out.println(entityName);


            return Class.forName("com.fuinco.report_manager.model." + entityName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid entity name");
        }
    }

    public ApiResponse<Report> createReport(Report reportRequest) {
        ApiResponse<Report> apiResponse = new ApiResponse<>();

        if (reportRequest.getEntity() == null || reportRequest.getEntity().isEmpty()) {
            apiResponse.setMessage("Check Entity");
            apiResponse.setSuccess(false);
            apiResponse.setStatusCode(500);
            return apiResponse;
        }
        if (reportRequest.getFields().isEmpty()) {
            apiResponse.setMessage("Check Fields");
            apiResponse.setSuccess(false);
            apiResponse.setStatusCode(500);
            return apiResponse;
        }
        if (reportRequest.getUserId() == 0) {
            apiResponse.setMessage("Check User Id");
            apiResponse.setSuccess(false);
            apiResponse.setStatusCode(500);
            return apiResponse;
        }
        apiResponse.setEntity(reportRequest);
        apiResponse.setSuccess(true);
        apiResponse.setStatusCode(200);
        reportRepository.save(reportRequest);
        apiResponse.setMessage("Report Created");
        return apiResponse;
    }

    public ApiResponse<List<Report>> findByUserId(int userId) {
        ApiResponse<List<Report>> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setStatusCode(200);
        apiResponse.setMessage("Report Found");
        List<Report> list = reportRepository.findAllByUserId(userId);
        apiResponse.setEntity(list);
        return apiResponse;
    }

    public List<Report> allReports() {
        return reportRepository.findAll();
    }

    private boolean isMongoEntity(String entityName) {
        Entity entity = entityRepository.findByEntityName(entityName);
        return entity.getDataBaseName().equals("Mongo");

    }

    public ResponseEntity<List<Document>> generateReport(Report reportRequest) {
        if (isMongoEntity(reportRequest.getEntity())) {
            return generateMongoReport(reportRequest);
        } else {
            return generateSqlReport(reportRequest);
        }
    }

    private ResponseEntity<List<Document>> generateSqlReport(Report reportRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery(Object.class);
        Root<?> entityRoot = query.from(getEntityClass(reportRequest.getEntity()));

        List<Selection<?>> selections = new ArrayList<>();
        for (String field : reportRequest.getFields()) {
            selections.add(entityRoot.get(field));
        }

        List<Object> sqlResults = new ArrayList<>();
        List<Document> resultDocuments = new ArrayList<>();

        for (JoinRequest joinRequest : reportRequest.getJoinRequests()) {
            if (isMongoEntity(joinRequest.getEntityJoin())) {
                // SQL results are initially empty if there's a MongoDB join request
                if (sqlResults.isEmpty()) {
                    sqlResults = entityManager.createQuery(query).getResultList();
                }
                // Handle MongoDB join
                List<Document> mongoJoinResults = performMongoJoin(joinRequest, sqlResults);

                sqlResults = mergeResults(sqlResults, mongoJoinResults, joinRequest);
            } else {
                // Handle SQL join
                Join<Object, Object> join = entityRoot.join(joinRequest.getJoinField());
                for (String field : joinRequest.getFields()) {
                    selections.add(join.get(field));
                }
            }
        }

        if (sqlResults.isEmpty()) {
            query.multiselect(selections);

            List<Predicate> predicates = applyFilters(cb, entityRoot, reportRequest.getFilters());
            if (!predicates.isEmpty()) {
                query.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            sqlResults = entityManager.createQuery(query).getResultList();
        }

        for (Object sqlResult : sqlResults) {
            Document doc = new Document(convertToMap(sqlResult));
            resultDocuments.add(doc);
        }

        List<Document> filteredDocuments = applyDocumentFilters(resultDocuments, reportRequest.getFilters());

        return ResponseEntity.ok(filteredDocuments);
    }

    private List<Document> applyDocumentFilters(List<Document> documents, List<Filter> filters) {
        List<Document> filteredDocuments = new ArrayList<>(documents);

        for (Filter filter : filters) {
            filteredDocuments.removeIf(doc -> !matchesFilter(doc, filter));
        }

        return filteredDocuments;
    }

    private boolean matchesFilter(Document doc, Filter filter) {
        List<Object> docValue = Collections.singletonList(doc.get(filter.getField()));
        String lastValue = "";
        for (Object value : docValue) {
            lastValue = value.toString();
        }
        String fieldValue = lastValue;

        if (fieldValue == null) return false;

        return switch (filter.getOperator().toUpperCase()) {
            case "=" -> fieldValue.equals(filter.getValue());
            case "!=" -> !fieldValue.equals(filter.getValue());
            case ">" -> compareValues(fieldValue, filter.getValue()) > 0;
            case ">=" -> compareValues(fieldValue, filter.getValue()) >= 0;
            case "<" -> compareValues(fieldValue, filter.getValue()) < 0;
            case "<=" -> compareValues(fieldValue, filter.getValue()) <= 0;
            case "BETWEEN" -> {
                List<Object> values = new ArrayList<>();
                values.add(filter.getValue());
                values.add(filter.getValue2());

                yield compareValues(fieldValue, values.get(0)) >= 0 && compareValues(fieldValue, values.get(1)) <= 0;
            }
            case "LIKE" -> fieldValue.matches((String) filter.getValue());
            case "IN" -> ((List<?>) filter.getValue()).contains(fieldValue);
            default -> throw new IllegalArgumentException("Unsupported operator: " + filter.getOperator());
        };
    }

    private int compareValues(Object fieldValue, Object filterValue) {
        if (fieldValue instanceof Comparable && filterValue instanceof Comparable) {
            return ((Comparable) fieldValue).compareTo(filterValue);
        }
        throw new IllegalArgumentException("Values are not comparable: " + fieldValue + " and " + filterValue);
    }

    private List<Object> mergeResults(List<Object> primaryResults, List<?> joinResults, JoinRequest joinRequest) {
        List<Object> mergedResults = new ArrayList<>();

        for (Object primary : primaryResults) {
            Map<String, Object> mergedRecord = new HashMap<>(convertToMap(primary));

            for (Object join : joinResults) {
                Map<String, Object> joinRecord = convertToMap(join);

                String primaryFieldValue = mergedRecord.get(joinRequest.getLocalField()) != null ? mergedRecord.get(joinRequest.getLocalField()).toString() : null;
                String joinFieldValue = joinRecord.get(joinRequest.getJoinField()) != null ? joinRecord.get(joinRequest.getJoinField()).toString() : null;

                if (primaryFieldValue != null && primaryFieldValue.equals(joinFieldValue)) {
                    for (String field : joinRequest.getFields()) {
                        // Check if the field already exists and is a list
                        if (mergedRecord.containsKey(field) && mergedRecord.get(field) instanceof List) {
                            List<Object> fieldList = (List<Object>) mergedRecord.get(field);
                            fieldList.add(joinRecord.get(field));
                        } else if (mergedRecord.containsKey(field)) {
                            // If the field exists but is not a list, convert it to a list
                            List<Object> fieldList = new ArrayList<>();
                            fieldList.add(mergedRecord.get(field));
                            fieldList.add(joinRecord.get(field));
                            mergedRecord.put(field, fieldList);
                        } else {
                            // If the field does not exist, add it as a list with a single element
                            List<Object> fieldList = new ArrayList<>();
                            fieldList.add(joinRecord.get(field));
                            mergedRecord.put(field, fieldList);
                        }
                    }
                }
            }

            mergedResults.add(mergedRecord);
        }

        return mergedResults;
    }

    private List<Document> performMongoJoin(JoinRequest joinRequest, List<Object> sqlResults) {
        Query mongoQuery = new Query();

        List<Criteria> criteriaList = new ArrayList<>();
        for (Object sqlResult : sqlResults) {
            Map<String, Object> sqlRecord = convertToMap(sqlResult);
            Object joinFieldValue = sqlRecord.get(joinRequest.getLocalField());

            criteriaList.add(Criteria.where(joinRequest.getJoinField()).is(joinFieldValue));
        }

        mongoQuery.addCriteria(new Criteria().orOperator(criteriaList.toArray(new Criteria[0])));

        mongoQuery.fields().include(joinRequest.getFields().toArray(new String[0]));

        return mongoTemplate.find(mongoQuery, Document.class, joinRequest.getEntityJoin().toLowerCase());
    }

    private Map<String, Object> convertToMap(Object result) {
        if (result instanceof Map) {
            return (Map<String, Object>) result;
        } else if (result instanceof Document) {
            return documentToMap((Document) result);
        } else {
            return new ObjectMapper().convertValue(result, Map.class);
        }
    }

    private Map<String, Object> documentToMap(Document document) {
        return new HashMap<>(document);
    }

    private ResponseEntity<List<Document>> generateMongoReport(Report reportRequest) {
        // Create a query object
        Query mongoQuery = new Query();

        // Apply filters to the query
        for (Filter filter : reportRequest.getFilters()) {
            mongoQuery.addCriteria(createMongoCriteria(filter));
        }

        // Include the requested fields
        if (reportRequest.getFields() != null && !reportRequest.getFields().isEmpty()) {
            mongoQuery.fields().include(reportRequest.getFields().toArray(new String[0]));
        }

        // Execute the query and retrieve the results
        List<Document> results = mongoTemplate.find(mongoQuery, Document.class, reportRequest.getEntity().toLowerCase());

        // Return the results
        return ResponseEntity.ok(results);
    }

    private Criteria createMongoCriteria(Filter filter) {
        return switch (filter.getOperator().toUpperCase()) {
            case "=" -> Criteria.where(filter.getField()).is(filter.getValue());
            case "!=" -> Criteria.where(filter.getField()).ne(filter.getValue());
            case ">" -> Criteria.where(filter.getField()).gt(filter.getValue());
            case ">=" -> Criteria.where(filter.getField()).gte(filter.getValue());
            case "<" -> Criteria.where(filter.getField()).lt(filter.getValue());
            case "<=" -> Criteria.where(filter.getField()).lte(filter.getValue());
            case "LIKE" -> Criteria.where(filter.getField()).regex((String) filter.getValue());
            case "IN" -> Criteria.where(filter.getField()).in((List<?>) filter.getValue());
            case "BETWEEN" -> Criteria.where(filter.getField()).gte(filter.getValue()).lte(filter.getValue2());
            default -> throw new IllegalArgumentException("Unsupported operator: " + filter.getOperator());
        };
    }
}



