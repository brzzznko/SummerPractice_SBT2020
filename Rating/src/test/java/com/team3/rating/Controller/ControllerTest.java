package com.team3.rating.Controller;


import com.team3.rating.Database.RatingDataOperator;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@SpringBootTest
public class ControllerTest {

    private RestTemplate restTemplate = new RestTemplate();

    private static final String TEST_COLLECTION_ID = "1";
    private static final Integer TEST_POST_ID = 33;
    private static final String GOOD_TOKEN = "1";
    private static final String BAD_TOKEN = "very_bad";

    @Value("${server.port}")
    private String PORT;

    @Autowired
    RatingDataOperator ratingDataOperator;

    @Test
    @DisplayName("Rate post")
    public void rate() throws URISyntaxException {
        String url = "http://localhost:" + PORT + "/rating/";
        URI uri = new URI(url);

        Document requestBody = new Document("collection_id", TEST_COLLECTION_ID)
                .append("post_id", TEST_POST_ID)
                .append("user_id", 1)
                .append("rating", new Document("Цена", 4).append("Качество", 9))
                .append("token", "ff48d4fv64d45df1v41db781t7g");

        ResponseEntity<String> result = restTemplate.postForEntity(uri, requestBody, String.class);

        // Check Http status code
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());

        // Check that data was written to the database
        String criterionName = "Цена";
        Integer expectedRating = ((Document)requestBody.get("rating")).getInteger(criterionName);

        Integer actualRating = ratingDataOperator.findRating(requestBody.getInteger("user_id"),
                TEST_COLLECTION_ID, TEST_POST_ID, criterionName);

        Assertions.assertEquals(expectedRating, actualRating);

        // Remove test data from db
        ratingDataOperator.deleteData(TEST_COLLECTION_ID, TEST_POST_ID, requestBody.getInteger("user_id"));
    }

    @Test
    @DisplayName("Delete post ratings from collection")
    void deletePostRatings() throws URISyntaxException {
        // Uri for request
        String url = "http://localhost:" + PORT + "/rating/collections/" + TEST_COLLECTION_ID +
                "/posts/" + TEST_POST_ID + "/token/" + GOOD_TOKEN;
        URI uri = new URI(url);

        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);

        // Check Http status code
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Not rights to delete post ratings from collection")
    void deletePostRatingsNotAccess() throws URISyntaxException {
        // Uri for request
        String url = "http://localhost:" + PORT + "/rating/collections/" + TEST_COLLECTION_ID +
                "/posts/" + TEST_POST_ID + "/token/" + BAD_TOKEN;
        URI uri = new URI(url);

        // Try to make request and check Http status code
        Assertions.assertThrows(HttpClientErrorException.Unauthorized.class,
                () -> restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class));
    }
}
