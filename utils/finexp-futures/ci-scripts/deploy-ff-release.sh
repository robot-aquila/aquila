#!/bin/sh
# ----------------------------------
# Ensure GHUB_TOKEN variable defined
# ----------------------------------

MY_PATH=`dirname "$0"`
#RELEASE_TAG="${1}"
RELEASE_TAG=`git describe --tags`
if [[ $? -ne 0 ]]; then
    echo "Error obtaining most recent tag"
    exit 1
fi
GHUB_OWNER="robot-aquila"
GHUB_REPOS="aquila"
GHUB_URL="https://api.github.com/repos/${GHUB_OWNER}/${GHUB_REPOS}"
if [ -z "${GHUB_TOKEN}" ]; then
    GHUB_TOKEN_PATH="${MY_PATH}/../ght.txt"
    if [ ! -f "${GHUB_TOKEN_PATH}" ]; then
        echo "GitHub token undefined."
        exit 1
    fi
    GHUB_TOKEN=`cat "${GHUB_TOKEN_PATH}"`
fi
RELEASE_PROJ="finexp-futures"

MSG_NOT_FOUND='Not Found'
RELEASE_VER=`echo "${RELEASE_TAG}" | grep -E '^ff\-v[0-9]+\.[0-9]+\.[0-9]+\-rc$' | sed 's/^ff-v//' | sed 's/-rc$//'`
if [ "${RELEASE_TAG}" != "ff-v${RELEASE_VER}-rc" ]; then
    echo "Unable to extract version of release tag: ${RELEASE_TAG}"
    echo "Highly possible that latest tag not assotiated with new release of ${RELEASE_PROJ}"
    exit
fi
RELEASE_FILE="${RELEASE_PROJ}-${RELEASE_VER}.jar"
JSON_HEADER="- JSON --------------------"
CURL_OPTS="--ipv4 --silent"

# Test that tag exists.
# If not exists then nothing to do.
LAST_URL="${GHUB_URL}/tags"
JSON=`curl $CURL_OPTS "${LAST_URL}"`
if [[ $? -ne 0 ]]; then
    echo "Getting tag list failed: ${LAST_URL}"
    exit 1
fi
MSG=`echo ${JSON} | jq -r '.message?'`
if [[ "${MSG}" != '' ]] && [[ "${MSG}" != 'null' ]]; then
    echo "Error response from: ${LAST_URL}"
    echo "Error message: [${MSG}]"
    echo ${JSON_HEADER}
    echo ${JSON}
    exit 1
fi
MSG=`echo ${JSON} | jq -r -e ".[] | select(.name == \"${RELEASE_TAG}\") | .name"`
if [[ $? -ne 0 ]]; then
    echo "Tag not exists: ${RELEASE_TAG}"
    echo ${JSON_HEADER}
    echo ${JSON}
    exit 1
fi

# Test that release for selected tag is exists.
# If not exists then release have to be created.
LAST_URL="${GHUB_URL}/releases/tags/${RELEASE_TAG}"
JSON=`curl $CURL_OPTS "${LAST_URL}"`
if [[ $? -ne 0 ]]; then
    echo "Getting release info failed: ${LAST_URL}"
    exit 1
fi
MSG=`echo ${JSON} | jq -r '.message?'`
if [[ "${MSG}" != '' ]] && [[ "${MSG}" != 'null' ]]; then
    echo "Release for tag was not found: ${RELEASE_TAG}"
    RELEASE_NAME="FF v${RELEASE_VER} RC"
    RELEASE_DESC="New finexp-futures release based on ${RELEASE_TAG} tag"
    JSON_DUMMY_DATA="{ \"tag_name\": \"${RELEASE_TAG}\", \"name\": \"${RELEASE_NAME}\", \"body\": \"${RELEASE_DESC}\", \"draft\": false, \"prerelease\": true }"
    LAST_URL="${GHUB_URL}/releases"
    JSON=`curl $CURL_OPTS -XPOST -H "Authorization:token $GHUB_TOKEN" --data "${JSON_DUMMY_DATA}" "${LAST_URL}"`
    if [[ $? -ne 0 ]]; then
        echo "Creating release failed: ${LAST_URL}"
        exit 1
    fi
    MSG=`echo ${JSON} | jq -r '.message?'`
    if [[ "${MSG}" != '' ]] && [[ "${MSG}" != 'null' ]]; then
        echo "Error creating release: ${MSG}"
        echo "JSON data sent: ${JSON_DUMMY_DATA}"
        echo ${JSON_HEADER}
        echo ${JSON}
        exit 1
    fi
    RELEASE_ID=`echo ${JSON} | jq -r -e '.id'`
    if [[ $? -ne 0 ]]; then
        echo "Unable to determine ID of new release"
        echo ${JSON_HEADER}
        echo ${JSON}
        exit 1
    fi
    echo "New release created: name=[${RELEASE_NAME}] tag=[${RELEASE_TAG}] id=[${RELEASE_ID}]"
    #echo ${JSON_HEADER}
    #echo ${JSON}
else
    RELEASE_NAME=`echo ${JSON} | jq -r -e '.name'`
    if [[ $? -ne 0 ]]; then
        echo "Unable to determine release name."
        echo ${JSON_HEADER}
        echo ${JSON}
        exit 1
    fi
    RELEASE_DESC=`echo ${JSON} | jq -r -e '.body'`
    if [[ $? -ne 0 ]]; then
        echo "Unable to determine release description."
        echo ${JSON_HEADER}
        echo ${JSON}
        exit 1
    fi
    RELEASE_ID=`echo ${JSON} | jq -r -e '.id'`
    if [[ $? -ne 0 ]]; then
        echo "Unable to determine release id."
        echo ${JSON_HEADER}
        echo ${JSON}
        exit 1
    fi
fi
RELEASE_UPLOAD_URL=`echo ${JSON} | jq -r -e '.upload_url'`
if [[ $? -ne 0 ]]; then
    echo "Unable to determine release upload url."
    echo ${JSON_HEADER}
    echo ${JSON}
    exit 1
fi
RELEASE_UPLOAD_URL=`echo ${RELEASE_UPLOAD_URL} | sed s/assets\{.*$/assets/`
echo "Release upload URL: ${RELEASE_UPLOAD_URL}"

# Test that appropriate asset exists.
# If not exist then upload.
LAST_URL="${GHUB_URL}/releases/${RELEASE_ID}/assets"
JSON=`curl $CURL_OPTS ${LAST_URL}`
if [[ $? -ne 0 ]]; then
    echo "Getting list of assets failed: ${LAST_URL}"
    exit 1
fi
MSG=`echo ${JSON} | jq -r '.message?'`
if [[ "${MSG}" != '' ]] && [[ "${MSG}" != 'null' ]]; then
    echo "Error response from: ${LAST_URL}"
    echo "Error message: [${MSG}]"
    echo ${JSON_HEADER}
    echo ${JSON}
    exit 1
fi
MSG=`echo ${JSON} | jq -r -e ".[] | select(.name == \"${RELEASE_FILE}\") | .id"`
if [[ $? -ne 0 ]]; then
    echo "Asset ${RELEASE_FILE} not found in release ID ${RELEASE_ID}. Create new one."
    MOST_RECENT_TAG=`git describe --tags`
    if [[ "${MOST_RECENT_TAG}" != "${RELEASE_TAG}" ]]; then
        echo "Most recent tag [${MOST_RECENT_TAG}] does not equal to release tag [${RELEASE_TAG}]."
        exit 1
    fi
    SOURCE_FILE="${MY_PATH}/../target/${RELEASE_FILE}"
    if [[ ! -f "${SOURCE_FILE}" ]]; then
        echo "Source file expected to be exist: ${SOURCE_FILE}"
        echo "Ensure that this script is called after build"
        exit 1
    fi
    LAST_URL="${RELEASE_UPLOAD_URL}?name=${RELEASE_FILE}"
    JSON=`curl $CURL_OPTS -XPOST -H "Authorization:token $GHUB_TOKEN" -H "Content-Type: application/octet-stream" --data-binary @"${SOURCE_FILE}" "${LAST_URL}"`
    if [[ $? -ne 0 ]]; then
        echo "Error uploading asset: ${LAST_URL}"
        exit 1
    fi
    MSG=`echo ${JSON} | jq -r '.message?'`
    if [[ "${MSG}" != '' ]] && [[ "${MSG}" != 'null' ]]; then
        echo "Error response from: ${LAST_URL}"
        echo "Error message: ${MSG}"
        echo ${JSON_HEADER}
        echo ${JSON}
        exit 1
    fi
    ASSET_ID=`echo ${JSON} | jq -r -e '.id'`
    if [[ $? -ne 0 ]]; then
        echo "Unable to determine ID of new uploaded asset"
        echo ${JSON_HEADER}
        echo ${JSON}
        exit 1
    fi
    echo "New asset {$RELEASE_FILE} uploaded. Assigned ID: ${ASSET_ID}"
else
    ASSET_ID="${MSG}"
    echo "Asset ${RELEASE_FILE} already uploaded. Assigned ID: ${ASSET_ID}. Skip uploading."
fi

echo " RELEASE_TAG: ${RELEASE_TAG}"
echo " RELEASE_VER: ${RELEASE_VER}"
echo "RELEASE_NAME: ${RELEASE_NAME}"
echo "RELEASE_DESC: ${RELEASE_DESC}"
echo "  RELEASE ID: ${RELEASE_ID}"
echo "    ASSET ID: ${ASSET_ID}"
