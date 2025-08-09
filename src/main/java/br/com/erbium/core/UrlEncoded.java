/**
 * Handles URL-encoded form data for requests, providing methods to set, clear, and submit form fields.
 *
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */
package br.com.erbium.core;

import br.com.erbium.core.interfaces.ISubmission;
import br.com.erbium.utils.StringUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class UrlEncoded implements ISubmission {

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    protected RequestManager parentRequestManager;

    @Getter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    protected String body; // original request body
    LinkedHashMap<String, String> formData = new LinkedHashMap<>();

    /**
     * Sets a field in the form data. If value is null, removes the field.
     *
     * @param name  The field name.
     * @param value The field value, or null to remove.
     * @return This UrlEncoded instance for chaining.
     */
    public UrlEncoded setField(@NonNull String name, String value) {
       if (value == null) {
           formData.remove(name);
       }
       formData.put(name, value);
       return this;
   }

    /**
     * Clears all fields from the form data.
     *
     * @return This UrlEncoded instance for chaining.
     */
    public UrlEncoded clear() {
       formData.clear();
       return this;
   }

    /**
     * Returns the form data as a string representation.
     *
     * @return The form data as a string.
     */
    private String getFormData() {
        return formData.toString();
    }

    /**
     * Sets the form data from a string body, parsing key-value pairs.
     *
     * @param body The string body containing key-value pairs.
     * @return This UrlEncoded instance for chaining.
     */
    UrlEncoded setBody(@NonNull String body) {
        Pattern pattern = Pattern.compile("key=([^,]+), value=([^,]+)");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            formData.put(key, value);
        }
        return this;
    }


    @Override
    /**
     * Submits the form data using the parent request manager.
     *
     * @return The resulting Collection after submission.
     */
    public Collection submit() {
        return parentRequestManager().submit();
    }

    @Override
    /**
     * Sends the form data using the parent request manager.
     *
     * @return The resulting Collection after sending.
     */
    public Collection send() {
        return parentRequestManager().send();
    }

    @Override
    /**
     * Sends a POST request with the form data using the parent request manager.
     *
     * @return The resulting Collection after the POST request.
     */
    public Collection post() {
        return parentRequestManager().post();
    }

    @Override
    /**
     * Sends a GET request with the form data using the parent request manager.
     *
     * @return The resulting Collection after the GET request.
     */
    public Collection get() {
        return parentRequestManager().get();
    }

    @Override
    /**
     * Sends a PUT request with the form data using the parent request manager.
     *
     * @return The resulting Collection after the PUT request.
     */
    public Collection put() {
        return parentRequestManager().put();
    }

    @Override
    /**
     * Sends an OPTIONS request with the form data using the parent request manager.
     *
     * @return The resulting Collection after the OPTIONS request.
     */
    public Collection options() {
        return parentRequestManager().options();
    }

    @Override
    /**
     * Sends a PATCH request with the form data using the parent request manager.
     *
     * @return The resulting Collection after the PATCH request.
     */
    public Collection patch() {
        return parentRequestManager().patch();
    }

    @Override
    /**
     * Sends a DELETE request with the form data using the parent request manager.
     *
     * @return The resulting Collection after the DELETE request.
     */
    public Collection delete() {
        return parentRequestManager().delete();
    }

    @Override
    /**
     * Sends a HEAD request with the form data using the parent request manager.
     *
     * @return The resulting Collection after the HEAD request.
     */
    public Collection head() {
        return parentRequestManager().head();
    }


    /**
     * Gets the collection environment associated with this form data.
     *
     * @return The CollectionEnvironment instance.
     */
    CollectionEnvironment environment() {
        return parentRequestManager().parentEndpoint().parentCollection().collectionEnvironment();
    }

    //@Override
    /**
     * Returns the parent Endpoint for this form data.
     *
     * @return The parent Endpoint.
     */
    public Endpoint backToEndpoint() {
        return (Endpoint) parentRequestManager().parentEndpoint();
    }

    //@Override
    /**
     * Returns the parent Collection for this form data.
     *
     * @return The parent Collection.
     */
    public Collection backToCollection() {
        return (Collection) parentRequestManager().parentEndpoint().parentCollection();
    }

    /**
     * Prints the provided messages using StringUtil and returns this instance.
     *
     * @param messages The messages to print.
     * @return This UrlEncoded instance for chaining.
     */
    public UrlEncoded print(@NonNull String... messages) {
        StringUtil.print(messages);
        return this;
    }


}