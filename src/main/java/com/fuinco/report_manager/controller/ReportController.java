package com.fuinco.report_manager.controller;

import com.fuinco.report_manager.service.ReportService;
import com.fuinco.report_manager.dto.ApiResponse;
import com.fuinco.report_manager.report.entity.Report;

import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")


public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {

        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<List<Document>> generateReport(@RequestBody Report reportModel) {
        return reportService.generateReport(reportModel);
    }

    @PostMapping("/create")
    public ApiResponse<Report> createReport(@RequestBody Report report) {

        return reportService.createReport(report);
    }

    @GetMapping("/{id}")
    public ApiResponse<List<Report>> getReportById(@PathVariable int id) {
        return reportService.findByUserId(id);
    }

    @GetMapping("")
    public List<Report> getAllReports() {
        return reportService.allReports();
    }

//    @GetMapping("/entity/{entityName}")
//    public ReportService.EntityMetadata metadata(@PathVariable String entityName) {
//        return reportService.getEntityMetadata(entityName);
//    }

}




