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


    @DeleteMapping("collections/posts/{postID}/token/{token}")
    public boolean deleteAllPostRatings(@PathVariable String token,
                                        @PathVariable Integer postID) {
        return true;
    }

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
