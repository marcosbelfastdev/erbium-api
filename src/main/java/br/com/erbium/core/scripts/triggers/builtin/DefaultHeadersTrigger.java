package br.com.erbium.core.scripts.triggers.builtin;

import br.com.erbium.core.Header;
import br.com.erbium.core.Headers;
import br.com.erbium.core.base.scripts.HeadersTrigger;
import br.com.erbium.core.enums.RequestType;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 *
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */

public class DefaultHeadersTrigger extends HeadersTrigger implements Runnable {

    public DefaultHeadersTrigger() {

    }

    @Override
    public void run() {

        getBasicRequirementHeaders();

        getCleanedupHeaders(); // this includes headers with environment variables replaced

        // Now we will set the literal headers (environment variables are replaced here)
        Headers literalHeaders = headersManagerOperator().getLiteralHeaders();
        // This is to ensure that the headers are set correctly for the next submission
        headersManagerOperator()
                .setCommittedHeaders(literalHeaders);
    }

    @Override
    public void getBasicRequirementHeaders() {
        // FIX: Simplified and corrected the header merging logic.
        // The principle is that user-defined headers should always overwrite defaults.

        // 1. Start with a map that will hold the final headers. Using a map
        // naturally handles overwriting duplicates by key.
        Map<String, Header> finalHeaders = new LinkedHashMap<>();

        // 2. Add default headers first.
        finalHeaders.put("User-Agent", new Header("User-Agent", "Erbium/0.1.0", "", "text"));


        if (method().equals("POST") ||
                method().equals("PUT") ||
                method().equals("PATCH") ||
                method().equals("DELETE") ||
                method().equals("OPTIONS")) {

            if (requestType().equals(RequestType.JSON)) {
                finalHeaders.put("Content-Type", new Header("Content-Type", "application/json", "", ""));
            }
            if (requestType().equals(RequestType.XML)) {
                finalHeaders.put("Content-Type", new Header("Content-Type", "application/xml", "", ""));
            }
            if (requestType().equals(RequestType.URL_ENCODED)) {
                finalHeaders.put("Content-Type", new Header("Content-Type", "application/x-www-form-urlencoded", "", ""));
            }
            if (requestType().equals(RequestType.MULTIPART_FORMDATA)) {
                finalHeaders.put("Content-Type", new Header("Content-Type", "multipart/form-data", "", ""));
            }
        }

        // 3. Get the user's current headers and let them overwrite the defaults in the map.
        // Exception: the Content-Type will remain adjusted by the request type.
        Headers existingHeaders = headersManagerOperator().getHeaders();
        for (Header existingHeader : existingHeaders.headers()) {
            if (existingHeader.getKey().equals("Content-Type")) {
                continue;
            }
            finalHeaders.put(existingHeader.getKey(), existingHeader);
        }

        // 4. Set the final, merged headers back to the manager.
        Headers headers = new Headers();
        headers.headers(new LinkedList<>(finalHeaders.values()));
        headersManagerOperator().setHeaders(headers);
    }

    //@Override
    public void getCleanedupHeaders() {
        headersManagerOperator().getHeaders()
                .removeIfValueIsNull()
                .removeIfHeaderKeyIsEmpty()
                .removeDuplicates();
    }


}
