package br.com.erbium.core.postman.json_210;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

public class PostmanFlattener {

    private final Map<String, Object> root;
    private final Map<String, Object> flattened = new LinkedHashMap<>();

    public PostmanFlattener(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        this.root = mapper.readValue(json, new TypeReference<>() {});
        process();
    }

    public Map<String, Object> getFlattenedMap() {
        return flattened;
    }

    @SuppressWarnings("unchecked")
    private void process() {
        if (!root.containsKey("collection")) return;
        Map<String, Object> collection = (Map<String, Object>) root.get("collection");
        if (!collection.containsKey("item")) return;

        List<Object> items = (List<Object>) collection.get("item");
        flatten(items, "");
    }

    @SuppressWarnings("unchecked")
    private void flatten(List<Object> items, String parentPath) {
        for (Object itemObj : items) {
            if (!(itemObj instanceof Map)) continue;
            Map<String, Object> item = (Map<String, Object>) itemObj;
            String name = (String) item.getOrDefault("name", "unnamed");
            String path = parentPath.isEmpty() ? name : parentPath + "|" + name;

            if (item.containsKey("item")) {
                List<Object> children = (List<Object>) item.get("item");
                flatten(children, path);
            } else {
                flattened.put(path, item);
            }
        }
    }
}
