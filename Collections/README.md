# Collection microservice
Microservice is responsible for operations with collections: creating, storing, deleting, updating data.

## API Documentation
https://borzzzenko.github.io/SummerPractice_SBT2020/Collections/index.html

## Installation
It is necessary to install [JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) version 8 or 11, [maven](https://maven.apache.org/install.html), [MongoDB](https://docs.mongodb.com/manual/administration/install-community/)

* Download "collections-version.jar" and "collection_app.properties" files from releases.
* Open config file "collection_app.properties" and set your properties.
    * Server properties:
```java
    server.port=8081
    server.host=localhost
```
    * MongoDB properties.
```java
    mongodb.host=localhost
    mongodb.port=27017
```
    * Gateway service properties.
```java
    gateway.host=http://localhost
    gateway.port=8085
```
* Launch app:
```bash
    java -jar collections-0.0.1-SNAPSHOT.jar --spring.config.location=./collections_app.properties
```

## Buld guide
```bash
    git clone https://github.com/BorZzzenko/SummerPractice_SBT2020.git
    cd SummerPractice_SBT2020
    cd Collections
    mvn package -Dmaven.test.skip=true
    cd target
    java -jar collections-0.0.1-SNAPSHOT.jar
```
You can change app properties in \Collections\src\main\resources\application.properties


