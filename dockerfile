FROM maven:3.8-openjdk-17 AS MAVEN_BUILD
EXPOSE 8081
WORKDIR java
COPY ./ ./
RUN mvn clean package -DskipTests



FROM akoskuczi/openjdk-11
COPY ./ ./
#COPY --from=MAVEN_BUILD truck-follower/target/truck-follower-0.0.1-SNAPSHOT.jar /truck-follower.jar

#CMD  ["java", "-jar", "truck-follower/target/truck-follower-0.0.1-SNAPSHOT.jar"]
