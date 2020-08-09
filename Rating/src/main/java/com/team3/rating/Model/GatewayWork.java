package com.team3.rating.Model;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Component
@Scope("prototype")
public class GatewayWork extends Thread {
    @Value("${gateway.host}")
    private String gatewayHost;  //Host service Gateway

    @Value("${gateway.port}")
    private String gatewayPort;       //Port service Gateway

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Value("${service.name}")
    private String serviceName;

    @Value("${service.version}")
    private String serviceVersion;


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
        String url = gatewayHost + ":" + gatewayPort + "/gateway/ping/" + instanceId;

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
        String url = gatewayHost + ":" + gatewayPort + "/gateway/publish";
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
        String url = gatewayHost + ":" + gatewayPort + "/gateway/ready/" + instanceId;

        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
    }

    public ArrayList<Document> getNameApiFunctions() throws ClassNotFoundException {
            ArrayList<Document> arrayList = new ArrayList<>();

            Class clazz = Class.forName("com.team3.rating.Controller.Controller");

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
                        if(str.charAt(0) != '/'){
                            str = "/" + str;
                        }
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
            requestBody.append("address", gatewayHost);
            requestBody.append("port", port);
            requestBody.append("name_service", serviceName);
            requestBody.append("version_service", serviceVersion);
            return requestBody;
    }

    public void setGatewayHost(String gatewayHost) {
        this.gatewayHost = gatewayHost;
    }

    public void setGatewayPort(String gatewayPort) {
        this.gatewayPort = gatewayPort;
    }
}
