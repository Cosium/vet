package com.cosium.vet.push;

import com.cosium.vet.VetCommand;
import com.cosium.vet.VetCommandArgParser;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.thirdparty.apache_commons_cli.*;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommandArgParser implements VetCommandArgParser {

  private static final String COMMAND_NAME = "push";
  private static final String TARGET_BRANCH = "b";
  private static final String PATCH_SET_SUBJECT = "s";

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
            .desc("The branch targeted by the changes. Default to 'master'.")
            .build());
    options.addOption(
        Option.builder(PATCH_SET_SUBJECT)
            .argName("subject")
            .longOpt("patch-set-subject")
            .hasArg()
            .desc("The subject of the patch set.")
            .build());
    options.addOption(Option.builder("h").longOpt("help").build());
  }

  @Override
  public void displayHelp(String executableName) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setOptionComparator(null);
    formatter.printHelp(
        String.format("%s %s", executableName, COMMAND_NAME),
        "Push the changes to Gerrit by adding a new patch set.",
        options,
        "If the change set does not exist, patch set will be appended to a new one, otherwise to the existing one.",
        true);
  }

  @Override
  public String getCommandArgName() {
    return COMMAND_NAME;
  }

  @Override
  public boolean canParse(String... args) {
    return args.length != 0 && COMMAND_NAME.equals(args[0]);
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
    PatchSetSubject patchSetSubject =
        ofNullable(commandLine.getOptionValue(PATCH_SET_SUBJECT))
            .filter(StringUtils::isNotBlank)
            .map(PatchSetSubject::of)
            .orElse(null);

    return pushCommandFactory.build(targetBranch, patchSetSubject);
  }
}
