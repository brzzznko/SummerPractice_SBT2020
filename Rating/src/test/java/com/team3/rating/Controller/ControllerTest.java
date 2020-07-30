package com.team3.rating.Controller;


import com.team3.rating.Database.RatingDataOperator;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class ControllerTest {

    RestTemplate restTemplate = new RestTemplate();
    RatingDataOperator ratingDataOperator = new RatingDataOperator();

    @Test
    @DisplayName("Rate post")
    public void rate() throws URISyntaxException {
        String url = "http://localhost:8080/rating/";
        URI uri = new URI(url);

        Document requestBody = new Document("collectionID", 21659)
                .append("postID", 46161651)
                .append("userID", 789816)
                .append("rating", new Document("Цена", 5).append("Качество", 9))
                .append("token", "ff48d4fv64d45df1v41db781t7g");

        ResponseEntity<String> result = restTemplate.postForEntity(uri, requestBody, String.class);

        // Check Http status code
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());

        // Check that data was written to the database
        String criterionName = "Цена";
        Integer expectedRating = ((Document)requestBody.get("rating")).getInteger(criterionName);

        Integer actualRating = ratingDataOperator.findRating(requestBody.getInteger("collectionID"), requestBody.getInteger("postID"),
                requestBody.getInteger("userID"), criterionName);

        Assertions.assertEquals(expectedRating, actualRating);

        // Remove test data from db
        ratingDataOperator.deleteData(requestBody.getInteger("collectionID"), requestBody.getInteger("postID"),
                requestBody.getInteger("userID"));
    }

    @Test
    @DisplayName("Getting test by criterion")
    public void getRatingByCriterion() throws URISyntaxException {
        String url = "http://localhost:8080/rating/collections/11/posts/12/users/3/criterion/55";

        URI uri = new URI(url);
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals("11,12,3", result.getBody());
    }
}
