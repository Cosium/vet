package com.cosium.vet.command.push;

import com.cosium.vet.command.AbstractVetAdvancedCommandArgParser;
import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.CodeReviewVote;
import com.cosium.vet.gerrit.PatchsetSubject;
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
public class PushCommandArgParser extends AbstractVetAdvancedCommandArgParser {

  private static final String COMMAND_NAME = "push";

  private static final String PUBLISH_DRAFTED_COMMENTS = "p";
  private static final String WORK_IN_PROGRESS = "w";
  private static final String PATCHSET_SUBJECT = "s";
  private static final String BYPASS_REVIEW = "f";
  private static final String CODE_REVIEW_VOTE = "v";

  private final PushCommandFactory pushCommandFactory;

  public PushCommandArgParser(PushCommandFactory pushCommandFactory) {
    super(
        new Options()
            .addOption(
                Option.builder(PUBLISH_DRAFTED_COMMENTS)
                    .numberOfArgs(0)
                    .longOpt("publish-drafted-comments")
                    .desc("Publish currently drafted comments of the change if any.")
                    .build())
            .addOption(
                Option.builder(WORK_IN_PROGRESS)
                    .numberOfArgs(0)
                    .longOpt("work-in-progress")
                    .desc("Turn the change to work in progress (e.g. wip).")
                    .build())
            .addOption(
                Option.builder(PATCHSET_SUBJECT)
                    .argName("subject")
                    .longOpt("patchset-subject")
                    .hasArg()
                    .desc("The subject of the patchset.")
                    .build())
            .addOption(
                Option.builder(BYPASS_REVIEW)
                    .numberOfArgs(0)
                    .longOpt("bypass-review")
                    .desc(
                        "Submit directly the change bypassing the review. Neither labels nor submit rules are checked.")
                    .build())
            .addOption(
                Option.builder(CODE_REVIEW_VOTE)
                    .argName("vote")
                    .longOpt("code-review-vote")
                    .hasArg()
                    .desc("Vote on code review. i.e. +1 is a valid vote value.")
                    .build()));
    this.pushCommandFactory = requireNonNull(pushCommandFactory);
  }

  @Override
  public void displayHelp(String executableName) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(
        String.format("%s %s", executableName, COMMAND_NAME),
        StringUtils.EMPTY,
        getOptions(),
        "Uploads modifications to the currently tracked change",
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
      commandLine = parser.parse(getOptions(), args);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    Boolean publishDraftedComments = commandLine.hasOption(PUBLISH_DRAFTED_COMMENTS) ? true : null;
    Boolean workInProgress = commandLine.hasOption(WORK_IN_PROGRESS) ? true : null;
    PatchsetSubject patchsetSubject =
        ofNullable(commandLine.getOptionValue(PATCHSET_SUBJECT))
            .filter(StringUtils::isNotBlank)
            .map(PatchsetSubject::of)
            .orElse(null);
    Boolean bypassReview = commandLine.hasOption(BYPASS_REVIEW) ? true : null;
    CodeReviewVote reviewVote =
        ofNullable(commandLine.getOptionValue(CODE_REVIEW_VOTE))
            .filter(StringUtils::isNotBlank)
            .map(CodeReviewVote::of)
            .orElse(null);

    return pushCommandFactory.build(
        publishDraftedComments, workInProgress, patchsetSubject, bypassReview, reviewVote);
  }
}
