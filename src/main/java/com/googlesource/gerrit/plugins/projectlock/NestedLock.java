package com.googlesource.gerrit.plugins.projectlock;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

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
        return lock(newValue, ImmutableList.copyOf(keys));
    }

    public boolean lock(V newValue, ImmutableList<K> keys) {
        if (keys == null || keys.size() == 0) {
            return locks.size() == 0 && stub.lock(newValue);
        }
        if (stub.getValue() != null) {
            return false;
        }
        K key = keys.get(0);
        NestedLock<K, V> lock = locks.computeIfAbsent(key, k -> new NestedLock<>());
        return lock.lock(newValue, keys.subList(1, keys.size()));
    }

    public boolean unlock(V newValue, K... keys) {
        return unlock(newValue, ImmutableList.copyOf(keys));
    }

    public boolean unlock(V existingValue, ImmutableList<K> keys) {
        if (keys == null || keys.size() == 0) {
            return locks.size() == 0 && stub.unlock(existingValue);
        }
        if (stub.getValue() != null) {
            return false;
        }
        K key = keys.get(0);
        AtomicBoolean success = new AtomicBoolean(false);
        locks.computeIfPresent(key, (k, lock) -> {
            if (lock.unlock(existingValue, keys.subList(1, keys.size()))) {
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
        return isLocked(ImmutableList.copyOf(keys));
    }

    public boolean isLocked(ImmutableList<K> keys) {
        if (stub.getValue() != null) {
            return true;
        }
        if (keys == null || keys.size() == 0) {
            return false;
        }
        NestedLock<K, V> lock = locks.get(keys.get(0));
        if (lock == null) {
            return false;
        }
        return lock.isLocked(keys.subList(1, keys.size()));
    }

    public V getValue(K... keys) {
        return getValue(ImmutableList.copyOf(keys));
    }
    public V getValue(ImmutableList<K> keys) {
        V value = stub.getValue();
        if (value != null) {
            Preconditions.checkState(locks.size() == 0);
            return value;
        }
        if (keys == null || keys.size() == 0) {
            return null;
        }
        NestedLock<K, V> lock = locks.get(keys.get(0));
        if (lock == null) {
            return null;
        }
        return lock.getValue(keys.subList(1, keys.size()));
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
