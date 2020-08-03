package com.team3.collections.Controllers;

import com.team3.collections.Database.CollectionsDataOperator;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @BeforeAll
    public static void fillDatabase(@Autowired CollectionsDataOperator collectionsDataOperator) {
        collectionsDataOperator.insertJson(new Document("collection_id", "25")
                .append("owner_id", 1)
                .append("name", "Макароны")
                .append("description", "Сравнение любимых макарон")
                .append("posts", Arrays.asList(11, 12, 13))
                .append("criterion", Arrays.asList("Вкус", "Цена"))
        );

        collectionsDataOperator.insertJson(new Document("collection_id", "1")
                .append("owner_id", 2)
                .append("name", "Яблоки")
                .append("description", "Сравнение")
                .append("posts", Arrays.asList(33, 66, 88))
                .append("criterion", Arrays.asList("Вкус", "Цена"))
        );

        collectionsDataOperator.insertJson(new Document("collection_id", "17")
                .append("owner_id", 3)
                .append("name", "Платки")
                .append("description", "Сравнение")
                .append("posts", Arrays.asList(1100, 1200, 1300))
                .append("criterion", Arrays.asList("Вкус", "Цена"))
        );
    }


    @Test
    @DisplayName("Success deletion")
    public void deleteCollection() throws URISyntaxException {
        // Good token
        String token = "1";
        int collectionId = 1;

        //Http request
        String url = "http://localhost:8081/collections/" + collectionId + "/token/" + token;
        URI uri = new URI(url);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);

        // Check Http status code
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Not enough rights to delete collection")
    public void notRightsToDeleteCollection() throws URISyntaxException {
        // Bad token
        String token = "sdfjka";
        int collectionId = 1;

        // Uri for request
        String url = "http://localhost:8081/collections/" + collectionId + "/token/" + token;
        URI uri = new URI(url);

        // Try to make request and check Http status code
        Assertions.assertThrows(HttpClientErrorException.Forbidden.class,
                () -> restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class));
    }
}
