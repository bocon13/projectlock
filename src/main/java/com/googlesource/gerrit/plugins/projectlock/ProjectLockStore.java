package com.googlesource.gerrit.plugins.projectlock;

import com.google.gerrit.reviewdb.client.Account;
import com.google.gwtorm.client.Key;
import com.google.inject.Singleton;

/**
 * FIXME.
 */
@Singleton
public class ProjectLockStore {

    private final NestedLock<Key, Account.Id> locks = new NestedLock<>();

    public NestedLock<Key, Account.Id> get() {
        return locks;
    }
}