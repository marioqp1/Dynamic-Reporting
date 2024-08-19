package com.fuinco.report_manager.repository;


import com.fuinco.report_manager.report.entity.Entity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntityRepository extends MongoRepository<Entity, String> {
    Entity findByEntityName(String name);
}
