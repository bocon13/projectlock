package com.googlesource.gerrit.plugins.projectlock;

import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.reviewdb.client.Branch;
import com.google.gerrit.server.account.AccountCache;
import com.google.gwtorm.client.Key;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

/**
 * FIXME.
 */
@Singleton
public class ProjectLockStore {

    private final NestedLock<Key, Account.Id> locks = new NestedLock<>();

    @Inject
    private AccountCache accountCache;


    public NestedLock<Key, Account.Id> get() {
        return locks;
    }

    public String lockString(Account.Id id, List<Key> keys) {
        StringBuilder lockEntry = new StringBuilder();
        if (keys.size() == 0) {
            lockEntry.append("(root)");
        } else {
            keys.forEach(k -> {
                if (k instanceof Branch.NameKey) {
                    lockEntry.append(((Branch.NameKey) k).get());
                } else {
                    lockEntry.append(k);
                }
                lockEntry.append(':');
            });
        }
        lockEntry.append('\t');
        lockEntry.append(accountCache.get(id)
                                 .getAccount().getNameEmail("Unknown Name"));
        lockEntry.append('\n');
        return lockEntry.toString();
    }

}