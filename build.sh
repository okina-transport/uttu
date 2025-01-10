#!/usr/bin/env bash

echo Building docker image

# Back
VERSION=$(mvn -q \
    -Dexec.executable=echo \
    -Dexec.args='${project.version}' \
    --non-recursive \
    exec:exec)

mvn clean install -DskipTests


VERSION=`echo ${VERSION} | sed -r "s/\x1B\[([0-9]{1,3}(;[0-9]{1,2};?)?)?[mGK]//g"`
VERSION=$(echo "$VERSION" | sed 's/[[:space:]]*$//')
IMAGE_NAME=registry.okina.fr/mobiiti/uttu:"${VERSION}"

echo version:${VERSION}
echo targetFile:target/uttu-${VERSION}.jar

#mvn spring-boot:build-image -Dspring-boot.build-image.imageName="${IMAGE_NAME}" -Dfile.encoding=UTF-8
docker build -t "${IMAGE_NAME}" --build-arg JAR_FILE=target/uttu-${VERSION}.jar .
docker push "${IMAGE_NAME}"
