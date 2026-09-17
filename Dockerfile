# syntax=docker/dockerfile:1.4
FROM node:20 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

FROM maven:3.9.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY Backend/pom.xml ./
COPY Backend/src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=30

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
COPY --from=frontend-build /app/frontend/dist ./static

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "app.jar" ]
