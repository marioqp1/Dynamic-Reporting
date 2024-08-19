package com.fuinco.report_manager.report.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "report")
public class Report {
    @Id
    private String id;
    private int userId;
    private String ReportName;
    private String entity;
    private List<String> fields;
    private List<Filter> filters;
    private List<JoinRequest> joinRequests;
}
