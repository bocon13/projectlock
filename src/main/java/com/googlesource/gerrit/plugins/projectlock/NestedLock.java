package com.googlesource.gerrit.plugins.projectlock;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * FIXME
 */
public class NestedLock<K, V> {
    private final Map<K, NestedLock<K, V>> locks = Maps.newConcurrentMap();
    private final LockStub<K, V> stub = new LockStub<>();

    public boolean lock(V newValue, K... keys) {
        if (keys.length == 0) {
            return locks.size() == 0 && stub.lock(newValue);
        }
        if (stub.getValue() != null) {
            return false;
        }
        K key = keys[0];
        NestedLock<K, V> lock = locks.computeIfAbsent(key, k -> new NestedLock<>());
        return lock.lock(newValue, Arrays.copyOfRange(keys, 1, keys.length));
    }

    public boolean unlock(V existingValue, K... keys) {
        if (keys.length == 0) {
            return locks.size() == 0 && stub.unlock(existingValue);
        }
        if (stub.getValue() != null) {
            return false;
        }
        K key = keys[0];
        AtomicBoolean success = new AtomicBoolean(false);
        locks.computeIfPresent(key, (k, lock) -> {
            if (lock.unlock(existingValue, Arrays.copyOfRange(keys, 1, keys.length))) {
                success.set(true);
                return lock.isEmpty() ? null : lock;
            } else {
                return lock;
            }
        });
        return success.get();
    }

    private boolean isEmpty() {
        return locks.size() == 0 && stub.getValue() == null;
    }

    public boolean isLocked(K... keys) {
        if (stub.getValue() != null) {
            return true;
        }
        if (keys.length == 0) {
            return false;
        }
        NestedLock<K, V> lock = locks.get(keys[0]);
        if (lock == null) {
            return false;
        }
        return lock.isLocked(Arrays.copyOfRange(keys, 1, keys.length));
    }

    public V getValue(K... keys) {
        V value = stub.getValue();
        if (value != null) {
            Preconditions.checkState(locks.size() == 0);
            return value;
        }
        if (keys.length == 0) {
            return null;
        }
        NestedLock<K, V> lock = locks.get(keys[0]);
        if (lock == null) {
            return null;
        }
        return lock.getValue(Arrays.copyOfRange(keys, 1, keys.length));
    }

    private static class LockStub<K, V> {
        private final AtomicReference<V> value;

        LockStub() {
            value = new AtomicReference<V>();
        }

        boolean lock(V newValue) {
            if (newValue == null) {
                return false;
            }

            final AtomicBoolean success = new AtomicBoolean(false);
            value.getAndUpdate(storedValue -> {
                if (storedValue == null) {
                    success.set(true);
                    return newValue;
                } else {
                    success.set(storedValue.equals(newValue));
                    return storedValue;
                }
            });
            return success.get();
        }

        boolean unlock(V existingValue) {
            if (existingValue == null) {
                return false;
            }

            final AtomicBoolean success = new AtomicBoolean(false);
            value.getAndUpdate(storedValue -> {
                if (Objects.equals(storedValue, existingValue)) {
                    success.set(true);
                    return null;
                } else {
                    return storedValue;
                }
            });
            return success.get();
        }

        V getValue() {
            return value.get();
        }
    }
}
