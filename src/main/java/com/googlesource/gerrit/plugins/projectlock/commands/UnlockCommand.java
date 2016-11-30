package com.googlesource.gerrit.plugins.projectlock.commands;

import com.google.common.collect.ImmutableList;
import com.google.gerrit.sshd.CommandMetaData;
import com.google.gwtorm.client.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//FIXME users should have a required capability
//@RequiresCapability(AdminCookbookCapability.ADMIN_COOKBOOK)
@CommandMetaData(name = "unlock", description = "Unlock the project or branch")
public final class UnlockCommand extends AbstractLockCommand {
    private static final Logger log = LoggerFactory.getLogger(UnlockCommand.class);

    @Override
    protected void run() throws Failure {
        if (isAllowed()) {
            ImmutableList<Key> keys = getLockKeys();
            if (lockStore.get().unlock(user.getAccountId(), keys)) {
                log.info("Unlocking: {}",
                         lockStore.lockString(user.getAccountId(), keys));
            } else {
                throw new UnloggedFailure("Unlock failed");
            }
        } else {
            throw new Failure(1, "User is not permitted to lock this project\n");
        }
    }
}

