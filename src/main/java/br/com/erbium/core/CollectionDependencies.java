package br.com.erbium.core;


import br.com.erbium.core.lock.LockManager;
import br.com.erbium.core.enums.CollectionAction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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


public abstract class CollectionDependencies extends CollectionIdentifiable {

    @Getter(AccessLevel.PROTECTED) @Accessors(fluent = true)
    final LockManager<CollectionAction> lockManager =
            new LockManager<>(CollectionAction.class);
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PACKAGE) @Accessors(fluent = true)
    Workspace workspace;

    protected CollectionDependencies() {
        super();
    }

    protected CollectionDependencies(@NonNull String name) {
        super(name);
    }

    protected CollectionDependencies lock() {
        lockManager().lock(CollectionAction.IMPORT_ENDPOINT);
        return this;
    }
}