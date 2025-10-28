# Etapa de construcción
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Instala utilidades necesarias (usando apt-get)
RUN apt-get update && apt-get install -y dos2unix && rm -rf /var/lib/apt/lists/*

# Copia los archivos del proyecto
COPY . .

# Corrige formato (si viene de Windows) y otorga permisos al wrapper Maven
RUN dos2unix mvnw && chmod +x mvnw

# Compila el proyecto sin ejecutar tests
RUN ./mvnw clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el JAR generado
COPY --from=build /app/target/operaciones-bancarias-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=docker"]
