package com.team3.collections.Database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.pull;

@Component
public class CollectionsDataOperator {
    private final MongoCollection<Document> collection;

    public CollectionsDataOperator(@Value("${mongodb.host}") String host,
                                   @Value("${mongodb.port}") int port,
                                   @Value("${mongodb.databaseName}")  String databaseName,
                                   @Value("${mongodb.collectionName}")  String collectionName) {

        // Connection to MongoDb
        MongoClient mongoClient = new MongoClient( host, port );
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        collection = database.getCollection(collectionName);
    }

    /**
     * Creates mongoDB entry
     */
    public void insertJson(Document doc) {
        collection.insertOne(doc);
    }

    /**
     * Find collection by id
     * @param collectionId id of collection
     * @return found mongoDb entry without "_id" field
     */
    public Document findCollection(String collectionId) {
        Document found = collection.find(eq("collection_id", collectionId)).first();

        if (found != null) {
            found.remove("_id");
        }

        return found;
    }

    /**
     * Delete collection by ID
     * @param collectionId id of collection
     */
    public void deleteCollection(String collectionId) {
        collection.deleteOne(eq("collection_id", collectionId));
    }

    /**
     * Delete post from collection
     * @param collectionId id of collection
     * @param postId id of post
     */
    public void deletePostFromCollection(String collectionId, Integer postId) {
        Bson updateOperation = pull("posts", postId);
        collection.updateOne(eq("collection_id", collectionId), updateOperation);
    }

    public void deletePostFromAllCollection(Integer postId) {
        Bson updateOperation = pull("posts", postId);
        collection.updateMany(eq("posts", postId), updateOperation);
    }
}
