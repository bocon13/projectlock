package com.googlesource.gerrit.plugins.projectlock;

import com.googlesource.gerrit.plugins.projectlock.NestedLock;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic tests for nested lock.
 */
public class NestedLockTest {

    @Test
    public void test() {
        NestedLock<String, Integer> lock = new NestedLock<>();

        assertTrue(lock.lock(1));
        assertEquals(lock.getValue(), Integer.valueOf(1));
        assertTrue(lock.lock(1));
        assertFalse(lock.lock(2));
        assertFalse(lock.unlock(2));
        assertTrue(lock.isLocked());
        assertTrue(lock.unlock(1));

        String key = "level1";
        assertTrue(lock.lock(3, key));
        assertEquals(lock.getValue(), null);
        assertEquals(lock.getValue(key), Integer.valueOf(3));
        assertTrue(lock.lock(3, key));
        assertFalse(lock.lock(4));
        assertFalse(lock.unlock(4));
        assertFalse(lock.isLocked());
        assertTrue(lock.isLocked(key));

        //lock at same level
        String key2 = "lock2";
        assertTrue(lock.lock(5, key2));
        assertEquals(lock.getValue(), null);
        assertEquals(lock.getValue(key2), Integer.valueOf(5));
        assertTrue(lock.lock(5, key2));
        assertTrue(lock.isLocked(key2));
        assertFalse(lock.isLocked("different key"));
        // unlock level2
        assertTrue(lock.unlock(3, key));
        assertTrue(lock.unlock(5, key2));

        // lock at top level again
        assertTrue(lock.lock(6));
        assertEquals(lock.getValue(), Integer.valueOf(6));
        assertTrue(lock.unlock(6));


        // build a tree
        assertTrue(lock.lock(7, "tree1"));
        assertTrue(lock.lock(8, "tree2", "branch1"));
        assertTrue(lock.lock(9, "tree2", "branch2"));
        assertFalse(lock.lock(10, "tree1", "branch1"));
        assertFalse(lock.lock(null, "tree2", "branch4"));
        assertFalse(lock.isLocked("tree2", "branch4"));
        assertTrue(lock.isLocked("tree1"));
        assertFalse(lock.isLocked("tree2"));
        assertFalse(lock.isLocked("tree3"));
        assertTrue(lock.isLocked("tree2", "branch1"));
        assertTrue(lock.isLocked("tree2", "branch2"));
        assertFalse(lock.isLocked("tree2", "branch3"));
        assertTrue(lock.isLocked("tree1", "branchFoo"));
        assertTrue(lock.isLocked("tree2", "branch1", "bar"));
        assertFalse(lock.isLocked("tree2", "branchFoo"));
        assertFalse(lock.unlock(null, "tree2", "branch2"));
        assertTrue(lock.isLocked("tree2", "branch2"));

        assertEquals(lock.getValue("tree2", "branch2"), Integer.valueOf(9));
        assertEquals(lock.getValue("tree2", "branch3"), null);
        assertEquals(lock.getValue("tree2", "branch2", "baz"), Integer.valueOf(9));
        assertFalse(lock.lock(11, "tree2", "branch2"));
        assertTrue(lock.unlock(8, "tree2", "branch1"));
        assertFalse(lock.isLocked("tree2", "branch1"));
        assertTrue(lock.isLocked("tree2", "branch2"));
        assertFalse(lock.unlock(9, "tree2", "branch2", "baz"));
        assertTrue(lock.isLocked("tree2", "branch2"));
        assertTrue(lock.unlock(9, "tree2", "branch2"));


    }

}
