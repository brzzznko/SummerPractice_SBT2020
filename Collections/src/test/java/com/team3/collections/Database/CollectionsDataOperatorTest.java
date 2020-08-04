package com.team3.collections.Database;

import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class CollectionsDataOperatorTest {
    @Autowired
    private CollectionsDataOperator collectionsDataOperator;

    Document testDoc = new Document("collection_id", "55")
            .append("owner_id", 35)
            .append("name", "Груши")
            .append("description", "Сравнение")
            .append("posts", Arrays.asList(13, 886, 32))
            .append("criterion", Arrays.asList("Вкус", "Цена"));

    @Test
    @DisplayName("Insert Collection and find it")
    public void insertDocTest() {
        // Insert collection
        collectionsDataOperator.insertJson(testDoc);
        testDoc.remove("_id");

        String collectionId = testDoc.getString("collection_id");

        // Try to find it
        Document found = collectionsDataOperator.findCollection(collectionId);
        Assertions.assertEquals(testDoc, found);

        // Delete collection
        collectionsDataOperator.deleteCollection(collectionId);
    }

    @Test
    @DisplayName("Can't find not existing collection")
    public void findNotExistingCollectionTest() {
        String collectionId = "Not exist";
        Document found = collectionsDataOperator.findCollection(collectionId);
        Assertions.assertNull(found);
    }

    @Test
    @DisplayName("Delete collection after insert")
    public void deleteAfterInsertCollectionTest() {
        // Insert collection
        collectionsDataOperator.insertJson(testDoc);
        testDoc.remove("_id");
        // Delete collection
        String collectionId = testDoc.getString("collection_id");
        collectionsDataOperator.deleteCollection(collectionId);
        // Try to find
        Document found = collectionsDataOperator.findCollection(collectionId);
        Assertions.assertNull(found);
    }

    @Test
    @DisplayName("Delete not existing collection")
    public void deleteNotExistingCollectionTest() {
        // Delete collection
        String collectionId = testDoc.getString("collection_id");
        collectionsDataOperator.deleteCollection(collectionId);
        // Try to find
        Document found = collectionsDataOperator.findCollection(collectionId);
        Assertions.assertNull(found);
    }
}
