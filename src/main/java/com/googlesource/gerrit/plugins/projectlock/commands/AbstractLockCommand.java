package com.googlesource.gerrit.plugins.projectlock.commands;

import com.google.common.collect.ImmutableList;
import com.google.gerrit.reviewdb.client.Branch;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.project.ProjectControl;
import com.google.gerrit.sshd.SshCommand;
import com.google.gwtorm.client.Key;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.projectlock.ProjectLockStore;
import org.kohsuke.args4j.Argument;

import static com.google.common.base.Strings.isNullOrEmpty;

public abstract class AbstractLockCommand extends SshCommand {

    @Argument(index = 0, metaVar = "PROJECT", usage = "name of the project to be locked")
    private ProjectControl project;

    @Argument(index = 1, metaVar = "BRANCH", usage = "name of branch to be locked")
    private String branch;

    @Inject
    protected ProjectLockStore lockStore;

    @Inject
    protected CurrentUser user;

    protected ImmutableList<Key> getLockKeys() {
        ImmutableList.Builder<Key> keys = ImmutableList.builder();
        if (project != null) {
            Project.NameKey projectKey = project.getProject().getNameKey();
            keys.add(projectKey);
            if (!isNullOrEmpty(branch)) {
                Branch.NameKey branchKey = new Branch.NameKey(projectKey, branch);
                //TODO check to see if branch exists
                keys.add(branchKey);
            }
        }
        return keys.build();
    }
}

