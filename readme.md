## Axa Section
### How to use this spring-boot project

- Install packages with `mvn package`
- Run `mvn spring-boot:run` for starting the application (or use your IDE)

Application (with the embedded H2 database) is ready to be used ! You can access the url below for testing it :

- Swagger UI : http://localhost:8080/swagger-ui.html
- H2 UI : http://localhost:8080/h2-console

> Don't forget to set the `JDBC URL` value as `jdbc:h2:mem:testdb` for H2 UI.



### Instructions

- download the zip file of this project
- create a repository in your own github named 'java-challenge'
- clone your repository in a folder on your machine
- extract the zip file in this folder
- commit and push

- Enhance the code in any ways you can see, you are free! Some possibilities:
  - Add tests
  - Change syntax
  - Protect controller end points
  - Add caching logic for database calls
  - Improve doc and comments
  - Fix any bug you might find
- Edit readme.md and add any comments. It can be about what you did, what you would have done if you had more time, etc.
- Send us the link of your repository.

#### Restrictions
- use java 8


#### What we will look for
- Readability of your code
- Documentation
- Comments in your code 
- Appropriate usage of spring boot
- Appropriate usage of packages
- Is the application running as expected
- No performance issues

#### Your experience in Java

Please let us know more about your Java experience in a few sentences. For example:

- I have 3 years experience in Java and I started to use Spring Boot from last year
- I'm a beginner and just recently learned Spring Boot
- I know Spring Boot very well and have been using it for many years


## *Developer's Section*

### **Introduction**

- Hello, I am Niraj Dighe. I have 5 years of experience in Java, I have used spring for most of my career 
  and used spring-boot from the last two years. 
- I have mostly worked on architecture and designing frameworks for adapting various types of 
  datastore's like elastic search, Postgres SQL, Orient DB.

### **Changes Done By Me.**
1. Modified the already existing MVC architecture. Created DTOs(Data Transfer Object) to communicate between 
   Controller and Service. Entity are generally part of persistence layer and should not be exposed. Additionally,
   using Entities in REST APIs may result in populating UI specific data in them.
2. Hence, Entities are only used by service to communicate with Repository, every other communication is
   done by DTO. 
3. Spring security is used to provide minimum viable permissions. There are two Roles in the system, user and admin.
   User only has access to Get APIs. While Admin has access to all the APIs.
> **username**: _user_   **password**: _user_
> 
> **username**: _admin_  **password**: _admin_
4. Caffeine Configuration has been used to implement in-memory caching.
5. Used Swagger with spring-fox-3 for documentation of APIs. The default URL for Swagger has been changed to:
   1. **http://localhost:8080/swagger-ui/index.html**
6. Isolated exception handling code in controller advice, this helps in service been free of redundant exception code.
7. Logging done through annotation using Slf4j specification and default logback implementation that is used by spring.
8. Implemented Unit tests and Integration tests for services using Mockito and JUnit5. 
9. Used Jacoco Plugin for code coverage. To trigger this plugin, run **mvn install**. The cover ratio has been provided
   in pom.xml and if the coverage is less than the cover ratio, the build fails and points out the packages that need 
   test cases.

### **Changes I would have done if I had more time.**
1. I Would have updated http 1.1 to http 2.
2. Would have integrated GraphQl instead of REST. GraphQL is faster, less verbose, and has integration with open api docs.
3. Protocol Buffers aka Protobuffs are serialization and deserialization tools. Generally, by default spring uses Jackson
   which has great functionality but is very slow. Protobuffs are fast and mainly used for communication between various 
   services.
4. Spring Actuator -> Would have integrated spring actuator. It has great in-built functionality for metrics.
5. I would have integrated sonarQube for code smells. It acts as all in one code analysis tool. 
6. I would have used hooks or pipelines to perform evaluation of code submitted, so it passes all the checks before it is merged.
7. I would have also integrated WebFlux reactive client.




