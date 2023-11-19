FROM eclipse-temurin:21-jdk-jammy as base
EXPOSE 8080
ADD targer/trader.jar trader.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "trader.jar"]