package com.team3.rating.Controller;

import com.team3.rating.Database.RatingDataOperator;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rating")
public class Controller {
    private RatingDataOperator ratingDataOperator = new RatingDataOperator();

    @PostMapping("/")
    public HttpStatus ratePost(@RequestBody Document requestBody) {
        requestBody.remove("token");
        ratingDataOperator.createRating(requestBody);

        return HttpStatus.OK;
    }

    @GetMapping("collections/{collectionID}/posts/{postID}/users/{userID}/criterion/{criterionName}")
    public String getRatingByCriterion(@PathVariable String collectionID,
                                       @PathVariable String postID,
                                       @PathVariable String userID,
                                       @PathVariable String criterionName){
        return collectionID + "," + postID + "," +  userID;
    }


}
