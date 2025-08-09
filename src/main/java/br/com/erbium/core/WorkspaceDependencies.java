package br.com.erbium.core;

import br.com.erbium.core.lock.ILock;
import br.com.erbium.core.lock.LockManager;
import br.com.erbium.core.enums.WorkspaceAction;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

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

public class WorkspaceDependencies extends WorkspaceIdentifiable implements ILock {

    @Getter @Accessors(fluent = true)
    private final Selector selector = new Selector();

    @Getter @Accessors(fluent = true)
    private final LockManager<WorkspaceAction> lockManager =
            new LockManager<>(WorkspaceAction.class);

    WorkspaceDependencies() {

    }

    WorkspaceDependencies(@NonNull String name) {
        super(name);
    }



    @Override
    public void lock() {
        lockManager().lock(
                WorkspaceAction.REGISTER,
                WorkspaceAction.UNREGISTER
        );
    }
}
