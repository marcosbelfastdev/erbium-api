package br.com.erbium.core;

import java.util.LinkedList;
import java.util.List;

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

public class RotatingBuffer<T> {

    private int maxSize;
    private final LinkedList<T> buffer;

    public RotatingBuffer(int maxSize) {
        this.maxSize = maxSize;
        this.buffer = new LinkedList<>();
    }

    public void add(T item) {
        if (buffer.size() == maxSize) {
            buffer.removeFirst(); // remove the oldest
        }
        buffer.addLast(item); // add new one at the end
    }

    public T get(int index) {
        if (index < 0 || index >= buffer.size()) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        return buffer.get(buffer.size() - 1 - index);
    }

    public List<T> getAll() {
        return List.copyOf(buffer);
    }

    /**
     * Returns the number of items currently in the buffer.
     *
     * @return The buffer size.
     */
    public int size() {
        return buffer.size();
    }

}
