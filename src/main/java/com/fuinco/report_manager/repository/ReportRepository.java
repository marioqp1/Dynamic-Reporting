package com.fuinco.report_manager.repository;

import com.fuinco.report_manager.report.entity.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReportRepository extends MongoRepository<Report,String> {
 List<Report> findAllByUserId(int userId);
}
