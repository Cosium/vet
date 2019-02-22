package com.cosium.vet.git;

import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.runtime.CommandRunner;
import com.cosium.vet.runtime.Environment;
import com.cosium.vet.thirdparty.apache_commons_lang3.ArrayUtils;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class BasicGitClient implements GitClient {

  private static final Logger LOG = LoggerFactory.getLogger(BasicGitClient.class);

  private static final String GIT = "git";

  private final Path repositoryDirectory;
  private final CommandRunner commandRunner;
  private final GitConfigRepository gitConfigRepository;
  private final Environment gitEnvironment;

  BasicGitClient(
      Path repositoryDirectory,
      CommandRunner commandRunner,
      GitConfigRepository gitConfigRepository,
      Environment gitEnvironment) {
    this.repositoryDirectory = requireNonNull(repositoryDirectory);
    this.commandRunner = requireNonNull(commandRunner);
    this.gitConfigRepository = requireNonNull(gitConfigRepository);
    this.gitEnvironment = requireNonNull(gitEnvironment);
  }

  @Override
  public BranchShortName getBranch() {
    return BranchShortName.of(git("symbolic-ref", "--short", "HEAD"));
  }

  @Override
  public Optional<RemoteName> getRemote(BranchShortName branch) {
    return ofNullable(gitConfigRepository.getValue(String.format("branch.%s.remote", branch)))
        .filter(StringUtils::isNotBlank)
        .map(RemoteName::of);
  }

  public Optional<RemoteUrl> getRemotePushUrl(RemoteName remoteName) {
    String pushUrlConfigKey = String.format("remote.%s.pushurl", remoteName);
    String pushUrl = gitConfigRepository.getValue(pushUrlConfigKey);
    if (!StringUtils.isBlank(pushUrl)) {
      return Optional.of(RemoteUrl.of(pushUrl));
    }
    String urlConfigKey = String.format("remote.%s.url", remoteName);
    LOG.debug("No value found for '{}'. Falling back on '{}'", pushUrlConfigKey, urlConfigKey);
    return Optional.ofNullable(gitConfigRepository.getValue(urlConfigKey))
        .filter(StringUtils::isNotBlank)
        .map(RemoteUrl::of);
  }

  @Override
  public String getMostRecentCommonCommit(String otherBranch) {
    return git("merge-base", "HEAD", otherBranch);
  }

  @Override
  public String getTree() {
    return git("rev-parse", "HEAD:");
  }

  @Override
  public String commitTree(String tree, String parent, CommitMessage commitMessage) {
    return git("commit-tree", tree, "-p", parent, "-m", commitMessage.toString());
  }

  @Override
  public CommitMessage getLastCommitMessage() {
    return CommitMessage.of(git("log", "-1", "--pretty=%B"));
  }

  @Override
  public CommitMessage getCommitMessage(RevisionId revisionId) {
    return CommitMessage.of(git("log", revisionId.toString(), "-1", "--pretty=%B"));
  }

  @Override
  public RevisionId getParent(RevisionId revisionId) {
    return RevisionId.of(git("log", "--pretty=%P", "-n", "1", revisionId.toString()));
  }

  @Override
  public String push(String remote, String refspec) {
    return git("push", remote, refspec);
  }

  @Override
  public List<BranchRef> listRemoteRefs(RemoteName remote) {
    String output = git("ls-remote", remote.toString());
    Pattern refMatcher = Pattern.compile("(.*?)\\s+(.*?)$");
    return Arrays.stream(output.split("\n"))
        .map(refMatcher::matcher)
        .filter(Matcher::find)
        .map(
            matcher ->
                new BranchRef(RevisionId.of(matcher.group(1)), BranchRefName.of(matcher.group(2))))
        .collect(Collectors.toList());
  }

  @Override
  public void fetch(RemoteName remote, BranchRefName branchRefName) {
    git("fetch", remote.toString(), branchRefName.toString());
  }

  @Override
  public void fetch(RemoteName remote, BranchShortName branchShortName) {
    git("fetch", remote.toString(), branchShortName.toString());
  }

  @Override
  public String pull(RemoteName remote, BranchRefName branchRefName) {
    return git("pull", remote.toString(), branchRefName.toString());
  }

  @Override
  public String status() {
    return git("status");
  }

  @Override
  public String checkoutFetchHead() {
    return git("checkout", "FETCH_HEAD");
  }

  @Override
  public String checkoutNewBranch(BranchShortName branchShortName) {
    return git("checkout", "-b", branchShortName.toString());
  }

  @Override
  public String resetKeep(RevisionId revisionId) {
    return git("reset", "--keep", revisionId.toString());
  }

  private String git(String... args) {
    return commandRunner.run(repositoryDirectory, gitEnvironment, ArrayUtils.insert(0, args, GIT));
  }
}
