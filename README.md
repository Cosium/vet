[![Build Status][travis-image]][travis-url]
[![Maven Central Latest][maven-central-image]][maven-central-url]

# vet

>vet
/vɛt/

>verb

>make a careful and critical examination of (something).
"proposals for vetting large takeover bids"
synonymes :	screen, assess, evaluate, appraise, weigh up, examine, look over, review, consider, scrutinize, study, inspect; Plus

Vet allows to review code on Gerrit using a pull-request workflow similar to GitHub, GitLab, Bitbucket and others.

## Why

##### No more commit amend

Gerrit can't manage more than one commit per change set.  
Because of that, the widespread and cumbersome workflow is to have a single local commit and amend it every time you 
want to update the changeset.

Using Vet, Gerrit keeps seeing one commit per change set while you don't have to rewrite your history anymore. 
Each time you will ask to push to Gerrit, Vet will forge and push a single commit based on your source branch commit sequence.

##### Now you can have feature branches

With Vet, you can have feature branches. 
If you set the correct Gerrit authorizations, you can push them as standard branches. 
Vet is able to manage git remote pushed or local only source branch the same way.

##### No pre commit hook needed

Vet computes the Gerrit change id on the fly before pushing to Gerrit.
Say bye to the pre-commit script.

##### No REST or SSH api involved

Git is the only channel used by Vet to communicate with Gerrit. 
Vet doesn't need to know your credentials.

#### Use any git remote protocol
 
Because Vet delegates all Gerrit communication to git, your remote access protocol is only limited by git:

- file
- ssh
- http(s)
- ...

## Usage

#### Command line interface

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

#### Library

```xml
<dependency>
   <groupId>com.cosium.vet</groupId>
   <artifactId>vet</artifactId>
   <version>${vet.version}</version>
</dependency>
```

[travis-image]: https://travis-ci.org/Cosium/vet.svg?branch=master
[travis-url]: https://travis-ci.org/Cosium/vet
[maven-central-image]: https://img.shields.io/maven-central/v/com.cosium.vet/vet.svg
[maven-central-url]: https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.cosium.vet%22%20AND%20a%3A%22vet%22