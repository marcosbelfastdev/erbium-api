package br.com.erbium.core;


import br.com.erbium.core.base.scripts.HeadersTrigger;
import br.com.erbium.core.scripts.triggers.builtin.DefaultHeadersTrigger;
import br.com.erbium.core.interfaces.HeadersManagerOperator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

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


public class HeadersManager implements HeadersManagerOperator {

    @Getter @Setter
    @Accessors(fluent = true)
    private Endpoint parentEndpoint;

    @Getter @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private Headers headers = new Headers();
    @Getter @Setter
    @Accessors(fluent = true)
    private Headers savedHeaders;

    @Getter @Setter
    @Accessors(fluent = true)
    private Headers committedHeaders;

    @Getter(AccessLevel.PROTECTED) @Accessors(fluent = true)
    final Map<String, HeadersTrigger> queuedHeaderTriggers = new LinkedHashMap<>();

    public HeadersManager() {

    }

    
    public CollectionEnvironment getEnvironment() {
        return parentEndpoint().parentCollection().collectionEnvironment();
    }

    
    public HeadersManager setHeaders(@NonNull Headers headers) {
        headers(headers);
        return this;
    }

    
    public HeadersManager setHeaders(@NonNull Header... headers) {
        this.headers().clear();
        for (Header header : headers) {
            headers().addHeader(header);
        }
        return this;
    }

    public Headers setCommittedHeaders(@NonNull Headers headers) {
        committedHeaders(headers);
        return committedHeaders();
    }

    
    public Headers getLiteralHeaders() {

        Headers literalHeaders = new Headers();

        for (Header originalHeader : headers().headers) {
            String key = getEnvironment().replaceVars(originalHeader.getKey());
            if (key.isEmpty())
                continue;
            Object value = originalHeader.getValue();
            String type = originalHeader.getType();


            if (type == null || type.equals("text") || type.equals("default") || type.equals("json") || type.isEmpty()) {
                if (value == null) {
                    value = "";
                }
                value = getEnvironment().replaceVars(value.toString());
            }


            Header literalHeader = new Header(key, value, originalHeader.getDescription(), type);
            literalHeaders.addHeader(literalHeader);
        }

        return literalHeaders;
    }

    
    public void saveHeaders() {
        if (savedHeaders() == null) {
            savedHeaders(headers().clone());
            return;
        }
        throw new IllegalStateException("Headers can only be saved once or after they have been reinstated. Call reinstateHeaders() first.");
    }

    
    public void reinstateHeaders() {
        if (savedHeaders() == null)
            throw new IllegalStateException("No headers are saved to be reinstated.");
        headers(savedHeaders.clone());
        savedHeaders(null);
    }

    @Override
    public Headers getHeaders() {
        return headers();
    }

    void queueHeaderTrigger(@NonNull String name, @NonNull HeadersTrigger trigger) {
        if (queuedHeaderTriggers().containsValue(trigger)) {
            System.out.println("WARNING: Headers trigger is already queued.");
            return;
        }
        if (queuedHeaderTriggers().containsKey(name)) {
            throw new IllegalStateException("WARNING: A headers trigger with name '" + name + "' is already queued.");
        }
        trigger.attach(this, parentEndpoint());
        queuedHeaderTriggers.put(name, trigger);
    }

    void queueHeaderTrigger(@NonNull String name, @NonNull Class<? extends HeadersTrigger> triggerClass) {
        try {
            HeadersTrigger trigger = triggerClass.getDeclaredConstructor().newInstance();
            queueHeaderTrigger(name, trigger);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate HeadersTrigger from class: " + triggerClass.getName(), e);
        }
    }

    void queueHeaderTrigger(@NonNull HeadersTrigger script) {
        queueHeaderTrigger(UUID.randomUUID().toString(), script);
    }

    void queueHeaderTrigger(@NonNull String name, @NonNull Consumer<HeadersManagerOperator> consumer) {
        HeadersTrigger wrapper = new HeadersTrigger() {
            @Override
            public void run() {
                consumer.accept(HeadersManager.this);
            }

            @Override
            public void getBasicRequirementHeaders() {

            }

            @Override
            public void getCleanedupHeaders() {

            }
        };
        wrapper.attach(this, parentEndpoint());
        queuedHeaderTriggers.put(name, wrapper);
    }

    void removeHeaderTrigger(@NonNull String name) {
        if (!queuedHeaderTriggers().containsKey(name)) {
            System.out.println("WARNING: No queued headers trigger with name '" + name + "' found.");
        }
        queuedHeaderTriggers().remove(name);
    }

    void runHeaderTriggers() {

        if (queuedHeaderTriggers.isEmpty()) {
            HeadersTrigger script = new DefaultHeadersTrigger();
            script.attach(this, parentEndpoint());
            script.run();
            return;
        }

        for (HeadersTrigger script : queuedHeaderTriggers.values()) {
            script.run();
        }
    }
}
