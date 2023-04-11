#
# Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
#

FROM gradle:7.3-jdk17 as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew service:buildNeeded

FROM openjdk:17-jdk-slim
EXPOSE 80
RUN mkdir /app
COPY --from=builder /home/gradle/src/service/build/libs/service-1.0.jar /app/spring-boot-application.jar
ENTRYPOINT ["java","-jar","/app/spring-boot-application.jar"]
