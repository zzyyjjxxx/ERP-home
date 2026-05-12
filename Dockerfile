FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY erp-server/target/erp-server-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
