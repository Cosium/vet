#!/usr/bin/env bash
jq --version > /dev/null

LINUX_BINARY="vet-linux_x64.zip"
MACOSX_BINARY="vet-macosx_x64.zip"
WINDOW_BINARY="vet-windows_x64.zip"

CURRENT_VERSION=$(cat version.txt)
NEW_VERSION=$(echo ${CURRENT_VERSION} | awk -F. -v OFS=. 'NF==1{print ++$NF}; NF>1{if(length($NF+1)>length($NF))$(NF-1)++; $NF=sprintf("%0*d", length($NF), ($NF+1)%(10^length($NF))); print}')
echo "Releasing ${NEW_VERSION}"

echo "Bumping version.txt"
echo ${NEW_VERSION} > version.txt
git add version.txt
echo "Bumped version.txt"

echo "Bumping README.md"
README_CONTENT=$(cat README.md)
echo "${README_CONTENT/$CURRENT_VERSION/$NEW_VERSION}"
git add README.md
echo "Bumped README.md"

git commit -m "Release ${NEW_VERSION}" && git tag ${NEW_VERSION}

echo "Cleaning"
./gradlew -Pproject-version=${NEW_VERSION} clean
echo "Cleaned"

echo "Uploading to sonatype repository"
./gradlew -Pproject-version=${NEW_VERSION} uploadArchives
echo "Uploaded to sonatype repository"

echo "Buiding binaries"
./gradlew -Pproject-version=${NEW_VERSION} binaries
echo "Built binaries"

echo "Uploading binaries to GitHub"
GITHUB_BASE_URL="https://github.com/repos/Cosium/vet"
GITHUB_UPLOAD_URL=$(curl -X POST -H "Content-Type: application/json" -d '{"tag_name": "${NEW_VERSION}", "draft": true}' ${GITHUB_BASE_URL}/releases | jq -r '.upload_url')
echo "Uploading Linux binaries to GitHub"
curl -X POST -H "Content-Type: application/zip" --data-binary @build/binaries/${LINUX_BINARY} ${GITHUB_UPLOAD_URL}?name=${LINUX_BINARY}
echo "Uploading MacOSX binaries to GitHub"
curl -X POST -H "Content-Type: application/zip" --data-binary @build/binaries/${MACOSX_BINARY} ${GITHUB_UPLOAD_URL}?name=${MACOSX_BINARY}
echo "Uploading Windows binaries to GitHub"
curl -X POST -H "Content-Type: application/zip" --data-binary @build/binaries/${WINDOW_BINARY} ${GITHUB_UPLOAD_URL}?name=${WINDOW_BINARY}
echo "Uploaded binaries to GitHub"

echo "Pushing to git"
git push && git push --tags

echo "Released ${NEW_VERSION}"