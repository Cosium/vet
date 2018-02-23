package com.cosium.vet.push;

import com.cosium.vet.VetCommand;
import com.cosium.vet.VetCommandArgParser;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.git.BranchShortName;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommandArgParser implements VetCommandArgParser {

  private static final Logger LOG = LoggerFactory.getLogger(PushCommandArgParser.class);
  private static final String COMMAND_NAME = "push";
  private static final String TARGET_BRANCH = "b";
  private static final String PATCH_SET_SUBJECT = "s";

  private final PushCommandFactory pushCommandFactory;

  public PushCommandArgParser(PushCommandFactory pushCommandFactory) {
    requireNonNull(pushCommandFactory);
    this.pushCommandFactory = pushCommandFactory;
  }

  @Override
  public Optional<VetCommand> parse(String... args) {
    if (args.length == 0) {
      LOG.trace("Argument array is empty. Not a push command.");
      return Optional.empty();
    }
    if (!COMMAND_NAME.equals(args[0])) {
      LOG.trace("First argument doesn't match {}. Not a push command.", COMMAND_NAME);
      return Optional.empty();
    }
    LOG.trace("This is a push command");

    Options options = new Options();
    options.addOption(
        Option.builder(TARGET_BRANCH)
            .argName("branch-name")
            .longOpt("target-branch")
            .hasArg()
            .desc("The branch targeted by the changes. Default value is 'master'.")
            .build());
    options.addOption(
        Option.builder(PATCH_SET_SUBJECT)
            .argName("subject")
            .longOpt("patch-set-subject")
            .hasArg()
            .desc("The subject of the patch set")
            .build());

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
    PatchSetSubject patchSetSubject =
        ofNullable(commandLine.getOptionValue(PATCH_SET_SUBJECT))
            .filter(StringUtils::isNotBlank)
            .map(PatchSetSubject::of)
            .orElse(null);

    return Optional.of(pushCommandFactory.build(targetBranch, patchSetSubject));
  }
}
