package com.team3.collections.Model;

import org.bson.Document;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PermissionValidator {
    private String host = "localhost";
    private String port = "5000";

    private final String HTTP = "http://";

    private final RestTemplate restTemplate = new RestTemplate();

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Get user id by token
     *
     * @param token user access token
     * @return user id
     */
    public Integer getUserId(String token) {
        String url = HTTP + host + ":" + port + "/user/info/" + token;
        URI uri;

        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }

        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        if (result.getStatusCode().equals(HttpStatus.OK)) {
            Document response = Document.parse(result.getBody());
            return response.getInteger("user_id");
        }

        return null;
    }

    /**
     * Get user role id in collection
     *
     * @param collectionId id of collection
     * @param userId user id
     * @return role id
     * @throws URISyntaxException exception by bad uri
     */
    public Integer getUserRoleInCollection(String collectionId, Integer userId) throws URISyntaxException {
        String url = HTTP + host + ":" + port + "/permissions/userRole/" + userId;
        URI uri = new URI(url);

        ResponseEntity<Document[]> result = restTemplate.getForEntity(uri, Document[].class);

        if (result.getStatusCode().equals(HttpStatus.OK)) {
            Document[] response = result.getBody();
            if(response != null) {
                for (Document doc : response) {
                    if(doc.getString("collection_id").equals(collectionId)) {
                        return doc.getInteger("role_id");
                    }
                }
            }
        }

        return null;
    }

    /**
     * Check that user have permission
     *
     * @param collectionId id of collection
     * @param token user access token
     * @param permissionName permission to check
     * @return true is user have permission
     */
    public boolean havePermission(String collectionId, String token, String permissionName) {
        Integer currentUser;
        Integer roleInCollection;

        try {
            currentUser = getUserId(token);
            roleInCollection = getUserRoleInCollection(collectionId, currentUser);

            if (currentUser == null || roleInCollection == null) {
                return false;
            }

            String url = HTTP + host + ":" + port + "/permissions/role/" + roleInCollection;
            URI uri = new URI(url);

            ResponseEntity<String[]> result = restTemplate.getForEntity(uri, String[].class);

            if (result.getStatusCode().equals(HttpStatus.OK)) {
                String[] response = result.getBody();
                if(response != null) {
                    for (String permission : response) {
                        if(permission.equals(permissionName)) {
                            return true;
                        }
                    }
                }
            }

            return false;

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return  false;
        }
    }

    /**
     * Check that current user is post owner
     *
     * @param token user access token
     * @param postId post id
     * @return true if user is post owner
     */
    public boolean isPostOwner(String token, String postId) {
        try {
            String url = HTTP + host + ":" + port + "/permissions/getPostOwner/" + postId;
            URI uri = new URI(url);

            ResponseEntity<Integer> result = restTemplate.getForEntity(uri, Integer.class);

            if (result.getStatusCode().equals(HttpStatus.OK)) {
                Integer postOwner = result.getBody();
                Integer userId = getUserId(token);
                return postOwner.equals(userId);
            }

            return false;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isGuestUser(String token) {
        return true;
    }

    public void setCollectionOwner(String token, String collectionId) throws URISyntaxException {
        String url = HTTP + host + ":" + port + "/permissions/userRole/setCollectionOwner/";
        URI uri= new URI(url);

        Document requestBody = new Document("token", token).append("collectionId", collectionId);

        ResponseEntity<String> result = restTemplate.postForEntity(uri, requestBody, String.class);
    }

    public boolean isCurrentUser (String token, Integer userId) {
        return userId.equals(getUserId(token));
    }

    public List<String> collectionFilter(List<String> allUserCollections, String token) throws URISyntaxException {
        List<String> allCollectionsCopy = new ArrayList<>(allUserCollections);
        List<String> available = new ArrayList<>(allUserCollections);

        for (String collection : allCollectionsCopy) {
            if (havePermission(collection, token, "read")) {
                available.add(collection);
                allUserCollections.remove(collection);
            }
        }

        String url = HTTP + host + ":" + port + "/permissions/getPublicCollection/";
        URI uri= new URI(url);

        ResponseEntity<String[]> result = restTemplate.postForEntity(uri, allUserCollections, String[].class);

        if (result.getStatusCode().equals(HttpStatus.OK)) {
            available.addAll(Arrays.asList(result.getBody()));
        }

        return available;
    }

    public String register(String login, String password, String name) throws URISyntaxException {
        String url = HTTP + host + ":" + port + "/user/";
        URI uri= new URI(url);

        Document requestBody = new Document("login", login).append("password", password).append("name", name);

        ResponseEntity<String> result = restTemplate.postForEntity(uri, requestBody, String.class);

        if (result.getStatusCode().equals(HttpStatus.OK)) {
            return result.getBody();
        }

        return null;
    }

    public void deleteUser(String token) throws URISyntaxException {
        String url = HTTP + host + ":" + port + "/user/delete/" + token;
        URI uri= new URI(url);

        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);
    }
}
