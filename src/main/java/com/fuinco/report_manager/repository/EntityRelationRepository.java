package com.fuinco.report_manager.repository;

import com.fuinco.report_manager.report.entity.EntityRelation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntityRelationRepository extends MongoRepository<EntityRelation, Integer> {
}
