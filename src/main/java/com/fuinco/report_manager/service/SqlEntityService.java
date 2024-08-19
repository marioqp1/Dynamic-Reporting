package com.fuinco.report_manager.service;

import com.fuinco.report_manager.service.intface.EntityService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SqlEntityService implements EntityService {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<String> getFields(String entityName) {
        Class<?> entityClass = getEntityClass(entityName);
        Field[] fields = entityClass.getDeclaredFields();
        return Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> getRelationships(String entityName) {
        Class<?> entityClass = getEntityClass(entityName);
        EntityType<?> entityType = entityManager.getMetamodel().entity(entityClass);

        return entityType.getAttributes().stream()
                .filter(Attribute::isAssociation)
                .map(Attribute::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> findByCriteria(String entityName, Map<String, Object> criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery(Object.class);
        Root<?> root = query.from(getEntityClass(entityName));

        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, Object> entry : criteria.entrySet()) {
            predicates.add(cb.equal(root.get(entry.getKey()), entry.getValue()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public void saveEntity(String entityName, Object entity) {
        entityManager.persist(entity);
    }

    private Class<?> getEntityClass(String entityName) {
        try {
            return Class.forName("com.fuinco.report_manager.report.entity." + entityName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid entity name");
        }
    }
}
