package com.team3.rating.Controller;

import com.team3.rating.Database.RatingDataOperator;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rating")
public class Controller {
    @Autowired
    private RatingDataOperator ratingDataOperator;

    @PostMapping("/")
    public HttpStatus ratePost(@RequestBody Document requestBody) {
        requestBody.remove("token");
        ratingDataOperator.createRating(requestBody);

        return HttpStatus.OK;
    }


    @GetMapping("collections/{collectionID}/posts/{postID}/users/{userID}/criterion/{criterionName}")
    public String getRatingByCriterion(@PathVariable Integer collectionID,
                                       @PathVariable Integer postID,
                                       @PathVariable Integer userID,
                                       @PathVariable String criterionName) {
        return collectionID + "," + postID + "," +  userID;
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


}
