package br.com.erbium.core.postman.json_210;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

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

public class PostmanCollection {
    public static ThreadLocal<PostmanCollection> instance = new ThreadLocal<>();
    @Setter @Getter
    @Accessors(fluent = true)
    Set<String> postmanJson = new HashSet<>();

    public static PostmanCollection getInstance() {
        if (instance.get() == null) {
            instance.set(new PostmanCollection());
        }
        return instance.get();
    }

    public void destroy() {
        instance = new ThreadLocal<>();
    }

    public void addJson(String json) {
        postmanJson.add(json);
    }

}
