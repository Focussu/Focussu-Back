# backend/Dockerfile

FROM openjdk:17-slim
WORKDIR /app

# CI에서 빌드된 JAR을 그대로 사용
COPY build/libs/*.jar app.jar

EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar"]
