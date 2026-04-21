# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Copy dependency descriptor first (better caching)
COPY pom.xml .

# Download dependencies
RUN mvn -q -e -B dependency:go-offline

# Copy source
COPY src ./src

# Build application
RUN mvn clean package -DskipTests


# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy compiled jar from build stage
COPY --from=build /build/target/*.jar app.jar

# Application port
EXPOSE 8083

# Run application
ENTRYPOINT ["java","-jar","/app/app.jar"]