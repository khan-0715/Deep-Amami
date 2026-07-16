FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
COPY --from=build /target/*.jar app.jar
ENV JAVA_OPTS="-XX:TieredStopAtLevel=1 -Xmx300m -Xms300m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]
