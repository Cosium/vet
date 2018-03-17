#! /bin/bash
set -e

ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DEB_DIR=${ROOT_DIR}/vet_$1
mkdir -p ${DEB_DIR}/usr/local/bin
mkdir -p ${DEB_DIR}/usr/local/lib/vet
cp ${ROOT_DIR}/../../build/binaries/vet-linux_x64.zip ${DEB_DIR}/usr/local/lib/vet/vet.zip
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
mv ${ROOT_DIR}/vet_$1.deb ${ROOT_DIR}/vet.deb
zip -r ${ROOT_DIR}/vet-linux_x64.deb.zip ${ROOT_DIR}/vet.deb
rm ${ROOT_DIR}/vet.deb

curl -u $2 -H "Content-Type: application/zip" --data-binary @${ROOT_DIR}/vet-linux_x64.deb.zip $3?name=vet-linux_x64.deb.zip

rm ${ROOT_DIR}/vet-linux_x64.deb.zip