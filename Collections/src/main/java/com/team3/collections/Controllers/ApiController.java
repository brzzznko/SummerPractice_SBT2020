package com.team3.collections.Controllers;

import com.team3.collections.Database.CollectionsDataOperator;
import com.team3.collections.Model.PermissionValidator;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URISyntaxException;
import java.util.List;


@RestController
@RequestMapping("collections")
public class ApiController {
    @Autowired
    CollectionsDataOperator collectionsDataOperator;

    @Autowired
    PermissionValidator permissionValidator;

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
        boolean canDeleteCollection = permissionValidator.havePermission(collectionId, token, "delete_collection");

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
        boolean canDeletePost = permissionValidator.havePermission(collectionId, token, "delete_post") ||
                permissionValidator.isPostOwner(token, postId);

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
        boolean canDeletePost = permissionValidator.isPostOwner(token, postId);

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

        boolean canCreateCollection = !permissionValidator.isGuestUser(currentToken);
        Integer userId = permissionValidator.getUserId(currentToken);

        if (canCreateCollection && userId != null) {
            bodyRequest.append("owner_id", userId);

            final String idCollection = java.util.UUID.randomUUID().toString().replace("-", ""); //Generating an ID
            bodyRequest.append("collection_id", idCollection);

            try {
                permissionValidator.setCollectionOwner(currentToken, idCollection);
            } catch (URISyntaxException | HttpClientErrorException.BadRequest e) {
                e.printStackTrace();
            }

            String idPost = bodyRequest.getString("first_post_id");
            bodyRequest.remove("first_post_id");
            bodyRequest.remove("token");

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
        boolean canReadCollection = permissionValidator.havePermission(collectionID, token, "read") ||
                permissionValidator.isPublicCollection(collectionID);

        if (canReadCollection) {
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
        Document doc = collectionsDataOperator.getListCollectionsUser(userID);

        boolean isCurrentUser = permissionValidator.isCurrentUser(token, userID);

        if (isCurrentUser) {
            if (doc == null) {
                return new ResponseEntity<>(
                        new Document("response", "The user doesn't have any collections"),
                        HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(doc, HttpStatus.OK);
            }
        } else {
            try {
                List<String> available = permissionValidator.collectionFilter(doc.getList("collections", String.class), token);
                return new ResponseEntity<>(new Document("collections", available), HttpStatus.OK);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(new Document("response", "Something went wrong"), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/")
    public ResponseEntity<String> updateCollection(@RequestBody Document bodyRequest) {
        String id = bodyRequest.getString("collection_id");
        String token = bodyRequest.getString("token");
        bodyRequest.remove("token");

        boolean canUpdateCollectionData = permissionValidator.havePermission(id, token, "write");

        if (canUpdateCollectionData) {
            if (collectionsDataOperator.updateCollection(bodyRequest, id)) {
                return new ResponseEntity<>("Successful collection update!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("You can't add a new criteria", HttpStatus.NOT_FOUND);
            }
        }
        else {
            return new ResponseEntity<>("Not enough rights", HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/posts")
    public ResponseEntity<String> addPostToCollection(@RequestBody Document bodyRequest) {
        String currentToken = bodyRequest.getString("token");
        String idCollection = bodyRequest.getString("collection_id");
        String postId = bodyRequest.getString("post_id");

        if(currentToken == null || idCollection == null || postId == null) {
            return new ResponseEntity<>("Bad request", HttpStatus.NOT_FOUND);
        }

        boolean canWrite = permissionValidator.havePermission(idCollection, currentToken, "write");

        if (canWrite) {
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
            boolean canDeletePost = permissionValidator.isPostOwner(token, post);
            if (canDeletePost) {
                collectionsDataOperator.deletePostFromAllCollection(post);
            }
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
