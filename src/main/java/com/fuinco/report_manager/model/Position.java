package com.fuinco.report_manager.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "position")
public class Position {
    @Id
    private String id;
    private int vehicleId;
    private Double lat;
    private Double lon;
}
