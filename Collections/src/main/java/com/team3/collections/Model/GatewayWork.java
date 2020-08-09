package com.team3.collections.Model;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Component
@Scope("prototype")
public class GatewayWork extends Thread {
    private String host = "localhost";  //Host service Gateway
    private String port = "8085";       //Port service Gateway
    private final String HTTP = "http://";

    private String instanceId;
    private int pingInterval;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run() {

        while (true) {
            try {
                registration();             //To register for the service
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                ready();
            } catch (HttpServerErrorException e) {
                e.printStackTrace();
            }

            try {
                ping();                 //Ping
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void ping() throws InterruptedException {
        String url = HTTP + host + ":" + port + "/gateway/ping/" + instanceId;

        while (true) {
            ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);

            //If an instance with this ID has not published the API
            if (!(result.getStatusCode().equals(HttpStatus.OK)))
                return; //Then go to publishing

            Thread.sleep(pingInterval);
        }
    }

    public void registration() throws UnsupportedEncodingException, ClassNotFoundException {
        //Creating a request address
        String url = HTTP + host + ":" + port + "/gateway/publish";
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //Add address, port, name_service, version_service
        Document requestBody = readConfigService();

        //Add api
        requestBody.append("api", getNameApiFunctions());

        //Registering the service
        ResponseEntity<Document> result = restTemplate.postForEntity(uri, requestBody, Document.class);
        instanceId = result.getBody().getString("instance_id");
        pingInterval = (int) result.getBody().getInteger("ping_interval");

    }

    public void ready() {
        String url = HTTP + host + ":" + port + "/gateway/ready/" + instanceId;

        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
    }

    public ArrayList<Document> getNameApiFunctions() throws ClassNotFoundException {
            ArrayList<Document> arrayList = new ArrayList<>();

            Class clazz = Class.forName("com.team3.collections.Controllers.ApiController");

            String rm = "";
            for(Annotation an : clazz.getAnnotations()){
                rm = an.toString();
                int ix = rm.indexOf("RequestMapping");
                if(ix != -1){
                    rm = rm.substring(ix+"RequestMapping".length());
                    int first = rm.indexOf("value={") + "value={".length() + 1;
                    rm = rm.substring(first);
                    int last = rm.indexOf("\"},");
                    rm = rm.substring(0,last);
                    break;
                }
            }
            rm = "/" + rm;

            for(Method method :clazz.getMethods()){
                for(Annotation annotation :method.getDeclaredAnnotations()){

                    String str = annotation.toString();
                    int first = annotation.toString().indexOf("value={") + "value={".length() + 1;
                    if(first != -1) {
                        str = str.substring(first);
                        int last = str.indexOf("\"},");
                        if(last == -1)
                            continue;
                        str = str.substring(0,last);
                        arrayList.add(new Document("path", rm+str).append("api_version", "v1"));
                    }
                }
            }

            Set<Document> foo = new HashSet<Document>(arrayList);
            ArrayList<Document> mainList = new ArrayList<Document>();
            mainList.addAll(foo);

            return mainList;
    }

    public Document readConfigService() {
            Document requestBody = new Document();
            requestBody.append("address", "http://localhost");
            requestBody.append("port", "8081");
            requestBody.append("name_service", "CONTENT MANAGEMENT");
            requestBody.append("version_service", "0.1.0beta");
            return requestBody;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }


}
