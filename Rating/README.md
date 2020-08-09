# Rating microservice
Microservice is responsible for the operations with the rating: its creation, storage, updating, calculation of the average post ratings of the total and by criteria.

## API Documentation
https://borzzzenko.github.io/SummerPractice_SBT2020/Rating/index.html

## Installation
It is necessary to install [JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) version 8 or 11, [maven](https://maven.apache.org/install.html), [MongoDB](https://docs.mongodb.com/manual/administration/install-community/)

```bash
    git clone https://github.com/BorZzzenko/SummerPractice_SBT2020.git
    cd SummerPractice_SBT2020
    cd Rating
    mvn package -Dmaven.test.skip=true
    cd target
    java -jar rating-0.0.1-SNAPSHOT.jar
```
You can change app properties in \Rating\src\main\resources\application.properties

* Server properties:
```java
    server.port=8080
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
