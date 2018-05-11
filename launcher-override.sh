#!/bin/bash
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

# Preserve quotes on quoted arguments
WHITESPACE="[[:space:]]"
RECEIVED_ARGUMENTS=()
for arg in "$@"
do
    if [[ ${arg} =~ $WHITESPACE ]]
    then
        arg=\"${arg}\"
    fi
    RECEIVED_ARGUMENTS+=("${arg}")
done

JLINK_VM_OPTIONS=
$DIR/java $JLINK_VM_OPTIONS -m com.cosium.vet/com.cosium.vet.App "${RECEIVED_ARGUMENTS[@]}"