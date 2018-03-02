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

## Install

### Linux - Debian based distributions

```
wget https://github.com/Cosium/vet/releases/download/1.1/vet-linux_x64.deb.zip -O vet.deb.zip \
&& unzip vet.deb.zip \
&& sudo dpkg -i vet.deb \
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

Download the binaries from from https://github.com/Cosium/vet/releases

## Benefits

##### No more commit amend

Gerrit can't manage more than one commit per change set.  
Because of that, the widespread and cumbersome workflow is to have a single local commit and amend it every time you 
want to update the changeset.

Using Vet, Gerrit keeps seeing one commit per change set while you don't have to rewrite your history anymore.    
Each time you will ask to push to Gerrit, Vet will forge and push a single commit based on your source branch commit sequence.

##### Now you can have feature branches

With Vet, you can have feature branches.  
If you set the correct Gerrit authorizations, you can push them as standard branches.  
Vet is able to manage git remote pushed or local only source branches the same way.

##### Checkout the feature branch to test or hack the changeset

As a reviewer you want to test the change set locally? 
Just checkout the feature branch.  

Now maybe you want to contribute to the change set?  
Run Vet push and your changes are added to the correct change set.

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

### Push

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

```bash
$ vet push --patch-set-subject "Add bar"
remote: Processing changes: new: 1, done            
remote: 
remote: New Changes:        
remote:   http://localhost:38391/#/c/foo/+/1001 The topic        
remote: 
To http://localhost:38391/foo
 * [new branch]      ab1e39bc82d81d5ed80e2357e67f57e47d4a4cd0 -> refs/for/master%m=Add_bar
From http://localhost:38391/foo
 * branch            refs/changes/01/1001/1 -> FETCH_HEAD
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

## Limitation

Since Vet has its own change id computation system, all clients of the same Gerrit instance must use Vet.

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
