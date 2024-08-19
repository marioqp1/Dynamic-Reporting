package com.fuinco.report_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private Integer statusCode;
    private Boolean success;
    private String message;
    private T entity;
}
