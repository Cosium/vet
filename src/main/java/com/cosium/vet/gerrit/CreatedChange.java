package com.cosium.vet.gerrit;

/**
 * Created on 12/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface CreatedChange extends Change {

    /**
     * @return The log produced while creating the change
     */
    String getCreationLog();

}
