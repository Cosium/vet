[![Build Status][travis-image]][travis-url]
[![Maven Central Latest][maven-central-image]][maven-central-url]

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
$ vet --help push
usage: vet push [-b <branch-name>] [-s <subject>] [-h]
 -b,--target-branch <branch-name>   The branch targeted by the changes. If
                                    not set, it will be asked if needed.
 -s,--patch-set-subject <subject>   The subject of the patch set. If not
                                    set, it will be asked if needed.
Push the changes to Gerrit by adding a new patch set to the current change
set.
If no change set exists, patch set will be appended to a new one.
```

[travis-image]: https://travis-ci.org/Cosium/vet.svg?branch=master
[travis-url]: https://travis-ci.org/Cosium/vet
[maven-central-image]: https://img.shields.io/maven-central/v/com.cosium.vet/vet.svg
[maven-central-url]: https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.cosium.vet%22%20AND%20a%3A%22vet%22