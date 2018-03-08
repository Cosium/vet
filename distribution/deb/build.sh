#! /bin/bash
WORKDIR=$(pwd)
DIR=vet_$1
mkdir $DIR
mkdir $DIR/usr
mkdir $DIR/usr/local
mkdir $DIR/usr/local/bin
mkdir $DIR/usr/local/lib
mkdir $DIR/usr/local/lib/vet
wget https://github.com/Cosium/vet/releases/download/$1/vet-linux_x64.zip -O $DIR/usr/local/lib/vet/vet.zip
unzip $DIR/usr/local/lib/vet/vet.zip -d $DIR/usr/local/lib/vet
rm $DIR/usr/local/lib/vet/vet.zip
cd $DIR/usr/local/bin
ln -sfn ../lib/vet/bin/vet vet
cd $WORKDIR

mkdir $DIR/DEBIAN
cat > $DIR/DEBIAN/control <<EOF

Package: vet
Version: $1
Section: base
Priority: optional
Architecture: amd64
Maintainer: Réda Housni Alaoui <reda.housnialaoui@cosium.com>
Description: Gerrit client using pull request review workflow

EOF

dpkg-deb --build $DIR

rm -rf $DIR
mv vet_$1.deb vet.deb
zip -r vet-linux_x64.deb.zip vet.deb
rm vet.deb