package com.team3.collections.Controllers;

import com.team3.collections.Database.CollectionsDataOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest
public class ApiControllerTest {
    @Autowired
    private CollectionsDataOperator collectionsDataOperator;

    private final RestTemplate restTemplate = new RestTemplate();


    @Test
    public void deleteCollection() throws URISyntaxException {
        String token = "sdafsda";
        int collectionId = 1;

        String url = "http://localhost:8081/collections/" + collectionId + "/token/" + token;
        URI uri = new URI(url);


        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);

        // Check Http status code
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
