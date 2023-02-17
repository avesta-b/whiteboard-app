FROM gradle:7.3-jdk17 as builder
WORKDIR /app
COPY . .
RUN ./gradlew build --stacktrace

FROM openjdk
WORKDIR /app
EXPOSE 80
COPY --from=builder /app/service/libs/service-0.0.1.jar .
CMD java -jar service-0.0.1.jar
