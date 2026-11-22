# ---- Compilación ----
FROM gradle:8.14.3-jdk21 AS build
WORKDIR /app
COPY settings.gradle build.gradle ./
RUN gradle dependencies --no-daemon -q > /dev/null || true
COPY src ./src
RUN gradle bootJar --no-daemon -x test

# ---- Ejecución ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
