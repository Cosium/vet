#!/bin/bash

_vet()
{
    local words="$(printf '%s ' "${COMP_WORDS[@]}")"
    COMPREPLY=()
    local possibilities=$(vet autocomplete -a "${words}" -i ${COMP_CWORD})
    COMPREPLY=(${possibilities})
    return 0
}
complete -F _vet vet