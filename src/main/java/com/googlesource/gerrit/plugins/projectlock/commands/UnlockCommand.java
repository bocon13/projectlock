package com.googlesource.gerrit.plugins.projectlock.commands;

import com.google.gerrit.sshd.CommandMetaData;

//FIXME users should have a required capability
//@RequiresCapability(AdminCookbookCapability.ADMIN_COOKBOOK)
@CommandMetaData(name = "unlock", description = "Unlock the project or branch")
public final class UnlockCommand extends AbstractLockCommand {

    @Override
    protected void run() {
        if (isAllowed()) {
            if(!lockStore.get().unlock(user.getAccountId(), getLockKeys())) {
                stdout.print("Unlock failed\n");
            }
        } else {
            stdout.print("User is not permitted to lock this project\n");
        }
    }
}

