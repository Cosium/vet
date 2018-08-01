package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;

/**
 * Created on 01/08/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface ChangeParentBranchFactory {

  ChangeParent build(BranchShortName branch);
}
