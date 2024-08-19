package com.fuinco.report_manager.service;

import com.fuinco.report_manager.report.entity.JoinRequest;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class MongoService {

    private final MongoTemplate mongoTemplate;

    public MongoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Document> performJoin(JoinRequest joinRequest, String primaryCollectionName) {
        // $lookup stage for joining collections
        AggregationOperation lookup = lookup(
                joinRequest.getEntityJoin(),         // Foreign collection name from JoinRequest
                joinRequest.getJoinField(),      // Local field in primary collection
                joinRequest.getLocalField(),                           // Foreign field in foreign collection (assuming the field to join on is '_id')
                joinRequest.getEntityJoin()
        );

        ProjectionOperation project = project()
                // Include specific fields from the primary collection
                .andInclude(joinRequest.getFields().toArray(new String[0]))
                // Include specific fields from the joined collection
                .and(joinRequest.getEntityJoin() + "." + joinRequest.getJoinField())
                .as(joinRequest.getJoinField());

        // Create the aggregation pipeline
        Aggregation aggregation = newAggregation(lookup, project);

        // Execute the aggregation and retrieve the results
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, primaryCollectionName, Document.class);

        return results.getMappedResults();
    }
}
