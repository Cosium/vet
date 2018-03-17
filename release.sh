#!/usr/bin/env bash
set -e

LINUX_BINARY="vet-linux_x64.zip"
MACOSX_BINARY="vet-macosx_x64.zip"
WINDOW_BINARY="vet-windows_x64.zip"

GITHUB_CREDENTIALS=$(cat .github-credentials)
GITHUB_BASE_URL="https://api.github.com/repos/Cosium/vet"

CURRENT_VERSION=$(cat version.txt)
NEW_VERSION=$(echo ${CURRENT_VERSION} | awk -F. -v OFS=. 'NF==1{print ++$NF}; NF>1{if(length($NF+1)>length($NF))$(NF-1)++; $NF=sprintf("%0*d", length($NF), ($NF+1)%(10^length($NF))); print}')

echo "Releasing ${NEW_VERSION}"

echo "Checking jq presence"
jq --version > /dev/null
echo "Checked jq presence"

echo "Bumping version.txt"
echo ${NEW_VERSION} > version.txt
git add version.txt
echo "Bumped version.txt"

echo "Bumping README.md"
README_CONTENT=$(cat README.md)
echo "${README_CONTENT/$CURRENT_VERSION/$NEW_VERSION}" > README.md
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
GITHUB_UPLOAD_URL=$(curl -u ${GITHUB_CREDENTIALS} -X POST -H "Content-Type: application/json" -d '{"tag_name": "${NEW_VERSION}", "draft": false}' ${GITHUB_BASE_URL}/releases | jq -r '.upload_url' | cut -d'{' -f 1)
echo "Uploading Linux binaries to GitHub"
curl -u ${GITHUB_CREDENTIALS} -H "Content-Type: application/zip" --data-binary @build/binaries/${LINUX_BINARY} ${GITHUB_UPLOAD_URL}?name=${LINUX_BINARY}
echo "Uploading MacOSX binaries to GitHub"
curl -u ${GITHUB_CREDENTIALS} -H "Content-Type: application/zip" --data-binary @build/binaries/${MACOSX_BINARY} ${GITHUB_UPLOAD_URL}?name=${MACOSX_BINARY}
echo "Uploading Windows binaries to GitHub"
curl -u ${GITHUB_CREDENTIALS} -H "Content-Type: application/zip" --data-binary @build/binaries/${WINDOW_BINARY} ${GITHUB_UPLOAD_URL}?name=${WINDOW_BINARY}
echo "Uploaded binaries to GitHub"

echo "Publishing Linux deb package"
distribution/deb/publish.sh ${NEW_VERSION} ${GITHUB_CREDENTIALS} ${GITHUB_UPLOAD_URL}
echo "Published Linux deb file"

echo "Publishing Homebrew package"
distribution/homebrew/publish.sh ${NEW_VERSION} $(sha256sum build/binaries/${MACOSX_BINARY})
echo "Published Homebrew package"

echo "Pushing to git"
git push && git push --tags
echo "Pushed to git"

echo "Released ${NEW_VERSION}"