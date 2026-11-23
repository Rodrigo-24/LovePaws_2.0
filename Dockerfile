# Etapa 1: Construcción del proyecto
FROM maven:3.10.0-eclipse-temurin-21-noble AS build

WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests clean package


# Etapa 2: Ejecución de la aplicación
FROM eclipse-temurin:21-jre-noble

WORKDIR /app

# Crear usuario sin privilegios de administrador
# y preparar el directorio de fotografías
RUN groupadd --system spring \
    && useradd --system --gid spring --home-dir /app spring \
    && mkdir -p /app/uploads/mascotas \
    && chown -R spring:spring /app

# Copiar el ejecutable generado por Maven
COPY --from=build --chown=spring:spring \
    /workspace/target/lovepaws-0.0.1-SNAPSHOT.jar \
    /app/app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]