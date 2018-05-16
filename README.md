[![Build Status][travis-image]][travis-url]
[![Maven Central Latest][maven-central-image]][maven-central-url]

# vet

Vet allows to review code on Gerrit using a pull-request workflow similar to GitHub, GitLab, Bitbucket and others.

## Install

### Linux - Debian based distributions

```
wget https://github.com/Cosium/vet/releases/download/3.10/vet-linux_x64.deb.zip -O vet.deb.zip \
&& unzip vet.deb.zip \
&& sudo dpkg -i vet.deb \
&& source /etc/bash_completion.d/vet_completion.sh \
&& rm vet.deb.zip vet.deb
```

### Mac OSX - [Homebrew](https://brew.sh/)

```
brew tap cosium/vet && brew install vet
```

### Windows - [Chocolatey](https://chocolatey.org/)

```
choco install gerrit-vet
```

### Manual

Download the binaries from https://github.com/Cosium/vet/releases

## Benefits

##### No more commit amend

Gerrit can't manage more than one commit per change set.  
Because of that, the widespread and cumbersome workflow is to have a single local commit and amend it every time you 
want to update the changeset.

Using Vet, Gerrit keeps seeing one commit per change set while you don't have to rewrite your history anymore.    
Each time you will ask to push to Gerrit, Vet will forge and push a single commit based on your source branch commit sequence.

##### No pre commit hook needed

Vet computes the Gerrit change id on the fly before pushing to Gerrit.  
Say bye to the pre-commit script.

##### No Gerrit REST or SSH api involved

Git is the only channel used by Vet to communicate with Gerrit.  
Vet doesn't need to know your credentials.

##### Use any git remote protocol
 
Because Vet delegates all Gerrit communication to git, your remote access protocol is only limited by git:

- file
- ssh
- http(s)
- ...

## Usage

### help

```bash
$ vet --help
usage: vet [--version] [--help] <command> [<args>]

<command> can be one of:
 new, checkout-new, checkout, push, untrack, status, pull, fire-and-forget

Debug options:
 --stacktrace      Print stacktraces
 --verbose         Enable verbose mode

Vet: The Gerrit client using pull request review workflow
```

### checkout-new

```bash
$ vet --help checkout-new
usage: vet checkout-new [-b <branch>] [-f]
 -b,--checkout-branch <branch>   The branch that will be created to track
                                 the change.
 -f,--force                      Forces the execution of the command,
                                 bypassing any confirmation prompt.
Creates a new change and tracks it from a new branch
```

### checkout

```bash
$ vet --help checkout
usage: vet checkout [-b <branch>] [-f] [-i <id>] [-t <branch>]
 -b,--checkout-branch <branch>   The branch that will be created to track
                                 the change.
 -f,--force                      Forces the execution of the command,
                                 bypassing any confirmation prompt.
 -i,--numeric-id <id>            The numeric id of the change.
 -t,--target-branch <branch>     The target branch of the change.
Tracks an existing change from a new branch
```

### push

```bash
$ vet --help push
usage: vet push [-f] [-p] [-s <subject>] [-v <vote>] [-w]
 -f,--bypass-review                 Submit directly the change bypassing
                                    the review. Neither labels nor submit
                                    rules are checked.
 -p,--publish-drafted-comments      Publish currently drafted comments of
                                    the change if any.
 -s,--patch-set-subject <subject>   The subject of the patch set.
 -v,--code-review-vote <vote>       Vote on code review. i.e. +1 is a
                                    valid vote value.
 -w,--work-in-progress              Turn the change to work in progress
                                    (e.g. wip).
Uploads modifications to the currently tracked change
```

### fire-and-forget

```bash
$ vet --help fire-and-forget
usage: vet fire-and-forget [-f] [-v <vote>]
 -f,--force                     Forces the execution of the command,
                                bypassing any confirmation prompt.
 -v,--code-review-vote <vote>   Vote on code review. i.e. +1 is a valid
                                vote value.
Creates a new untracked change then resets the current branch to change
```

### new

```bash
$ vet --help new
usage: vet new [-f] [-t <branch>]
 -f,--force                    Forces the execution of the command,
                               bypassing any confirmation prompt.
 -t,--target-branch <branch>   The id of the change.
Creates a new change and tracks it from the current branch.
```

### pull

```bash
$ vet --help pull
usage: vet pull

Pulls modifications of the currently tracked change
```

### status

```bash
$ vet --help status
usage: vet status

Displays the current status
```

### untrack

```bash
$ vet --help untrack
usage: vet untrack [-f]
 -f,--force   Forces the execution of the command, bypassing any
              confirmation prompt.
Untracks any tracked change
```

### track

```bash
$ vet --help track
usage: vet track [-b <branch>] [-f] [-i <id>] [-t <branch>]
 -i,--numeric-id <id>            The numeric id of the change.
 -t,--target-branch <branch>     The target branch of the change.
Tracks an existing change from the current branch
```

## Library

You will need JDK 9+.

```xml
<dependency>
   <groupId>com.cosium.vet</groupId>
   <artifactId>vet</artifactId>
   <version>${vet.version}</version>
</dependency>
```

## Build

You will need to have Docker installed to build the project. 

### Generating standalone binaries

Vet uses Java 9 Jlink to generate standalone binaries for Linux, MacOSX and Windows.

```
./gradlew binaries
```

Generated binaries can be found in `build/binaries`

[travis-image]: https://travis-ci.org/Cosium/vet.svg?branch=master
[travis-url]: https://travis-ci.org/Cosium/vet
[maven-central-image]: https://img.shields.io/maven-central/v/com.cosium.vet/vet.svg
[maven-central-url]: https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.cosium.vet%22%20AND%20a%3A%22vet%22
