FROM openjdk:11-jdk-slim

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/cascadebot/cascadebot.jar"]

ADD target/lib /usr/share/cascadebot/lib

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/cascadebot/cascadebot.jar