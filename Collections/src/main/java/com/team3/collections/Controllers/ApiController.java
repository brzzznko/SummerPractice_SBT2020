package com.team3.collections.Controllers;

import com.team3.collections.Database.CollectionsDataOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
}
