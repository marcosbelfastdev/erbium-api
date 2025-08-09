/**
 * Class Name: LockManager
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 * Description: [Brief description of what this class does]
 *
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */
package br.com.erbium.core.lock;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Supplier;

/**
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 * Description: [Brief description of what this class does]
 *
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */

public class LockManager<T extends Enum<T>> {

    private final EnumSet<T> lockedActions;

    
    public LockManager(Class<T> actionType) {
        this.lockedActions = EnumSet.noneOf(actionType);
    }

    
    public void lock(T action) {
        lockedActions.add(action);
    }

    
    @SafeVarargs
    public final void lock(T... actions) {
        lockedActions.addAll(Arrays.asList(actions));
    }

    
    public void unlock(T action) {
        lockedActions.remove(action);
    }

    
    public boolean isLocked(T action) {
        return lockedActions.contains(action);
    }

    
    public void runIfUnlocked(T action, Runnable task) {
        if (isLocked(action)) {
            throw new IllegalStateException("Action " + action + " is locked");
        }
        task.run();
    }
    
    public void exitIfLocked(T action) {
        if (isLocked(action)) {
            throw new IllegalStateException("Action " + action + " is locked");
        }
    }

    
    public void warnIfLocked(T action) {
        if (isLocked(action)) {
            System.out.println("WARNING: Action %s is locked." + action);
        }
    }

    
    public <R> R callIfUnlocked(T action, Supplier<R> task) {
        if (isLocked(action)) {
            throw new IllegalStateException("Action " + action + " is locked");
        }
        return task.get();
    }
    
    
    public LockManager<T> throwIfLocked(T action) {
        exitIfLocked(action);
        return this;
    }
}
