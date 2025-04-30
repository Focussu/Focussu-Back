FROM openjdk:17-slim
WORKDIR /app
COPY ./backend/build/libs/*.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar"]