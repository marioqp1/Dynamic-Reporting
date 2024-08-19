package com.fuinco.report_manager.report.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "entity_fields")
public class EntityField {
    private String id;
    private String entityName;
    private String fieldName;
    private String fieldType;
}
