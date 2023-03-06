FROM gradle:7.3-jdk17 as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew service:buildNeeded

FROM openjdk:17-jdk-slim
EXPOSE 8080
RUN mkdir /app
COPY --from=builder /home/gradle/src/service/build/libs/*.jar /app/spring-boot-application.jar
ENTRYPOINT ["java","-jar","/app/spring-boot-application.jar"]
