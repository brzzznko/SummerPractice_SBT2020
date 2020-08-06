# Collection microservice
Microservice is responsible for operations with collections: creating, storing, deleting, updating data.

## API Documentation
https://borzzzenko.github.io/SummerPractice_SBT2020/Collections/index.html

## Installation
It is necessary to install [JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) version 8 or 11, [maven](https://maven.apache.org/install.html), [MongoDB](https://www.mongodb.com/try/download/community)
```bash
    git clone https://github.com/BorZzzenko/SummerPractice_SBT2020.git
    cd SummerPractice_SBT2020
    cd Collections
    mvn package -Dmaven.test.skip=true
    cd target
    java -jar collections-0.0.1-SNAPSHOT.jar
```
If you need to run app with specific port. You can change it in \Rating\src\main\resources\application.properties. Just change field server.port:
```java
    server.port=8080
```
