FROM openjdk:15-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-jar", "app.jar"]