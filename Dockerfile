# Imagen base con Maven y Java 17
FROM maven:3.9.2-eclipse-temurin-17 AS build

# Directorio de trabajo
WORKDIR /app

# Copiamos pom.xml y descargamos dependencias (cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el resto del proyecto
COPY src ./src

# Construimos el JAR
RUN mvn package -DskipTests

# Imagen final solo con Java para ejecutar la app
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copiamos el JAR construido
COPY --from=build /app/target/MyFocusTime-0.0.1-SNAPSHOT.jar app.jar

# Comando para ejecutar la app
ENTRYPOINT ["java","-jar","app.jar"]
