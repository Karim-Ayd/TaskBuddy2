# Verwende Java 17
FROM eclipse-temurin:17-jdk

# Arbeitsverzeichnis im Container
WORKDIR /app

# Alle Projektdateien in den Container kopieren
COPY . .

# Baue das Projekt (ohne Tests)
RUN ./gradlew clean build -x test

# Starte die Spring Boot App (achte auf Dateinamen!)
CMD ["java", "-jar", "build/libs/demo-0.0.1-SNAPSHOT.jar"]
