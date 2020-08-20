package com.team3.rating.Database;

import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@Component
public class RatingDataOperator {

    private final MongoCollection<Document> ratings; //Database which storing all ratings from all users
    private final MongoCollection<Document> averageRatings; //Database storing average post ratings by criteria and the
    // average between them

    public RatingDataOperator(@Value("${mongodb.host}") String host,
                              @Value("${mongodb.port}") int port,
                              @Value("${mongodb.databaseName}")  String databaseName,
                              @Value("${mongodb.ratingsCollectionName}")  String ratingsCollectionName,
                              @Value("${mongodb.averageRatingsCollectionName}")  String averageRatingsCollectionName) {
        // connection to mongoDB
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
     * Find rating by criterion
     */
    public Integer findRating(String userId, String collectionId, String postId, String criterionName){
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
     * Find all rating by post
     */
    public MongoCursor<Document> findAllRating(String collectionId, String postId){
        MongoCursor<Document> doc = ratings.find(
                and(
                        eq("collection_id", collectionId),
                        eq("post_id", postId)
                )
        ).iterator();

        return doc;
    }

    /**
     * Delete document from db
     */
    public void deleteData(String collectionId, String postId, String userId) {
        ratings.deleteOne(and(
                eq("collection_id", collectionId),
                eq("post_id", postId),
                eq("user_id", userId)
        ));
    }


    /**
     * Average rating
     */
    public void createAverageRating(Document doc) {

        String collectionId = doc.getString("collection_id");
        String postId = doc.getString("post_id");

        Bson updateOperation = combine(set("average_rating_by_criterion", doc.get("average_rating_by_criterion")),
                set("average_rating", doc.get("average_rating")));

        averageRatings.updateOne(
                and(
                        eq("collection_id", collectionId),
                        eq("post_id", postId)
                ),
                updateOperation,
                new UpdateOptions().upsert(true).bypassDocumentValidation(true)
        );

    }

    public void deletePostRatings(String postId) {
        ratings.deleteMany(eq("post_id", postId));
    }

    public void deletePostAverageRatings(String postId) {
        averageRatings.deleteMany(eq("post_id", postId));
    }

    /**
     * Get post average rating in collection
     * @param collectionId id of collection
     * @param postId id of post
     * @return integer average rating
     */
    public Integer getAveragePostRating(String collectionId, String postId) {
        Document found =  averageRatings.find(and(
                eq("post_id", postId), eq("collection_id", collectionId)
        )).first();

        if (found == null)
            return null;

        return found.getInteger("average_rating");
    }

    /**
     * Delete all post ratings from collection
     * @param collectionId id of collection
     * @param postId id of post
     */
    public void deletePostRatingsFromCollection(String collectionId, String postId) {
        ratings.deleteMany(and(eq("collection_id", collectionId), eq("post_id", postId)));
    }

    public Integer getAveragePostRatingByCriterion(String collectionId, String postId, String criterionName) {
        Document found =  averageRatings.find(and(
                eq("post_id", postId), eq("collection_id", collectionId)
        )).first();

        if (found == null)
            return null;

        return ((Document) found.get("average_rating_by_criterion")).getInteger(criterionName);
    }
}
