FROM openjdk:11.0.3-jdk-slim

# Add libraries in unshaded form to be cached
ADD target/lib /usr/share/cascadebot/lib

# Unshaded jar file that will be run with dependencies from lib/ in it's classpath
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/cascadebot/cascadebot.jar

ENTRYPOINT ["java", "-jar", "/usr/share/cascadebot/cascadebot.jar"]