package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;

/**
 * Created on 08/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
interface GerritChangeFactory {

  GerritChange build(ChangeNumericId changeNumericId, BranchShortName targetBranch);
}
