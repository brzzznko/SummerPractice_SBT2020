package com.team3.collections.Controllers;

import com.team3.collections.Database.CollectionsDataOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.bson.Document;

@RestController
@RequestMapping("collections")
public class ApiController {
    @Autowired
    CollectionsDataOperator collectionsDataOperator;

    @DeleteMapping("/{collectionID}/token/{token}")
    public HttpStatus deleteCollection(@PathVariable("collectionID") Integer collectionId,
                                       @PathVariable("token") String token) {

        // Сделать проверку в сервисе доступа
        boolean canDeleteCollection = true;

        if (canDeleteCollection) {
            collectionsDataOperator.deleteCollection(collectionId);
        }
        else {
            return HttpStatus.FORBIDDEN;
        }

        collectionsDataOperator.deleteCollection(collectionId);

        return HttpStatus.OK;
    }

    @PostMapping("/creat")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpStatus createNewCollection(@RequestBody Document bodyRequest){
        final String id = java.util.UUID.randomUUID().toString();           //Generating an ID
        bodyRequest.append("collection_id", id);
        collectionsDataOperator.insertJson(bodyRequest);
        return HttpStatus.OK;
    }

    @GetMapping("/get/{collectionID}")
    public ResponseEntity<Document> getCollectionData(@PathVariable String collectionID){
        Document doc = collectionsDataOperator.getCollection(collectionID);
        if(doc == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(doc, HttpStatus.OK);
        }
    }

}
