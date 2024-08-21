package com.fuinco.report_manager.repository;

import com.fuinco.report_manager.report.entity.EntityField;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityFieldsRepository extends MongoRepository<EntityField,String> {
    EntityField findByEntityNameAndFieldName(String entityName,String fieldName);

}
