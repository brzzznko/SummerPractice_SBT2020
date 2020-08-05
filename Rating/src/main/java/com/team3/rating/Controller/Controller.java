package com.team3.rating.Controller;

import com.team3.rating.Database.RatingDataOperator;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rating")
public class Controller {
    @Autowired
    private RatingDataOperator ratingDataOperator;

    @PostMapping("/")
    public HttpStatus ratePost(@RequestBody Document requestBody) {
        try {
            String currentToken = requestBody.getString("token");
            if (true) {
                requestBody.remove("token");
                ratingDataOperator.createRating(requestBody);
            } else {
                return HttpStatus.UNAUTHORIZED;
            }
        } catch (Exception ex) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }


    @GetMapping("/collections/{collectionID}/posts/{postID}/users/{userID}/criterion/{criterionName}")
    public Integer getRatingByCriterion(@PathVariable Integer collectionID,
                                    @PathVariable Integer postID,
                                    @PathVariable Integer userID,
                                    @PathVariable String criterionName) {

        return ratingDataOperator.findRating(userID, collectionID, postID, criterionName);
    }


    @GetMapping("average/collections/{collectionID}/posts/{postID}/criterion/{criterionName}")
    public String getAverageRatingByCriterion(@PathVariable Integer collectionID,
                                              @PathVariable Integer postID,
                                              @PathVariable String criterionName) {
        return new Document("цена:" , 5).toJson();
    }

    /**
     * Delete all specific post ratings from specific collection
     * @param collectionId id of collection
     * @param postId id of post
     * @param token user access token
     * @return Http Status code
     */
    @DeleteMapping("/collections/{collectionID}/posts/{postID}/token/{token}")
    public ResponseEntity<String> deletePostRatings(@PathVariable("collectionID") String collectionId,
                                                    @PathVariable("postID") Integer postId,
                                                    @PathVariable("token") String token) {

        // !!! Need to do check with auth service
        boolean canDeleteRating = token.equals("1");

        if (canDeleteRating) {
            ratingDataOperator.deletePostRatingsFromCollection(collectionId, postId);
        }
        else {
            return new ResponseEntity<>("Not enough rights", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


}
