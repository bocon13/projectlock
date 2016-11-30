package com.googlesource.gerrit.plugins.projectlock.commands;

import com.google.gerrit.sshd.CommandMetaData;

//FIXME users should have a required capability
//@RequiresCapability(AdminCookbookCapability.ADMIN_COOKBOOK)
@CommandMetaData(name = "lock", description = "Lock the project or branch")
public final class LockCommand extends AbstractLockCommand {

    @Override
    protected void run() {
        if (isAllowed()) {
            if (!lockStore.get().lock(user.getAccountId(), getLockKeys())) {
                stdout.print("Lock failed\n");
            }
        } else {
            stdout.print("User is not permitted to lock this project\n");
        }
    }
}

