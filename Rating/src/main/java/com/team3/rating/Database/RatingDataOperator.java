package com.team3.rating.Database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

@Component
public class RatingDataOperator {

    private final MongoCollection<Document> collection;

    public RatingDataOperator(@Value("${mongodb.host}") String host,
                              @Value("${mongodb.port}") int port,
                              @Value("${mongodb.databaseName}")  String databaseName,
                              @Value("${mongodb.collectionName}")  String collectionName) {
        /**
         * Connection to MongoDb
         */
        MongoClient mongoClient = new MongoClient( host, port );
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        collection = database.getCollection(collectionName);
    }

    /**
     * Creates mongoDB entry
     */
    public void createRating(Document doc) {
        Document exRating = new Document(doc);

        Integer userId = exRating.getInteger("userID");
        Integer collectionId = exRating.getInteger("collectionID");
        Integer postId = exRating.getInteger("postID");

        Bson updateOperation = set("rating", doc.get("rating"));

        collection.updateOne(
                and(
                        eq("collectionID", collectionId),
                        eq("postID", postId),
                        eq("userID", userId)
                ),
                updateOperation,
                new UpdateOptions().upsert(true).bypassDocumentValidation(true)
        );
    }

    /**
     * Findes rating by criterion
     */
    public Integer findRating(Integer userId, Integer collectionId, Integer postId, String criterionName){
        Document response = collection.find(
                and(
                        eq("collectionID", collectionId),
                        eq("postID", postId),
                        eq("userID", userId)
                )
        ).first();

        return ((Document)response.get("rating")).getInteger(criterionName);
    }

    /**
     * Delete document from db
     *
     */
    public void deleteData(Integer collectionId, Integer postId, Integer userId) {
        collection.deleteOne(and(
                eq("collectionID", collectionId),
                eq("postID", postId),
                eq("userID", userId)
        ));
    }
}
