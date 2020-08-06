package com.team3.rating.Database;

import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RatingDataOperatorTest {
    @Autowired
    private RatingDataOperator ratingDataOperator;

    @Test
    @DisplayName("Delete all post rating from collection")
    void deletePostRatingsFromCollection() {
        String COLLECTION_ID = "1";
        String POST_ID = "1";
        int user_id = 1;

        // Add test docs
        while (user_id < 3) {
            ratingDataOperator.createRating(new Document("collection_id", COLLECTION_ID)
                    .append("post_id", POST_ID)
                    .append("user_id", user_id)
                    .append("rating", new Document("Цена", 4).append("Качество", 9)));
            user_id += 1;
        }

        // Remove
        ratingDataOperator.deletePostRatingsFromCollection("1", "1");

        while(user_id > 1) {
            user_id -= 1;
            Assertions.assertNull(ratingDataOperator.findRating(COLLECTION_ID, POST_ID, user_id));
        }
    }
}