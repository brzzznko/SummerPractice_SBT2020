package com.team3.rating.Controller;

import com.mongodb.client.MongoCursor;
import com.team3.rating.Database.RatingDataOperator;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.*;

@RestController
@RequestMapping("rating")
public class Controller {
    @Autowired
    private RatingDataOperator ratingDataOperator;

    @PostMapping("/")
    public ResponseEntity<String> ratePost(@RequestBody Document requestBody) {
        try {
            String currentToken = requestBody.getString("token");
            if (true) {
                requestBody.remove("token");
                ratingDataOperator.createRating(requestBody);
                calculateAverageRating(requestBody.toJson());
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    @GetMapping("collections/{collectionID}/posts/{postID}/users/{userID}/criterion/{criterionName}")
    public ResponseEntity<Integer> getRatingByCriterion(@PathVariable String collectionID,
                                    @PathVariable String postID,
                                    @PathVariable String userID,
                                    @PathVariable String criterionName) {
        Integer rating;
        try {
            rating = ratingDataOperator.findRating(userID, collectionID, postID, criterionName);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(rating, HttpStatus.OK);
    }


    private void calculateAverageRating(String requestBody){


    /**
     * * Delete all post ratings and all post average rating
     * @param token user access token
     * @param postId id of posts that we want to remove rating
     * @return http status code
     */
    @DeleteMapping("collections/posts/{postID}/token/{token}")
    public ResponseEntity<String> deleteAllPostRatings(@PathVariable("token") String token,
                                                       @PathVariable("postID") String postId) {
        boolean canDeleteAllPostRatings = token.equals("1");
        if (canDeleteAllPostRatings) {
            ratingDataOperator.deletePostRatings(postId);
            ratingDataOperator.deletePostAverageRatings(postId);
        }
        else {
            return new ResponseEntity<>("Not enough rights", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    /**
     * Delete all post ratings and all post average rating
     * @param postsList list of posts that we want to remove rating
     * @param token user access token
     * @return http status code
     */
    @DeleteMapping("collections/posts/token/{token}")
    public ResponseEntity<String> deleteAllpostsRatings(@RequestParam("postsList") List<String> postsList,
                                                        @PathVariable("token") String token) {
        for(String post : postsList) {
            boolean canDeleteAllPostsAllRatings = token.equals("1");
            if (canDeleteAllPostsAllRatings) {
                ratingDataOperator.deletePostRatings(post);
                ratingDataOperator.deletePostAverageRatings(post);
            }
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    /**
     * Get post average rating
     * @param collectionId id collection in which the post
     * @param postId post id
     * @return json that contain average rating
     */
    @GetMapping("/average/collections/{collectionID}/posts/{postID}")
    public ResponseEntity<Document> getAveragePostRating(@PathVariable("collectionID") String collectionId,
                                                         @PathVariable("postID") String postId){
      
        Integer rating = ratingDataOperator.getAveragePostRating(collectionId, postId);
        if(rating == null) {
            return  new ResponseEntity<>(new Document("response", "Not Found"), HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(new Document("average_rating", rating), HttpStatus.OK);
        }
    }
}
