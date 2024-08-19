package com.fuinco.report_manager.report.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "entity")
public class Entity {
    private String id;
    private String entityName;
    private String DataBaseName;
    private List <String> fields;
    private List<EntityRelation> relations;

}
