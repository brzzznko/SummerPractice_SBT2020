package com.team3.rating.Controller;

import com.team3.rating.Database.RatingDataOperator;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @GetMapping("collections/{collectionID}/posts/{postID}/users/{userID}/criterion/{criterionName}")
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
     * * Delete all post ratings and all post average rating
     * @param token user access token
     * @param postId id of posts that we want to remove rating
     * @return http status code
     */
    @DeleteMapping("collections/posts/{postID}/token/{token}")
    public ResponseEntity<String> deleteAllPostRatings(@PathVariable("token") String token,
                                                       @PathVariable("postID") Integer postId) {
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
    public ResponseEntity<String> deleteAllpostsRatings(@RequestParam("postsList") List<Integer> postsList,
                                                        @PathVariable("token") String token) {
        for(Integer post : postsList) {
            boolean canDeleteAllPostsAllRatings = token.equals("1");
            if (canDeleteAllPostsAllRatings) {
                ratingDataOperator.deletePostRatings(post);
                ratingDataOperator.deletePostAverageRatings(post);
            }
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
