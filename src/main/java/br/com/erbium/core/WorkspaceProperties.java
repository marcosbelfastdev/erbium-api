package br.com.erbium.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

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

public class WorkspaceProperties extends WorkspaceDependencies {


    @Getter @Accessors(fluent = true)
    protected Map<String, Collection> collections = new HashMap<>();

    WorkspaceProperties() {

    }

    WorkspaceProperties(@NonNull String name) {
        super(name);
    }

}
