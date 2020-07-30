package com.team3.rating.Database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static java.lang.Integer.getInteger;

public class RatingDataOperator {
    private MongoCollection<Document> collection;


    public RatingDataOperator() {
        /**
         * Connection to MongoDb
         */
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        MongoDatabase database = mongoClient.getDatabase("mydb");
        collection = database.getCollection("rating");
    }

    /**
     * Creates mongoDB entry
     */
    public void createRating(Document doc) {
        collection.insertOne(doc);
    }

    /**
     * Findes rating by criterion
     */
    public Integer findRating(Integer collectionId, Integer postId, Integer userId, String criterionName){
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
