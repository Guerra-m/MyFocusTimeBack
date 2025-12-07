# Usamos la imagen oficial de OpenJDK 17 (compatible con Spring Boot)
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo pom.xml y descargamos dependencias (cacheo de dependencias)
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

# Copiamos todo el proyecto
COPY . .

# Compilamos el proyecto y generamos el jar
RUN ./mvnw clean package -DskipTests

# Expone el puerto que usa tu app (8080)
EXPOSE 8080

# Comando para correr la aplicación
CMD ["java", "-jar", "target/MyFocusTime-0.0.1-SNAPSHOT.jar"]
