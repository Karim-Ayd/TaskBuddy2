# Verwende Java 25 (neueste Version von Eclipse Temurin)
FROM eclipse-temurin:25-jdk

# Arbeitsverzeichnis im Container
WORKDIR /app

# Projektdateien kopieren
COPY . .

# Baue das Projekt (ohne Tests)
RUN ./gradlew clean build -x test

# Starte deine Spring Boot App
CMD ["java", "-jar", "build/libs/demo-0.0.1-SNAPSHOT.jar"]
