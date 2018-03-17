#!/usr/bin/env bash
set -e

WORKDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

TEMPLATE=$(cat ${WORKDIR}/vet.rb.template)
TEMPLATE=$(echo -e "${TEMPLATE/_version_/$1}")
TEMPLATE=$(echo -e "${TEMPLATE/_sha256_/$2}")

HOMEBREW_DIR=$(mktemp -d)
git clone https://github.com/Cosium/homebrew-vet ${HOMEBREW_DIR}

cd ${HOMEBREW_DIR}
echo -e "${TEMPLATE}" > Formula/vet.rb
git add Formula/vet.rb && git commit -m "Release $1" && git tag $1 && git push && git push --tags
cd -

rm -fr ${HOMEBREW_DIR}