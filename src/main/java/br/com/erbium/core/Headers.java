package br.com.erbium.core;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.LinkedList;

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

@AllArgsConstructor()
@NoArgsConstructor
@Data
@Accessors(chain = true)
/**
 * Represents a collection of HTTP headers and provides utility methods for header management.
 */
public class Headers implements Cloneable {

    
    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) @Accessors(fluent = true)
    LinkedList<Header> headers = new LinkedList<>();
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE) @Accessors(fluent = true)
    HeadersManager headersManager;

    
    public Boolean isEmpty() {
        return headers.isEmpty();
    }

    
    public void addHeader(Header header) {
        headers.add(header);
    }

    public void setHeader(Header header) {
        headers.remove(header);
        headers.add(header);
    }

    
    public void removeHeader(Header header) {
        headers.remove(header);
    }

    
    public void removeHeader(String key) {
        // FIX: Use removeIf to prevent ConcurrentModificationException.
        if (key != null) {
            headers.removeIf(header -> key.equals(header.getKey()));
        }
    }

    @Override
    public Headers clone() {
        try {
            // Perform a deep clone to prevent shared state and side effects.
            Headers cloned = (Headers) super.clone();
            // Create a new list for the clone
            cloned.headers = new LinkedList<>();
            // Copy each header into the new list
            for (Header header : this.headers) {
                // This assumes Header is a simple data class. If it also contained
                // mutable objects, it would need its own deep clone method.
                cloned.headers.add(new Header(header.getKey(), header.getValue(), header.getDescription(), header.getType()));
            }
            // The manager reference can be copied, as it's a link back to the parent.
            cloned.headersManager(this.headersManager);
            return cloned;
        } catch (CloneNotSupportedException e) {
            // This should not happen since we implement Cloneable.
            throw new RuntimeException("Cloning failed for Headers", e);
        }
    }

    
    public Header getHeaderByKey(String key) {
        for (Header header : headers) {
            if (header.getKey().equals(key))
                return header;
        }
        return null;
    }

    
    public Object getHeaderValue(String key) {
        for (Header header : headers) {
            if (header.getKey().equals(key))
                return header.getValue();
        }
        return null;
    }

    public Headers set(@NonNull String varName, Object value) {
        headersManager().getEnvironment().set(varName, value);
        return this;
    }

    public Object get(@NonNull String varName) {
        return headersManager().getEnvironment().get(varName);
    }

    public Headers removeDuplicates() {
        LinkedList<Header> uniqueHeaders = new LinkedList<>();
        for (Header header : headers) {
            if (uniqueHeaders.stream().noneMatch(h -> h.getKey().equals(header.getKey()))) {
                uniqueHeaders.add(header);
            }
        }
        headers = uniqueHeaders;
        return this;
    }

    public Headers removeIfValueIsNull() {
        headers.removeIf(header -> header.getValue() == null);
        return this;
    }

    public Headers removeIfValueIsBlankString() {
        headers.removeIf(header -> header.getValue().toString().trim().isEmpty());
        return this;
    }

    public Headers removeIfHeaderKeyIsEmpty() {
        headers.removeIf(header -> header.getKey().isEmpty());
        return this;
    }

    public Headers replaceNullValuesWithEmptyString() {
        for (Header header : headers) {
            if (header.getValue() == null) {
                header.setValue("");
            }
        }
        return this;
    }

    public Headers replaceEmptyValuesWithNull() {
        for (Header header : headers) {
            if (header.getValue() != null && header.getValue().toString().trim().isEmpty()) {
                header.setValue(null);
            }
        }
        return this;
    }

    public Headers clear() {
        headers().clear();
        return this;
    }
}
