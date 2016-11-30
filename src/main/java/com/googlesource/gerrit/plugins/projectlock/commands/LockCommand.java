package com.googlesource.gerrit.plugins.projectlock.commands;

import com.google.common.collect.ImmutableList;
import com.google.gerrit.sshd.CommandMetaData;
import com.google.gwtorm.client.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//FIXME users should have a required capability
//@RequiresCapability(AdminCookbookCapability.ADMIN_COOKBOOK)
@CommandMetaData(name = "lock", description = "Lock the project or branch")
public final class LockCommand extends AbstractLockCommand {
    private static final Logger log = LoggerFactory.getLogger(LockCommand.class);

    @Override
    protected void run() throws Failure {
        if (isAllowed()) {
            ImmutableList<Key> keys = getLockKeys();
            if (lockStore.get().lock(user.getAccountId(), keys)) {
                log.info("Locking: {}",
                         lockStore.lockString(user.getAccountId(), keys));
            } else {
                throw new UnloggedFailure("Lock failed");
            }
        } else {
            throw new Failure(1, "User is not permitted to lock this project\n");
        }
    }
}

