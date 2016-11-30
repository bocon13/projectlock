package com.googlesource.gerrit.plugins.projectlock;

import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.reviewdb.client.AccountExternalId;
import com.google.gerrit.reviewdb.client.Branch;
import com.google.gerrit.reviewdb.client.PatchSet;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.account.AccountCache;
import com.google.gerrit.server.git.CodeReviewCommit;
import com.google.gerrit.server.git.validators.MergeValidationException;
import com.google.gerrit.server.git.validators.MergeValidationListener;
import com.google.gerrit.server.project.ProjectState;
import com.google.gwtorm.client.Key;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * FIXME.
 */
public class ProjectLockedValidator implements MergeValidationListener {
    private static final Logger log =
            LoggerFactory.getLogger(ProjectLockedValidator.class);

    private final Provider<ReviewDb> reviewDb;
    private final AccountCache accounts;
    private final ProjectLockStore lockStore;

    @Inject
    ProjectLockedValidator(Provider<ReviewDb> reviewDb,
                           ProjectLockStore lockStore,
                           AccountCache accountCache) {
        this.reviewDb = reviewDb;
        this.lockStore = lockStore;
        this.accounts = accountCache;
        log.info(lockStore + " " + accountCache);
    }

    /**
     * Reject merges if the submitter does not have the appropriate file permissions.
     */
    @Override
    public void onPreMerge(Repository repo, CodeReviewCommit commit,
                           ProjectState destProject, Branch.NameKey destBranch,
                           PatchSet.Id patchSetId, IdentifiedUser caller)
            throws MergeValidationException {
        Project.NameKey projectKey = destProject.getProject().getNameKey();

        Account.Id lockingUser = lockStore.get().getValue(projectKey, destBranch);
        // If a user, other than the caller, has locked the tree...
        if (lockingUser != null && !lockingUser.equals(caller.getAccountId())) {
            Account account = accounts.get(lockingUser).getAccount();
            throw new MergeValidationException(String.format(
                    "Submits are currently blocked by %s",
                    account.getNameEmail("Unknown User")));
        }

//        throw new MergeValidationException("would have submitted!");
    }
}
