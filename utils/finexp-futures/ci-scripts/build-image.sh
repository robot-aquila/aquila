#!/bin/bash

MY_PATH=`dirname $0`
APP_VERSION=$1
if [[ -z "${APP_VERSION}" ]]; then
    echo "Usage: $0 <VERSION_NUMBER>"
    exit 1
fi
APP_NAME="finexp-futures-${APP_VERSION}"
docker build -t "aquila:${APP_NAME}" -f "${MY_PATH}/${APP_NAME}.dockerfile" "${MY_PATH}"
