package com.fuinco.report_manager.service;

import com.fuinco.report_manager.dto.ApiResponse;
import com.fuinco.report_manager.report.entity.Entity;
import com.fuinco.report_manager.report.entity.EntityRelation;
import com.fuinco.report_manager.repository.EntityRelationRepository;
import com.fuinco.report_manager.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EntityService {
    private final EntityRepository entityRepository;
    private final EntityRelationRepository entityRelationRepository;
    private final EntityFieldsService entityFieldsService;

    public EntityService(EntityRepository entityRepository, EntityRelationRepository entityRelationRepository, EntityFieldsService entityFieldsService) {
        this.entityRepository = entityRepository;
        this.entityRelationRepository = entityRelationRepository;
        this.entityFieldsService = entityFieldsService;
    }

    public Entity createEntity(Entity entity) {
        if (entity == null) {
            return null;
        }
        List<EntityRelation> relations = new ArrayList<>();
        for (EntityRelation entityRelation : entity.getRelations()) {
            relations.add(entityRelationRepository.save(entityRelation));
        }
        entity.setRelations(relations);
        entity.setDataBaseName("Mongo");
        return entityRepository.save(entity);
    }

    public ApiResponse<List<Entity>> findAll() {
        ApiResponse<List<Entity>> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("OK");
        apiResponse.setStatusCode(200);
        List<Entity> entities = entityRepository.findAll();
        for (Entity entity : entities) {
            entity.setRelations(null);
            entity.setFields(null);
        }
        apiResponse.setEntity(entities);
        return apiResponse;
    }

    public ApiResponse<Entity> findById(String id) {
        ApiResponse<Entity> entityApiResponse = new ApiResponse<>();
        if (id == null) {
            entityApiResponse.setMessage("id is null");
            entityApiResponse.setStatusCode(400);
            entityApiResponse.setSuccess(false);
            return entityApiResponse;
        }
        if (entityRepository.existsById(id)) {
            Entity entity = entityRepository.findById(id).orElse(null);
            if (entity != null) {
                entityApiResponse.setEntity(entity);
                entityApiResponse.setStatusCode(200);
                entityApiResponse.setSuccess(true);
                entityApiResponse.setMessage("entity found");
            } else {
                entityApiResponse.setStatusCode(404);
                entityApiResponse.setSuccess(false);
                entityApiResponse.setMessage("entity not found");
            }

        } else {
            entityApiResponse.setStatusCode(404);
            entityApiResponse.setSuccess(false);
            entityApiResponse.setMessage("entity not found");
        }
        return entityApiResponse;
    }

    public ApiResponse<Entity> update(Entity entity) {
        ApiResponse<Entity> apiResponse = new ApiResponse<>();
        if (entity.getId() == null || entity.getId().isEmpty()) {
            apiResponse.setStatusCode(400);
            apiResponse.setSuccess(false);
            apiResponse.setMessage("id is null");
            return apiResponse;
        }
        if (entityRepository.existsById(entity.getId())) {
            apiResponse.setEntity(entityRepository.save(entity));
            apiResponse.setStatusCode(200);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("entity updated");
        } else {
            apiResponse.setStatusCode(404);
            apiResponse.setSuccess(false);
            apiResponse.setMessage("entity not found");
        }
        return apiResponse;
    }

    public Entity getEntityByName(String Name) {
        return entityRepository.findByEntityName(Name);
    }


    public ApiResponse<List<String>> getEntityFieldOperator(String entityName, String fieldName) {
        ApiResponse<List<String>> apiResponse = new ApiResponse<>();
        List<String> operators = new ArrayList<>();

        try {
            // Retrieve the data type from the database
            String dataType = entityFieldsService.returnType(entityName, fieldName);

            switch (dataType.toUpperCase()) {
                case "INTEGER":
                case "DOUBLE":
                    // Numeric types
                    operators.add("=");
                    operators.add("!=");
                    operators.add(">");
                    operators.add(">=");
                    operators.add("<");
                    operators.add("<=");
                    operators.add("BETWEEN");
                    operators.add("IN");
                    break;
                case "STRING":
                    // String type
                    operators.add("=");
                    operators.add("!=");
                    operators.add("LIKE");
                    operators.add("IN");
                    break;
                case "DATE":
                    // Date type
                    operators.add("=");
                    operators.add("!=");
                    operators.add(">");
                    operators.add(">=");
                    operators.add("<");
                    operators.add("<=");
                    operators.add("BETWEEN");
                    break;
                case "BOOLEAN":
                    // Boolean type
                    operators.add("=");
                    operators.add("!=");
                    break;
                default:
                    // Default operators for any other types
                    operators.add("=");
                    operators.add("!=");
                    break;
            }

            apiResponse.setEntity(operators);
            apiResponse.setSuccess(true);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error retrieving operators: " + e.getMessage());
        }

        return apiResponse;
    }

}

