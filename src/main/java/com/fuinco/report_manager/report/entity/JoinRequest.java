package com.fuinco.report_manager.report.entity;

import lombok.Data;
import java.util.List;

@Data
public class JoinRequest {
    private String entityJoin;
    private String joinField;
    private String localField;
    private List<String> fields;


}
