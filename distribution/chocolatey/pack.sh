#!/usr/bin/env bash
docker run --rm -u $(id -u) -v "$(pwd):/workdir" telyn/chocolatey sh -c "cd /workdir && mono /usr/bin/choco.exe pack && rm -fr usr"