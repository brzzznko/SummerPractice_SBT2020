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

@Component
public class CollectionsDataOperator {
    private final MongoCollection<Document> collection;

    public CollectionsDataOperator(@Value("${mongodb.host}") String host,
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
    public void insertJson(Document doc) {
        collection.insertOne(doc);
    }

    /**
     * Delete collection by ID
     * @param collectionId
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
    }
}
