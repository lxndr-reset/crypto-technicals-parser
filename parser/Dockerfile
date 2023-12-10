FROM eclipse-temurin:21-jdk-jammy as base
EXPOSE 8080
ADD target/parser-exec.jar parser.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "parser.jar"]