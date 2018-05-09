package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitUtils;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;
import com.cosium.vet.utils.NonBlankString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultChange implements Change {

  private final PatchSetRepository patchSetRepository;
  private final BranchShortName targetBranch;
  private final ChangeNumericId numericId;

  private DefaultChange(
      PatchSetRepository patchSetRepository,
      BranchShortName targetBranch,
      ChangeNumericId numericId) {
    this.patchSetRepository = requireNonNull(patchSetRepository);
    this.targetBranch = requireNonNull(targetBranch);
    this.numericId = requireNonNull(numericId);
  }

  @Override
  public void createPatchSet(
      boolean publishDraftComments,
      boolean workInProgress,
      PatchSetSubject subject,
      boolean bypassReview) {
    patchSetRepository.createPatch(
        targetBranch,
        numericId,
        buildPatchSetOptions(publishDraftComments, workInProgress, subject, bypassReview));
  }

  private String buildPatchSetOptions(
      boolean publishDraftedComments,
      boolean workInProgress,
      PatchSetSubject subject,
      boolean bypassReview) {
    List<String> options = new ArrayList<>();
    options.add(publishDraftedComments ? "publish-comments" : null);
    options.add(workInProgress ? "wip" : null);
    options.add(
        ofNullable(subject)
            .map(NonBlankString::toString)
            .map(GitUtils::encodeForGitRef)
            .map(s -> String.format("m=%s", s))
            .orElse(null));
    options.add(bypassReview ? "submit" : null);
    return options.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
  }

  /**
   * Created on 08/05/18.
   *
   * @author Reda.Housni-Alaoui
   */
  static class Factory implements ChangeFactory {

    private final PatchSetRepository patchSetRepository;

    Factory(PatchSetRepository patchSetRepository) {
      this.patchSetRepository = requireNonNull(patchSetRepository);
    }

    @Override
    public Change build(BranchShortName targetBranch, ChangeNumericId changeNumericId) {
      return new DefaultChange(patchSetRepository, targetBranch, changeNumericId);
    }
  }

  @Override
  public String toString() {
    return "{\"numericId\": " + numericId + ", \"targetBranch\": \"" + targetBranch + "\"}";
  }
}
