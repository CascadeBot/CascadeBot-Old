FROM maven:3.6.2-amazoncorretto-11 AS builder
WORKDIR /bot/
ADD ./src ./src/
ADD pom.xml .
RUN mvn package

FROM amazoncorretto:11.0.4
WORKDIR /bot
COPY --from=builder /bot/target/*jar-with-dependencies.jar CascadeBot.jar
CMD java -jar CascadeBot.jar
EXPOSE 8080
EXPOSE 6060
