package com.team3.collections.Controllers;

import com.team3.collections.Database.CollectionsDataOperator;
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

    /**
     * Delete collection by ID, if you have sufficient rights.
     * @param collectionId ID of collection
     * @param token access token of user session
     * @return HttpStatus with message
     */
    @DeleteMapping("/{collectionID}/token/{token}")
    public ResponseEntity<String> deleteCollection(@PathVariable("collectionID") String collectionId,
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

    /**
     * Delete post from collection, if you have sufficient rights.
     * @param collectionId id of collection
     * @param postId id of post
     * @param token access token of user session
     * @return HttpStatus with message
     */
    @DeleteMapping("/{collectionID}/post/{postID}/token/{token}")
    public ResponseEntity<String> deletePostFromCollection(@PathVariable("collectionID") String collectionId,
                                                           @PathVariable("postID") Integer postId,
                                                           @PathVariable("token") String token) {

        // !!! Need to do check with auth service
        boolean canDeletePost = token.equals("1");

        if (canDeletePost) {
            collectionsDataOperator.deletePostFromCollection(collectionId, postId);
        }
        else {
            return new ResponseEntity<>("Not enough rights", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
