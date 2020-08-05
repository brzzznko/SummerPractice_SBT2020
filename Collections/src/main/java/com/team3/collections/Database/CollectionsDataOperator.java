package com.team3.collections.Database;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.print.Doc;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
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
     * Get collection data by ID
     */
    public Document getCollection(String idCollection){
        return collection.find(eq("collection_id", idCollection)).first();
    }

    /**
     * Getting a list of collections owned by a user
     */
    public Document getListCollectionsUser(String idUser){
        ArrayList<String> listId = new ArrayList<>();
        for (Document cur : collection.find(eq("owner_id", idUser))){
            listId.add(cur.getString("collection_id"));
        }
        return new Document("collections", listId);
    }

    /**
     * Edit data collection
     * */
    public void updateCollection(Document doc, String ID){
        deleteCollection(ID);
        insertJson(doc);
    }

    /**
     * Adding a post to a collection
     * */
    public void addPost(String idCollection, String idPost){
        UpdateResult updateResult = collection.updateOne(
                Filters.eq("collection_id", idCollection),
                new BsonDocument("$push", new BsonDocument("posts", new BsonString(idPost)))
        );

     * Delete post from collection
     * @param collectionId id of collection
     * @param postId id of post
     */
    public void deletePostFromCollection(String collectionId, Integer postId) {
        Bson updateOperation = pull("posts", postId);
        collection.updateOne(eq("collection_id", collectionId), updateOperation);
    }

    /**
     * Delete post from all collections
     * @param postId id of post to remove
     */
    public void deletePostFromAllCollection(Integer postId) {
        Bson updateOperation = pull("posts", postId);
        collection.updateMany(eq("posts", postId), updateOperation);
    }

    /**
     * Get posts in collection
     * @param collectionId id of collection
     * @return List of posts IDs
     */
    public ArrayList<Integer> getPosts(String collectionId) {
        // Try to find collection
        Document found = collection.find(eq("collection_id", collectionId)).first();
        // If no collection, return null
        if(found == null)
            return null;

        // Return posts list
        return new ArrayList<>(found.getList("posts", Integer.class));
    }
}
