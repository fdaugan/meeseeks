FROM openjdk:17.0.1-slim
LABEL maintainer "fabrice.daugan@gmail.com"

ARG GROUP_ID="org.fabdouglas.meeseeks"
ARG ARTIFACT_ID="devops-material-spring"
ARG NEXUS_HOST="oss.sonatype.org"
ARG VERSION="1.0.0"
ARG NEXUS_URL="https://${NEXUS_HOST}"
ARG WAR="${NEXUS_URL}/service/local/artifact/maven/redirect?r=public&g=${GROUP_ID}&a=${ARTIFACT_ID}&v=${VERSION}&p=jar"

RUN set -xe \
  apt-get update && \
  apt-get update --fix-missing && \
  apt-get -y install curl

ENV CONTEXT_URL="/" \
    APP_HOME="/usr/local/app" \
    SERVER_PORT=8080 \
    SERVER_HOST="0.0.0.0" \
    JMX_PORT=9010
ENV JMX_OPTS=" -Dcom.sun.management.jmxremote \
      -Dcom.sun.management.jmxremote.port=$JMX_PORT \
      -Dcom.sun.management.jmxremote.local.only=false \
      -Dcom.sun.management.jmxremote.authenticate=false \
      -Dcom.sun.management.jmxremote.ssl=false"

EXPOSE $SERVER_PORT
EXPOSE $JMX_PORT

ADD ${WAR} "${APP_HOME}/app.jar"
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