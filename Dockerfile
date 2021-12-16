FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /build/
ADD . .
RUN mvn clean package -B -q -Dmaven.test.skip=true

FROM openjdk:17.0.1-slim
LABEL maintainer "fabrice.daugan@gmail.com"

RUN set -xe \
  apt-get update && \
  apt-get update --fix-missing && \
  apt-get -y install curl

ENV CONTEXT_URL="/" \
    APP_HOME="/usr/local/app" \
    SERVER_PORT=8080 \
    SERVER_HOST="0.0.0.0" \
    JMX_PORT=9010 \
    CUSTOM_OPTS="" \
    JAVA_OPTIONS="" \
    JAVA_MEMORY=""
ENV JMX_OPTS=" -Dcom.sun.management.jmxremote \
      -Dcom.sun.management.jmxremote.port=$JMX_PORT \
      -Dcom.sun.management.jmxremote.local.only=false \
      -Dcom.sun.management.jmxremote.authenticate=false \
      -Dcom.sun.management.jmxremote.ssl=false"

EXPOSE $SERVER_PORT
EXPOSE $JMX_PORT

COPY --from=builder /build/target/*.war ${SERVER_HOME}/app.war
ADD src/test/resources/app.policy "${APP_HOME}/app.policy"

WORKDIR "${APP_HOME}"

CMD mkdir -p "${APP_HOME}" && \
  java $JAVA_MEMORY $JAVA_OPTIONS $CUSTOM_OPTS $JMX_OPTS \
    --add-opens java.base/jdk.internal.loader=ALL-UNNAMED \
    --add-opens java.base/jdk.internal.module=ALL-UNNAMED \
    --add-opens java.base/jdk.internal.ref=ALL-UNNAMED \
    --add-opens java.base/jdk.internal.util.jar=ALL-UNNAMED \
    --add-opens java.base/jdk.internal.ref=ALL-UNNAMED \
    --add-opens java.base/jdk.internal.perf=ALL-UNNAMED \
    --add-opens java.base/jdk.internal.reflect=ALL-UNNAMED \
    --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.lang.package=ALL-UNNAMED \
    --add-opens java.base/java.lang.module=ALL-UNNAMED \
    --add-opens java.base/java.util=ALL-UNNAMED \
    --add-opens java.base/jdk.internal.math=ALL-UNNAMED \
    --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED \
    --add-opens java.xml/javax.xml.catalog=ALL-UNNAMED \
    --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED \
    --add-opens jdk.management.jfr/jdk.management.jfr=ALL-UNNAMED \
    -Djavax.net.ssl.trustStorePassword=changeit \
    -Dserver.address="${SERVER_HOST}" \
    -Djava.security.policy="${APP_HOME}/app.policy" \
    -Dserver.port="${SERVER_PORT}" \
    -Djava.net.preferIPv4Stack=true \
    -Dserver.servlet.context-path="${CONTEXT_URL}" \
    -jar "${APP_HOME}/app.jar"

HEALTHCHECK --interval=10s --timeout=1s --retries=5 --start-period=10s \
CMD curl --fail http://localhost:${SERVER_PORT}${CONTEXT_URL}/manage/health || exit 1