FROM openjdk:11.0.3-jdk-slim

# Add libraries in unshaded form to be cached
ADD target/lib /cascadebot/lib

# Unshaded jar file that will be run with dependencies from lib/ in it's classpath
ARG JAR_FILE
ADD target/${JAR_FILE} /cascadebot/cascadebot.jar

ENTRYPOINT ["java", "-jar", "/cascadebot/cascadebot.jar"]