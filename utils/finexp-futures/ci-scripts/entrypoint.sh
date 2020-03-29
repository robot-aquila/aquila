#!/bin/sh

set -e
MY_PATH=`dirname $0`
if [ -z "${APP_VERSION}" ]; then
    echo "Environment variable not defined: APP_VERSION"
    exit 1
else
    echo "APP_VERSION: ${APP_VERSION}"
fi
if [ -z "${APP_HOME}" ]; then
    echo "Environment variable not defined: APP_HOME"
    exit 1
else
    echo "   APP_HOME: ${APP_HOME}"
fi
if [ -z "${APP_DATA}" ]; then
    echo "Environment variable not defined: APP_DATA"
    exit 1
else
    echo "   APP_DATA: ${APP_DATA}"
fi
if [ -z "${AQUILA_IT_HUB_URL}" ]; then
    if [ -z "${HUB_HOST}" ]; then
        echo "Environment variable not defined: HUB_HOST"
        exit 1
    else
        echo "   HUB_HOST: ${HUB_HOST}"
    fi
    if [ -z "${HUB_PORT}" ]; then
        echo "Environment variable not defined: HUB_PORT"
        exit 1
    else
        echo "   HUB_PORT: ${HUB_PORT}"
    fi
    export AQUILA_IT_HUB_URL="http://${HUB_HOST}:${HUB_PORT}/wd/hub"
else
    export AQUILA_IT_HUB_URL
fi
if [ -z "${AQUILA_IT_DRIVER}" ]; then
    export AQUILA_IT_DRIVER="chrome"
else
    export AQUILA_IT_DRIVER
fi
echo "AQUILA_IT_HUB_URL: ${AQUILA_IT_HUB_URL}"
echo "AQUILA_IT_DRIVER : ${AQUILA_IT_DRIVER}"

exec java -jar "${APP_HOME}/finexp-futures-${APP_VERSION}.jar" -r "${APP_DATA}" \
    --config="${APP_HOME}/finexp-futures.ini" --skip-integrity-test
