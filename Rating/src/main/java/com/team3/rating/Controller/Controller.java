package com.team3.rating.Controller;

import com.mongodb.client.MongoCursor;
import com.team3.rating.Database.RatingDataOperator;
import com.team3.rating.Model.PermissionValidator;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("rating")
public class Controller {
    @Autowired
    private RatingDataOperator ratingDataOperator;

    @Autowired
    private PermissionValidator permissionValidator;

    @PostMapping("/")
    public ResponseEntity<String> ratePost(@RequestBody Document requestBody) {
        try {
            String currentToken = requestBody.getString("token");
            String collectionId = requestBody.getString("colection_id");

            boolean canRate = permissionValidator.havePermission(collectionId, currentToken, "rate");

            if (canRate) {
                requestBody.remove("token");
                ratingDataOperator.createRating(requestBody);
                calculateAverageRating(requestBody.toJson());
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
        Document reqBody = Document.parse(requestBody);
        Document doc = (Document)reqBody.get("rating");

        String collectionId = reqBody.getString("collection_id");
        String postId = reqBody.getString("post_id");

        Set<String> criterion = doc.keySet();
        MongoCursor<Document> cursor = ratingDataOperator.findAllRating(collectionId, postId);

        Integer countOfRatings;
        HashMap<String, Float> averageRating = new HashMap<>();

        countOfRatings = 0;
        try {
            if (cursor.hasNext()) {
                do {
                    Document cur = cursor.next();
                    for (String criterionName : criterion) {
                        averageRating.put(criterionName, averageRating.getOrDefault(criterionName, 0f) +
                                ((Document) cur.get("rating")).getInteger(criterionName));
                    }
                    countOfRatings++;
                } while (cursor.hasNext());
            }
            for (String criterionName : criterion){
                averageRating.put(criterionName, averageRating.get(criterionName) / countOfRatings);
            }
        } finally {
                cursor.close();
        }

        Float averageRatingByPost = 0f;
        for (String criterionName : criterion) {
            averageRatingByPost += averageRating.get(criterionName);
        }
        averageRatingByPost /= criterion.size();

        Document document = new Document("collection_id", collectionId)
                .append("post_id", postId)
                .append("average_rating", averageRatingByPost)
                .append("average_rating_by_criterion", averageRating);

        ratingDataOperator.createAverageRating(document);
    }


    /**
     * * Delete all post ratings and all post average rating
     *
     * @param token user access token
     * @param postId id of posts that we want to remove rating
     * @return http status code
     */
    @DeleteMapping("collections/posts/{postID}/token/{token}")
    public ResponseEntity<String> deleteAllPostRatings(@PathVariable("token") String token,
                                                       @PathVariable("postID") String postId) {
        try {
            boolean canDeleteAllPostRatings = permissionValidator.isPostOwner(token, postId);

            if (canDeleteAllPostRatings) {
                ratingDataOperator.deletePostRatings(postId);
                ratingDataOperator.deletePostAverageRatings(postId);
            }
            else {
                return new ResponseEntity<>("Not enough rights", HttpStatus.UNAUTHORIZED);
            }

            return new ResponseEntity<>("OK", HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong", HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Delete all post ratings and all post average rating
     *
     * @param postsList list of posts that we want to remove rating
     * @param token user access token
     * @return http status code
     */
    @DeleteMapping("collections/posts/token/{token}")
    public ResponseEntity<String> deleteAllpostsRatings(@RequestParam("postsList") List<String> postsList,
                                                        @PathVariable("token") String token) {
        try {
            StringBuilder response = new StringBuilder();

            for (String post : postsList) {
                boolean canDeleteAllPostsRatings = permissionValidator.isPostOwner(token, post);

                if (canDeleteAllPostsRatings) {
                    ratingDataOperator.deletePostRatings(post);
                    ratingDataOperator.deletePostAverageRatings(post);
                }
                else
                    response.append("/nCan't delete post: ").append(post);
            }

            return new ResponseEntity<>("OK" + response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get post average rating
     *
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

    /**
     * Delete all specific post ratings from specific collection
     *
     * @param collectionId id of collection
     * @param postId id of post
     * @param token user access token
     * @return Http Status code
     */
    @DeleteMapping("/collections/{collectionID}/posts/{postID}/token/{token}")
    public ResponseEntity<String> deletePostRatings(@PathVariable("collectionID") String collectionId,
                                                    @PathVariable("postID") String postId,
                                                    @PathVariable("token") String token) {

        try {
            boolean canDeleteRating = permissionValidator.isPostOwner(token, postId) ||
                    permissionValidator.havePermission(collectionId, token, "delete_post");

            if (canDeleteRating) {
                ratingDataOperator.deletePostRatingsFromCollection(collectionId, postId);
            }
            else {
                return new ResponseEntity<>("Not enough rights", HttpStatus.UNAUTHORIZED);
            }

            return new ResponseEntity<>("OK", HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/average/collections/{collectionID}/posts/{postID}/criterion/{criterionName}")
    public ResponseEntity<Document> getAveragePostRatingByCriterion(@PathVariable("collectionID") String collectionId,
                                                                    @PathVariable("postID") String postId,
                                                                    @PathVariable("criterionName") String criterionName){

        Integer rating = ratingDataOperator.getAveragePostRatingByCriterion(collectionId, postId, criterionName);
        if(rating == null) {
            return  new ResponseEntity<>(new Document("response", "Not Found"), HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(new Document(criterionName, rating), HttpStatus.OK);
        }
    }
}
