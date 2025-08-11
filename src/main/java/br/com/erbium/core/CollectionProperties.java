package br.com.erbium.core;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
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


public abstract class CollectionProperties extends CollectionDependencies {

    
    protected CollectionProperties() {
        super();
    }

    
    protected CollectionProperties(@NonNull String name) {
        super(name);
    }

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    CollectionEnvironment collectionEnvironment = new CollectionEnvironment();
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    Map<String, Endpoint> endpoints = new LinkedHashMap<>();
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    protected String host;

    DefaultTestIteratorContext defaultTestIteratorContext;

    public Routers out() {
        return workspace().out;
    }

}