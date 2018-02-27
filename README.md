[![Build Status][travis-image]][travis-url]

# vet

```bash
$ vet --help
usage: vet [--version] [--help] <command> [<args>]

<command> can be one of:
 push

Debug options:
 --stackstrace      Print stacktraces
 --verbose         Enable verbose mode

Vet: The Gerrit client using pull request review workflow
```

```bash
$ vet --help
usage: vet push [-b <branch-name>] [-s <subject>] [-h]
 -b,--target-branch <branch-name>   The branch targeted by the changes. If
                                    not set, it will be asked if needed.
 -s,--patch-set-subject <subject>   The subject of the patch set. If not
                                    set, it will be asked if needed.
 -h,--help
Push the changes to Gerrit by adding a new patch set to the current change
set.
If no change set exists, patch set will be appended to a new one.
```

[travis-image]: https://travis-ci.org/Cosium/vet.svg?branch=master
[travis-url]: https://travis-ci.org/Cosium/vet