package com.fuinco.report_manager.report.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "relations")
public class EntityRelation {
    private String id;
    private String entityName;
    private String localField;
    private String joinField;
    private List<String> fields;
}
