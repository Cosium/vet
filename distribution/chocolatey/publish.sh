#!/usr/bin/env bash
set -e

WORKDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

API_KEY=$(cat ${WORKDIR}/../../.choco-apikey)

NUPKG_NAME=gerrit-vet.$1.nupkg
NUPKG_FILE=${WORKDIR}/${NUPKG_NAME}

VERIFICATION_FILE=${WORKDIR}/tools/VERIFICATION.txt
VERIFICATION=$(cat ${WORKDIR}/VERIFICATION.txt.template)
VERIFICATION=$(echo -e "${VERIFICATION/_version_/$1}")
VERIFICATION=$(echo -e "${VERIFICATION/_sha256_/$2}")

echo -e "${VERIFICATION}" > ${VERIFICATION_FILE}

NUSPEC_FILE=${WORKDIR}/vet.nuspec
NUSPEC=$(cat ${WORKDIR}/vet.nuspec.template)
NUSPEC=$(echo -e "${NUSPEC/_version_/$1}")

echo -e "${NUSPEC}" > ${NUSPEC_FILE}

cp ${WORKDIR}/../../build/binaries/vet-windows_x64.zip ${WORKDIR}/tools/vet.zip

docker build -t choco ${WORKDIR}
docker run --rm -u $(id -u) -v "${WORKDIR}:/workdir" choco sh -c "cd /workdir && mono /usr/bin/choco.exe pack && rm -fr usr"
docker run --rm -u $(id -u) -v "${WORKDIR}:/workdir" choco \
sh -c "cd /workdir && mono /usr/bin/choco.exe push ${NUPKG_NAME} -s https://chocolatey.org/ --k ${API_KEY} -c /tmp && rm -fr usr"

rm ${VERIFICATION_FILE}
rm ${NUSPEC_FILE}
rm ${NUPKG_FILE}