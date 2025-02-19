# Stage 1: Build the application
FROM maven:3.8.6-eclipse-temurin-17 AS build

WORKDIR /home/app
COPY pom.xml /home/app/pom.xml
RUN mvn dependency:go-offline -B

COPY . /home/app
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/app.jar"]