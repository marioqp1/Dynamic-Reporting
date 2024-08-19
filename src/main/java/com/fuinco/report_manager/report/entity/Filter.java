package com.fuinco.report_manager.report.entity;

import lombok.Data;

import java.util.List;

@Data
public class Filter {
    private String field;
    private String operator;
    private Object value;
    private Object value2;
    private List<Filter> nestedFilters;
}
