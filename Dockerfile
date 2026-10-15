# ── Stage 1: Build ────────────────────────────────────────────────────────────
# Gunakan image Maven + JDK 17 untuk kompilasi dan test
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml dulu (agar layer dependency di-cache oleh Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source code lalu build
COPY src ./src
RUN mvn package -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
# Gunakan image JRE yang lebih ringan untuk menjalankan aplikasi
FROM eclipse-temurin:17-jre

WORKDIR /app

# Salin JAR dari stage build
COPY --from=build /app/target/credit-simulator-1.0.0.jar app.jar

# Salin script launcher
COPY credit_simulator .
RUN chmod +x credit_simulator

# Jalankan aplikasi
ENTRYPOINT ["java", "-jar", "app.jar"]
