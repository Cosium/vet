package com.cosium.vet.push;

import com.cosium.vet.command.VetAdvancedCommandArgParser;
import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.thirdparty.apache_commons_cli.*;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommandArgParser implements VetAdvancedCommandArgParser {

  private static final String COMMAND_NAME = "push";
  private static final String TARGET_BRANCH = "b";
  private static final String PUBLISH_DRAFTED_COMMENTS = "p";
  private static final String WORK_IN_PROGRESS = "w";
  private static final String PATCH_SET_SUBJECT = "s";
  private static final String BYPASS_REVIEW = "f";

  private final PushCommandFactory pushCommandFactory;
  private final Options options;

  public PushCommandArgParser(PushCommandFactory pushCommandFactory) {
    requireNonNull(pushCommandFactory);
    this.pushCommandFactory = pushCommandFactory;
    options = new Options();
    options.addOption(
        Option.builder(TARGET_BRANCH)
            .argName("branch-name")
            .longOpt("target-branch")
            .hasArg()
            .desc("The branch targeted by the changes. If not set, it will be asked if needed.")
            .build());
    options.addOption(
        Option.builder(PUBLISH_DRAFTED_COMMENTS)
            .numberOfArgs(0)
            .longOpt("publish-drafted-comments")
            .desc("Publish currently drafted comments of the change set if any.")
            .build());
    options.addOption(
        Option.builder(WORK_IN_PROGRESS)
            .numberOfArgs(0)
            .longOpt("work-in-progress")
            .desc("Turn the change to work in progress (e.g. wip).")
            .build());
    options.addOption(
        Option.builder(PATCH_SET_SUBJECT)
            .argName("subject")
            .longOpt("patch-set-subject")
            .hasArg()
            .desc("The subject of the patch set.")
            .build());
    options.addOption(
        Option.builder(BYPASS_REVIEW)
            .numberOfArgs(0)
            .longOpt("bypass-review")
            .desc(
                "Submit directly the change bypassing the review. Neither labels nor submit rules are checked.")
            .build());
  }

  @Override
  public void displayHelp(String executableName) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(
        String.format("%s %s", executableName, COMMAND_NAME),
        StringUtils.EMPTY,
        options,
        "Push the changes to Gerrit by adding a new patch set to the current change set.\n"
            + "If no change set exists, the patch set will be appended to a new one.",
        true);
  }

  @Override
  public String getCommandArgName() {
    return COMMAND_NAME;
  }

  @Override
  public boolean canParse(String... args) {
    return Arrays.stream(args).anyMatch(COMMAND_NAME::equals);
  }

  @Override
  public VetCommand parse(String... args) {
    CommandLineParser parser = new DefaultParser();
    CommandLine commandLine;
    try {
      commandLine = parser.parse(options, args);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    BranchShortName targetBranch =
        ofNullable(commandLine.getOptionValue(TARGET_BRANCH))
            .filter(StringUtils::isNotBlank)
            .map(BranchShortName::of)
            .orElse(null);
    Boolean publishDraftedComments = commandLine.hasOption(PUBLISH_DRAFTED_COMMENTS) ? true : null;
    Boolean workInProgress = commandLine.hasOption(WORK_IN_PROGRESS) ? true : null;
    PatchSetSubject patchSetSubject =
        ofNullable(commandLine.getOptionValue(PATCH_SET_SUBJECT))
            .filter(StringUtils::isNotBlank)
            .map(PatchSetSubject::of)
            .orElse(null);
    Boolean bypassReview = commandLine.hasOption(BYPASS_REVIEW) ? true : null;

    return pushCommandFactory.build(
        targetBranch, publishDraftedComments, workInProgress, patchSetSubject, bypassReview);
  }
}
