#!/bin/bash
set -e

WORKDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
GITHUB_CREDENTIALS=$(cat ${WORKDIR}/../../.github-credentials)

DEB_DIR=${WORKDIR}/vet_$1
mkdir -p ${DEB_DIR}/usr/local/bin
mkdir -p ${DEB_DIR}/usr/local/lib/vet
mkdir -p ${DEB_DIR}/etc/bash_completion.d
cp ${WORKDIR}/../../vet_completion.sh ${DEB_DIR}/etc/bash_completion.d
cp ${WORKDIR}/../../build/binaries/vet-linux_x64.zip ${DEB_DIR}/usr/local/lib/vet/vet.zip
unzip ${DEB_DIR}/usr/local/lib/vet/vet.zip -d ${DEB_DIR}/usr/local/lib/vet
rm ${DEB_DIR}/usr/local/lib/vet/vet.zip
cd ${DEB_DIR}/usr/local/bin
ln -sfn ../lib/vet/bin/vet vet
cd -

mkdir ${DEB_DIR}/DEBIAN
cat > ${DEB_DIR}/DEBIAN/control <<EOF

Package: vet
Version: $1
Section: base
Priority: optional
Architecture: amd64
Maintainer: Réda Housni Alaoui <reda.housnialaoui@cosium.com>
Description: Gerrit client using pull request review workflow

EOF

dpkg-deb --build ${DEB_DIR}

rm -rf ${DEB_DIR}
mv ${WORKDIR}/vet_$1.deb ${WORKDIR}/vet.deb
cd ${WORKDIR}
zip -r vet-linux_x64.deb.zip vet.deb
cd -
rm ${WORKDIR}/vet.deb

curl -u ${GITHUB_CREDENTIALS} -H "Content-Type: application/zip" --data-binary @${WORKDIR}/vet-linux_x64.deb.zip $2?name=vet-linux_x64.deb.zip

rm ${WORKDIR}/vet-linux_x64.deb.zip