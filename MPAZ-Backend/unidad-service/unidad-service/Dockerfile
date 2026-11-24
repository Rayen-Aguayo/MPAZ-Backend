# Etapa 1: Compilación y empaquetado con Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cachear dependencias primero
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y generar el JAR ejecutable
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final ligera de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Usuario sin privilegios de root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
