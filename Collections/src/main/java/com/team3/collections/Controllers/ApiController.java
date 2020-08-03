package com.team3.collections.Controllers;

import com.team3.collections.Database.CollectionsDataOperator;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("collections")
public class ApiController {
    @Autowired
    CollectionsDataOperator collectionsDataOperator;

    @DeleteMapping("/{collectionID}/token/{token}")
    public ResponseEntity<String> deleteCollection(@PathVariable("collectionID") Integer collectionId,
                                                     @PathVariable("token") String token) {

        // !!! Need to do check with auth service
        boolean canDeleteCollection = token.equals("1");

        if (canDeleteCollection) {
            collectionsDataOperator.deleteCollection(collectionId);
        }
        else {
            return new ResponseEntity<>("Not enough rights", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
