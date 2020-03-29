FROM openjdk:8-jre-alpine3.9

ENV APP_VERSION=0.14.5
ENV APP_HOME=/opt/finexp-futures
ENV APP_DATA=/var/finexp-futures-data
WORKDIR $APP_HOME

RUN apk add curl
RUN mkdir -p "${APP_DATA}" "${APP_HOME}" \
    && curl -L "https://github.com/robot-aquila/aquila/releases/download/ff-v${APP_VERSION}-rc/finexp-futures-${APP_VERSION}.jar" \
        -o "${APP_HOME}/finexp-futures-${APP_VERSION}.jar"
COPY finexp-futures.ini-docker "${APP_HOME}/finexp-futures.ini"

CMD java -jar "${APP_HOME}/finexp-futures-${APP_VERSION}.jar" -r "${APP_DATA}" --skip-integrity-test --config=finexp-futures.ini
