FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine

ADD https://dtdg.co/latest-java-tracer /dd-java-agent.jar

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-javaagent:/dd-java-agent.jar", \
  "-Ddd.service=geradornotafiscal", \
  "-Ddd.env=dev", \
  "-Ddd.version=1.0.0", \
  "-Ddd.logs.injection=true", \
  "-Ddd.profiling.enabled=true", \
  "-Ddd.trace.sample.rate=1", \
  "-jar", "app.jar"]