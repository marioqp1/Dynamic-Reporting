package com.fuinco.report_manager.service.intface;

import java.util.List;
import java.util.Map;

public interface EntityService {
    List<String> getFields(String entityName);
    List<String> getRelationships(String entityName);
    List<Object> findByCriteria(String entityName, Map<String, Object> criteria);
    void saveEntity(String entityName, Object entity);
}

