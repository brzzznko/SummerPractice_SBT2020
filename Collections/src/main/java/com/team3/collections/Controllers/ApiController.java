package com.team3.collections.Controllers;

import com.team3.collections.Database.CollectionsDataOperator;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("collections")
public class ApiController {
    @Autowired
    CollectionsDataOperator collectionsDataOperator;



    /**
     * Delete collection by ID, if you have sufficient rights.
     *
     * @param collectionId ID of collection
     * @param token        access token of user session
     * @return HttpStatus with message
     */
    @DeleteMapping("/{collectionID}/token/{token}")
    public ResponseEntity<String> deleteCollection(@PathVariable("collectionID") String collectionId,
                                                   @PathVariable("token") String token) {
        // !!! Need to do check with auth service
        boolean canDeleteCollection = token.equals("1");

        if (canDeleteCollection) {
            collectionsDataOperator.deleteCollection(collectionId);
        } else {
            return new ResponseEntity<>("Not enough rights", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    /**
     * Delete post from collection, if you have sufficient rights.
     *
     * @param collectionId id of collection
     * @param postId       id of post
     * @param token        access token of user session
     * @return HttpStatus with message
     */
    @DeleteMapping("/{collectionID}/post/{postID}/token/{token}")
    public ResponseEntity<String> deletePostFromCollection(@PathVariable("collectionID") String collectionId,
                                                           @PathVariable("postID") String postId,
                                                           @PathVariable("token") String token) {
        // !!! Need to do check with auth service
        boolean canDeletePost = token.equals("1");

        if (canDeletePost) {
            collectionsDataOperator.deletePostFromCollection(collectionId, postId);
        } else {
            return new ResponseEntity<>("Not enough rights", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @DeleteMapping("/post/{postID}/token/{token}")
    public ResponseEntity<String> deletePostFromAllCollection(@PathVariable("postID") String postId,
                                                              @PathVariable("token") String token) {
        // !!! Need to do check with auth service
        boolean canDeletePost = token.equals("1");

        if (canDeletePost) {
            collectionsDataOperator.deletePostFromAllCollection(postId);
        } else {
            return new ResponseEntity<>("Not enough rights", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("/{collectionID}/posts")
    public ResponseEntity<Document> getPosts(@PathVariable("collectionID") String collectionId) {
        List<String> posts = collectionsDataOperator.getPosts(collectionId);

        if (posts == null) {
            return new ResponseEntity<>(new Document("response", "Collection not found"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new Document("posts", posts), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<String> createNewCollection(@RequestBody Document bodyRequest) {
        String currentToken = bodyRequest.getString("token");
        if (true) {
            bodyRequest.remove("token");

            final String idCollection = java.util.UUID.randomUUID().toString(); //Generating an ID
            bodyRequest.append("collection_id", idCollection);

            String idPost = bodyRequest.getString("first_post_id");
            bodyRequest.remove("first_post_id");

            collectionsDataOperator.insertJson(bodyRequest);        //Add collection
            collectionsDataOperator.addPost(idCollection, idPost);  //Add post to collection
        } else {
            return new ResponseEntity<>("Not enough rigths", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("The collection is created", HttpStatus.CREATED);
    }

    @GetMapping("/{collectionID}/token/{token}")
    public ResponseEntity<Document> getCollectionData(@PathVariable("collectionID") String collectionID,
                                                      @PathVariable("token") String token) {
        String currentToken = token;
        if (true) {
            Document doc = collectionsDataOperator.getCollection(collectionID);
            if (doc == null) {
                return new ResponseEntity<>(new Document("response", "Collection not found"), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(doc, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new Document("response", "Not enough rigths"), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/users/{userID}/token/{token}")
    public ResponseEntity<Document> getCollectionsUser(@PathVariable("userID") Integer userID,
                                                       @PathVariable("token") String token) {
        String currentToken = token;
        if (true) {
            Document doc = collectionsDataOperator.getListCollectionsUser(userID);
            if (doc == null) {
                return new ResponseEntity<>(
                        new Document("response", "The user doesn't have any collections"),
                        HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(doc, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new Document("response", "Not enough rigths"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/")
    public ResponseEntity<String> updateCollection(@RequestBody Document bodyRequest) {
        String id = bodyRequest.getString("collection_id");
        if (collectionsDataOperator.updateCollection(bodyRequest, id) != true) {
            return new ResponseEntity<>("You can't add a new criteria", HttpStatus.NOT_ACCEPTABLE);
        } else {
            return new ResponseEntity<>("Successful collection update!", HttpStatus.OK);
        }
    }

    @PutMapping("/posts")
    public ResponseEntity<String> addPostToCollection(@RequestBody Document bodyRequest) {
        String currentToken = bodyRequest.getString("token");
        if (true) {
            String idCollection = bodyRequest.getString("collection_id");
            if (idCollection == null) {
                return new ResponseEntity<>("Not found collection ID", HttpStatus.NOT_FOUND);
            }
            collectionsDataOperator.addPost(idCollection, bodyRequest.getString("post_id"));
            return new ResponseEntity<>("Post added to the collection.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    "Not enough rights to add a post to the collection", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/posts/token/{token}")
    public ResponseEntity<String> deleteListPostsFromCollections(@RequestParam("postsList") List<String> postsList,
                                                                 @PathVariable("token") String token) {
        for (String post : postsList) {
            boolean canDeleteListPosts = token.equals("1");
            if (canDeleteListPosts) {
                collectionsDataOperator.deletePostFromAllCollection(post);
            }
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
