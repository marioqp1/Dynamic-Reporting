package com.fuinco.report_manager.service;

import com.fuinco.report_manager.report.entity.EntityField;
import com.fuinco.report_manager.repository.EntityFieldsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntityFieldsService {
    private final EntityFieldsRepository fieldsRepository;

    public EntityFieldsService(EntityFieldsRepository fieldsRepository) {
        this.fieldsRepository = fieldsRepository;
    }
    public EntityField create(EntityField field) {
        EntityField f = new EntityField();
        if(field.getFieldName()!=null && !field.getFieldName().isEmpty()) {
             f = fieldsRepository.save(field);
        }
        return f;
    }
    public List<EntityField> findAll() {
        return fieldsRepository.findAll();
    }
    public String returnType(String entityName,String fieldName) {
        return fieldsRepository.findByEntityNameAndFieldName(entityName, fieldName);
    }

}
