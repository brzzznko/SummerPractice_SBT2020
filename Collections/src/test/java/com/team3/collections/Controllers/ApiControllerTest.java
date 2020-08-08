package com.team3.collections.Controllers;

import com.team3.collections.Database.CollectionsDataOperator;
import com.team3.collections.Model.PermissionValidator;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

@SpringBootTest
public class ApiControllerTest {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String TEST_COLLECTION_ID = "1";
    private static final String DELETING_TEST_POST_ID = "33";
    private static String GOOD_TOKEN;
    private static final String BAD_TOKEN = "not_exist";

    @Value("${server.port}")
    private String PORT;


    @BeforeAll
    public static void createUser() throws URISyntaxException {
        PermissionValidator permissionValidator = new PermissionValidator();
        GOOD_TOKEN = permissionValidator.register("test", "test", "test");
    }

    @AfterAll
    public static void deleteUser() throws URISyntaxException {
        PermissionValidator permissionValidator = new PermissionValidator();
        permissionValidator.deleteUser(GOOD_TOKEN);
    }

    @BeforeEach
    public void fillDatabase(@Autowired CollectionsDataOperator collectionsDataOperator) {
        collectionsDataOperator.insertJson(new Document("collection_id", TEST_COLLECTION_ID)
                .append("owner_id", 2)
                .append("name", "Яблоки")
                .append("description", "Сравнение")
                .append("posts", Arrays.asList("66", "78880", DELETING_TEST_POST_ID, "88"))
                .append("criterion", Arrays.asList("Вкус", "Цена"))
        );
    }

    @AfterEach
    public void clearDatabase(@Autowired CollectionsDataOperator collectionsDataOperator) {
        collectionsDataOperator.deleteCollection(TEST_COLLECTION_ID);
    }

    @Test
    @DisplayName("Successfully deleting a collection")
    public void deleteCollection() throws URISyntaxException {
        //Http request
        String url = "http://localhost:" + PORT + "/collections/" + TEST_COLLECTION_ID + "/token/" + GOOD_TOKEN;
        URI uri = new URI(url);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);

        // Check Http status code
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Not enough rights to delete collection")
    public void notRightsToDeleteCollection() throws URISyntaxException {
        // Uri for request
        String url = "http://localhost:" + PORT + "/collections/" + TEST_COLLECTION_ID + "/token/" + BAD_TOKEN;
        URI uri = new URI(url);

        // Try to make request and check Http status code
        Assertions.assertThrows(HttpClientErrorException.Unauthorized.class,
                () -> restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class));
    }

    @Test
    @DisplayName("Successfully deleting post from collection")
    public void deletePostFromCollection() throws URISyntaxException {
        //Http request
        String url = "http://localhost:" + PORT + "/collections/" + TEST_COLLECTION_ID +
                "/post/" + DELETING_TEST_POST_ID + "/token/" + GOOD_TOKEN;
        URI uri = new URI(url);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);

        // Check Http status code
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Not enough rights to delete post from collection")
    public void notRightsToDeletePostFromCollection() throws URISyntaxException {
        // Uri for request
        String url = "http://localhost:" + PORT + "/collections/" + TEST_COLLECTION_ID +
                "/post/" + DELETING_TEST_POST_ID + "/token/" + BAD_TOKEN;
        URI uri = new URI(url);

        // Try to make request and check Http status code
        Assertions.assertThrows(HttpClientErrorException.Unauthorized.class,
                () -> restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class));
    }

    @Test
    @DisplayName("Successfully deleting post from ALL collection")
    void deletePostFromAllCollection() throws URISyntaxException {
        //Http request
        String url = "http://localhost:" + PORT + "/collections/post/" + DELETING_TEST_POST_ID + "/token/" + GOOD_TOKEN;
        URI uri = new URI(url);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);

        // Check Http status code
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Not enough rights to delete post from ALL collection")
    void notRightstoDeletePostFromAllCollection() throws URISyntaxException {
        // Uri for request
        String url = "http://localhost:" + PORT + "/collections/post/" + DELETING_TEST_POST_ID + "/token/" + BAD_TOKEN;
        URI uri = new URI(url);

        // Try to make request and check Http status code
        Assertions.assertThrows(HttpClientErrorException.Unauthorized.class,
                () -> restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class));
    }

    @Test
    @DisplayName("Get posts of collection. OK")
    void getPosts() throws URISyntaxException {
        // Uri for request
        String url = "http://localhost:" + PORT + "/collections/" + TEST_COLLECTION_ID + "/posts";
        URI uri = new URI(url);

        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        // Check Http status code
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Get posts of collection. Not Found (no collection)")
    void getPostsNoCollection() throws URISyntaxException {
        // Uri for request
        String url = "http://localhost:" + PORT + "/collections/" + TEST_COLLECTION_ID + "not_exist" + "/posts";
        URI uri = new URI(url);

        // Try to make request and check Http status code
        Assertions.assertThrows(HttpClientErrorException.NotFound.class,
                () -> restTemplate.getForEntity(uri, String.class));
    }
}
