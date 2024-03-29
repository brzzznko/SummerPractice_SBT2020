package com.team3.collections.Database;

import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class CollectionsDataOperatorTest {
    @Autowired
    private CollectionsDataOperator collectionsDataOperator;

    Document testDoc = new Document("collection_id", "55")
            .append("owner_id", 35)
            .append("name", "Груши")
            .append("description", "Сравнение")
            .append("posts", Arrays.asList("13", "886", "32"))
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

    @Test
    @DisplayName("Delete post from collection")
    public void deletePostFromCollection() {
        // Add test doc
        Document testPostDeleting = new Document("collection_id", "56")
                .append("owner_id", 35)
                .append("name", "Груши")
                .append("description", "Сравнение")
                .append("posts", Arrays.asList("13", "886", "32"))
                .append("criterion", Arrays.asList("Вкус", "Цена"));

        collectionsDataOperator.insertJson(testPostDeleting);

        String collectionId = testPostDeleting.getString("collection_id");

        // Get posts list
        List<String> posts = new ArrayList<>(testDoc.getList("posts", String.class));

        // Remove post
        int indexForRemove = 0;
        collectionsDataOperator.deletePostFromCollection(collectionId, posts.get(indexForRemove));
        posts.remove(indexForRemove);

        // Check that post was removed
        Document collection = collectionsDataOperator.findCollection(collectionId);
        List<String> postsAfter = collection.getList("posts", String.class);

        Assertions.assertEquals(posts, postsAfter);

        // Delete test doc
        collectionsDataOperator.deleteCollection(collectionId);
    }

    @Test
    @DisplayName("Delete not existing post from collection")
    public void deleteNotExistingPostFromCollection() {
        // Add test doc
        Document testPostDeleting = new Document("collection_id", "56")
                .append("owner_id", 35)
                .append("name", "Груши")
                .append("description", "Сравнение")
                .append("posts", Arrays.asList("13", "886", "32"))
                .append("criterion", Arrays.asList("Вкус", "Цена"));

        collectionsDataOperator.insertJson(testPostDeleting);

        String collectionId = testPostDeleting.getString("collection_id");

        // Get posts list
        List<String> posts = new ArrayList<>(testDoc.getList("posts", String.class));

        // Remove post
        String postForRemove = "0";
        collectionsDataOperator.deletePostFromCollection(collectionId, postForRemove);

        // Check that post was removed
        Document collection = collectionsDataOperator.findCollection(collectionId);
        List<String> postsAfter = collection.getList("posts", String.class);

        Assertions.assertEquals(posts, postsAfter);

        // Delete test doc
        collectionsDataOperator.deleteCollection(collectionId);
    }

    @Test
    @DisplayName("Delete post from all collection")
    void deletePostFromAllCollection() {
        String postId = "1";
        String colletionId = "56";
        List<String> posts = Arrays.asList("110", postId, "13", "886", "32");

        Document testPostDeleting = new Document("collection_id", colletionId)
                .append("owner_id", 35)
                .append("name", "Груши")
                .append("description", "Сравнение")
                .append("posts", posts)
                .append("criterion", Arrays.asList("Вкус", "Цена"));

        // Insert 3 test documents
        for (int i = 0; i < 3; i++) {
            testPostDeleting.remove("_id");
            collectionsDataOperator.insertJson(testPostDeleting);
            colletionId += 1;
            testPostDeleting.replace("collection_id", colletionId);
        }

        // Delete post from all collections
        collectionsDataOperator.deletePostFromAllCollection(postId);

        // Check that post was deleted from all collections
        for (int i = 0; i < 3; i++) {
            colletionId = colletionId.substring(0, colletionId.length() - 1);

            // check
            Document afterDeleting = collectionsDataOperator.findCollection(colletionId);
            List<String> postsAfter = afterDeleting.getList("posts", String.class);
            Assertions.assertNotEquals(posts, postsAfter);

            // Delete collection
            collectionsDataOperator.deleteCollection(colletionId);
        }
    }

    @Test
    @DisplayName("Get posts of collection")
    void getPosts() {
        // Add test doc
        Document doc = new Document("collection_id", "56")
                .append("owner_id", 35)
                .append("name", "Груши")
                .append("description", "Сравнение")
                .append("posts", Arrays.asList("13", "886", "32"))
                .append("criterion", Arrays.asList("Вкус", "Цена"));
        collectionsDataOperator.insertJson(doc);

        String collectionId = doc.getString("collection_id");

        // Get posts list
        List<String> posts = new ArrayList<>(doc.getList("posts", String.class));
        List<String> postsTest = collectionsDataOperator.getPosts(collectionId);

        Assertions.assertEquals(posts, postsTest);

        // Delete test doc
        collectionsDataOperator.deleteCollection(collectionId);
    }

    @Test
    @DisplayName("Get posts of collection, that has no posts")
    void getZeroPosts() {
        // Add test doc
        Document doc = new Document("collection_id", "0001")
                .append("owner_id", 35)
                .append("name", "Груши")
                .append("description", "Сравнение")
                .append("posts", Collections.emptyList())
                .append("criterion", Arrays.asList("Вкус", "Цена"));
        collectionsDataOperator.insertJson(doc);

        String collectionId = doc.getString("collection_id");

        // Get posts list
        List<String> posts = new ArrayList<>(doc.getList("posts", String.class));
        List<String> postsTest = collectionsDataOperator.getPosts(collectionId);

        Assertions.assertEquals(posts, postsTest);

        // Delete test doc
        collectionsDataOperator.deleteCollection(collectionId);
    }

    @Test
    @DisplayName("Get posts of not existing collection, return null")
    void getPostsFromNotExistingCollection() {
        // Get posts list
        String collectionId = "not_exist";
        List<String> postsTest = collectionsDataOperator.getPosts(collectionId);

        Assertions.assertNull(postsTest);

        // Delete test doc
        collectionsDataOperator.deleteCollection(collectionId);
    }
}
