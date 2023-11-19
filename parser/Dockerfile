FROM eclipse-temurin:21-jdk-jammy as base
EXPOSE 8081
ADD targer/parser.jar parser.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "parser.jar"]