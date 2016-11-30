package com.googlesource.gerrit.plugins.projectlock.commands;

import com.google.gerrit.reviewdb.client.Branch;
import com.google.gerrit.server.account.AccountCache;
import com.google.gerrit.sshd.CommandMetaData;
import com.google.gerrit.sshd.SshCommand;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.projectlock.ProjectLockStore;

//FIXME users should have a required capability
//@RequiresCapability(AdminCookbookCapability.ADMIN_COOKBOOK)
@CommandMetaData(name = "ls", description = "List the current locks")
public final class LockListCommand extends SshCommand {
    @Inject
    private ProjectLockStore lockStore;

    @Inject
    private AccountCache accountCache;

    @Override
    protected void run() {
        lockStore.get().getEntries().forEach(e -> {
            StringBuilder lockEntry = new StringBuilder();
            if (e.prefix().size() == 0) {
                lockEntry.append("(root)");
            } else {
                e.prefix().forEach(k -> {
                    if (k instanceof Branch.NameKey) {
                        lockEntry.append(((Branch.NameKey) k).get());
                    } else {
                        lockEntry.append(k);
                    }
                    lockEntry.append(':');
                });
            }
            lockEntry.append('\t');
            lockEntry.append(accountCache.get(e.value())
                                     .getAccount().getNameEmail("Unknown Name"));
            lockEntry.append('\n');
            stdout.print(lockEntry);
        });
    }
}

