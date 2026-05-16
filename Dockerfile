FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine

# Baixar o New Relic Java Agent 9.2.0 com versão fixada — builds reprodutíveis
ADD https://download.newrelic.com/newrelic/java-agent/newrelic-agent/9.2.0/newrelic-agent-9.2.0.jar /newrelic-agent.jar

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT exec java \
  -javaagent:/newrelic-agent.jar \
  -Dnewrelic.config.app_name=geradornotafiscal \
  -Dnewrelic.config.license_key=${NEW_RELIC_LICENSE_KEY} \
  -Dnewrelic.config.distributed_tracing.enabled=true \
  -Dnewrelic.config.span_events.enabled=true \
  -Dnewrelic.config.application_logging.enabled=true \
  -Dnewrelic.config.application_logging.forwarding.enabled=true \
  -Dnewrelic.config.application_logging.local_decorating.enabled=true \
  -jar app.jar
