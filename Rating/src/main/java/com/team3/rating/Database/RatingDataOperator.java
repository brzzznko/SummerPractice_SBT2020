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

    private final MongoCollection<Document> ratings; //Database which storing all ratings from all users
    private final MongoCollection<Document> averageRatings; //Database storing average post ratings by criteria and the average between them

    public RatingDataOperator(@Value("${mongodb.host}") String host,
                              @Value("${mongodb.port}") int port,
                              @Value("${mongodb.databaseName}")  String databaseName,
                              @Value("${mongodb.ratingsCollectionName}")  String ratingsCollectionName,
                              @Value("${mongodb.averageRatingsCollectionName}")  String averageRatingsCollectionName) {

        // Connection to MongoDb
        MongoClient mongoClient = new MongoClient( host, port );
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        ratings = database.getCollection(ratingsCollectionName);
        averageRatings = database.getCollection(averageRatingsCollectionName);
    }

    /**
     * Creates mongoDB entry
     */
    public void createRating(Document doc) {

        Integer userId = doc.getInteger("user_id");
        String collectionId = doc.getString("collection_id");
        String postId = doc.getString("post_id");

        Bson updateOperation = set("rating", doc.get("rating"));

        ratings.updateOne(
                and(
                        eq("collection_id", collectionId),
                        eq("post_id", postId),
                        eq("user_id", userId)
                ),
                updateOperation,
                new UpdateOptions().upsert(true).bypassDocumentValidation(true)
        );
    }

    /**
     * Findes rating by criterion
     */
    public Integer findRating(Integer userId, String collectionId, String postId, String criterionName){
        Document response = ratings.find(
                and(
                        eq("collection_id", collectionId),
                        eq("post_id", postId),
                        eq("user_id", userId)
                )
        ).first();

        return ((Document)response.get("rating")).getInteger(criterionName);
    }

    /**
     * Delete document from db
     */
    public void deleteData(String collectionId, String postId, Integer userId) {
        ratings.deleteOne(and(
                eq("collection_id", collectionId),
                eq("post_id", postId),
                eq("user_id", userId)
        ));
    }

    /**
     * Delete all post ratings from collection
     * @param collectionId id of collection
     * @param postId id of post
     */
    public void deletePostRatingsFromCollection(String collectionId, String postId) {
        ratings.deleteMany(and(eq("collection_id", collectionId), eq("post_id", postId)));
    }

    /**
     * Find rating
     * @param collectionId  collection id in which the post was rated
     * @param postId post id
     * @param userId user id who rate post
     * @return return Json with rating
     */
    public Document findRating(String collectionId, String postId, Integer userId) {
        return ratings.find(
                and(
                        eq("collection_id", collectionId),
                        eq("post_id", postId),
                        eq("user_id", userId)
                )
        ).first();
    }
}
