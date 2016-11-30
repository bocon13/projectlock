package com.googlesource.gerrit.plugins.projectlock.commands;

import com.google.gerrit.common.data.GlobalCapability;
import com.google.gerrit.extensions.annotations.RequiresCapability;
import com.google.gerrit.sshd.CommandMetaData;
import com.google.gerrit.sshd.SshCommand;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.projectlock.ProjectLockStore;

//FIXME users should have a required capability
@RequiresCapability(GlobalCapability.ADMINISTRATE_SERVER)
@CommandMetaData(name = "ls", description = "List the current locks")
public final class LockListCommand extends SshCommand {
    @Inject
    private ProjectLockStore lockStore;

    @Override
    protected void run() {
        lockStore.get().getEntries().forEach(e -> {
            stdout.print(lockStore.lockString(e.value(), e.prefix()));
        });
    }
}

