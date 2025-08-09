/**
 * Imports and flattens Postman collections into the internal Collection model for ERBIUM.
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

import br.com.erbium.core.postman.json_210.PostmanCollection;
import br.com.erbium.core.postman.json_210.PostmanFlattener;
import br.com.erbium.core.postman.PostmanCollectionFetcher;
import br.com.erbium.core.enums.RequestType;
import br.com.erbium.utils.JsonPathReader;
import br.com.erbium.utils.UtilConvert;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
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

public class CollectionJsonImporter {

    @Getter
    @Setter
    @Accessors(fluent = true)
    Collection endpointsCollection;

    PostmanCollection postmanCollectionContainer = PostmanCollection.getInstance();

    /**
     * Constructs a CollectionJsonImporter for the given endpoints collection.
     *
     * @param endpointsCollection The collection to import endpoints into.
     */
    protected CollectionJsonImporter(Collection endpointsCollection) {
        endpointsCollection(endpointsCollection);
    }

    /**
     * Imports a Postman collection by UID and API key, using the specified cache duration.
     *
     * @param collectionUid The Postman collection UID.
     * @param apiKey        The Postman API key.
     * @param duration      The cache duration for the collection file.
     * @return The loaded Collection, or null if not found.
     */
    public Collection importPostManCollection(String collectionUid, String apiKey, Duration duration) {
        Collection loadedCollection = importPostManCollection(postmanCollectionContainer, collectionUid);
        if (loadedCollection != null) {
            return loadedCollection;
        }
        PostmanCollectionFetcher fetcher = new PostmanCollectionFetcher();
        fetcher.setCollectionUid(collectionUid);
        fetcher.setApiKey(apiKey);
        File file;
        try {
            file = fetcher.fetchCollection(duration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Postman endpointsCollection with UID: " + collectionUid, e);
        }
        return importPostManCollection(file, collectionUid);
    }

    /**
     * Imports a Postman collection from a PostmanCollection instance by UID.
     *
     * @param postmanCollection The PostmanCollection instance.
     * @param collectionUid     The collection UID to import.
     * @return The loaded Collection, or null if not found.
     */
    protected Collection importPostManCollection(PostmanCollection postmanCollection, String collectionUid) {

        for (String json : postmanCollection.postmanJson()) {
            JsonPathReader jsonReader = new JsonPathReader(json);
            Map<String, Object> info = jsonReader.readObject("$.collection.info");
            String uid = (String) info.get("_postman_id");
            if (uid.equals(collectionUid)) {
                return importPostManCollection(json);
            }
        }
        return null;
    }

    /**
     * Imports a Postman collection from a PostmanCollection instance (not implemented).
     *
     * @param postmanCollection The PostmanCollection instance.
     * @return Always returns null (not implemented).
     */
    protected Collection importPostManCollection(PostmanCollection postmanCollection) {

        return null;
    }

    /**
     * Imports a Postman collection from a file and collection UID.
     *
     * @param file          The file containing the collection JSON.
     * @param collectionUId The collection UID.
     * @return The loaded Collection.
     */
    protected Collection importPostManCollection(File file, String collectionUId) {
        // Read the file into a String
        String jsonString = null;
        try {
            jsonString = UtilConvert.fileInputStreamToString(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return importPostManCollection(jsonString);
    }


    /**
     * Imports a Postman collection from a JSON string.
     *
     * @param json The JSON string representing the collection.
     * @return The loaded Collection.
     */
    public Collection importPostManCollection(String json) {
        JsonPathReader jsonReader = new JsonPathReader(json);
        Map<String, Object> info = jsonReader.readObject("$.collection.info");
        String name = (String) info.get("name");
        String uid = (String) info.get("_postman_id");


        PostmanFlattener flattener = null;
        try {
            flattener = new PostmanFlattener(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> flattened = flattener.getFlattenedMap();
        flattened.keySet().forEach((key) -> {
            importEndpoint((Map<String, Object>) flattened.get(key), key);
        });

        postmanCollectionContainer.addJson(json);
        return endpointsCollection;
    }

    /**
     * Imports a single endpoint from the flattened Postman collection map.
     *
     * @param endpointMap The map representing the endpoint.
     * @param key         The endpoint key/name.
     */
    private void importEndpoint(Map<String, Object> endpointMap, String key) {

        Map<String, Object> request = endpointMap.get("request") == null ? null : (Map<String, Object>) endpointMap.get("request");
        if (request == null) {
            throw new IllegalStateException("INTERNAL ERROR: Request is null but it should not be null");
        }


        Object bodyObject = request.get("body");
        String rawBody = null, formdataBody = null, urlencodedBody = null, binarySourceBody = null;
        RequestType requestType = RequestType.JSON;
        if (bodyObject != null) {
            String modeObject = ((Map<String, String>) bodyObject).get("mode");
            if (bodyObject != null) {
                if (modeObject.equals("raw")) {
                    Object bodyRawObject = ((Map<String, Object>) bodyObject).get("raw");
                    rawBody = bodyRawObject.toString();
                    // add check for XML to decide content type
                    //requestType = RequestType.JSON;
                    //contentType = "application/json";
                    // contentType = "application/xml";
                }
                if (modeObject.equals("formdata")) {
                    Object formdataObject = ((Map<String, Object>) bodyObject).get("formdata");
                    formdataBody = formdataObject.toString();
                    requestType = RequestType.MULTIPART_FORMDATA;
                    //contentType = "multipart/form-data";
                }
                if (modeObject.equals("urlencoded")) {
                    Object urlEncodedObject = ((Map<String, Object>) bodyObject).get("urlencoded");
                    urlencodedBody = urlEncodedObject.toString();
                    requestType = RequestType.URL_ENCODED;
                    //contentType = "application/x-www-form-urlencoded";
                }
                if (modeObject.equals("file")) {
                    Object fileObject = ((Map<String, Object>) bodyObject).get("file");
                    binarySourceBody = ((Map<String, Object>) bodyObject).get("src").toString();
                    requestType = RequestType.BINARY;
                    //contentType = "multipart/form-data";
                }
            }
        }

        String method = (String) request.get("method");
        Map<String, Object> urlMap = (HashMap<String, Object>) request.get("url");
        String url = (String) urlMap.get("raw");
        List<String> hostList = List.of(urlMap.get("host").toString());
        String host = (String) hostList.getFirst();
        String name = (String) endpointMap.get("name");

        Endpoint endpoint = Endpoint.builder()
                .collection(endpointsCollection())
                .eagerRequestValidation(false)
                .name(key)
                .build();

        Headers headers = importHeaders(request);
        endpoint.headersManager().setHeaders(headers);

        endpoint.setMethod(method);
        endpoint.setUrl(url); //order of url and host is important here.
        endpoint.setHost(host);
        endpoint.setRequestType(requestType);


        switch (requestType) {
            case URL_ENCODED -> {
                urlencodedBody = urlencodedBody == null ? "" : urlencodedBody;
                endpoint.requestManager().urlEncoded().setBody(urlencodedBody);
            }
            case JSON -> {
                rawBody = rawBody == null ? "" : rawBody;
                endpoint.requestManager().jsonRequest().setBody(rawBody);
            }
            case XML -> {
                endpoint.requestManager().xmlRequest().setBody(urlencodedBody);
            }
            case MULTIPART_FORMDATA -> {
                //
            }
            case BINARY -> {
                // //
            }
            default -> {

            }
        }
        endpointsCollection().addEndpoint(endpoint); // if the endpointsCollection is set, it will try to validate the json and replace variables.
    }

    /**
     * Imports headers from a request map.
     *
     * @param request The request map containing header information.
     * @return The Headers object containing all imported headers.
     */
    private Headers importHeaders(Map<String, Object> request) {
        List<Map<String, Object>> headerList = (List<Map<String, Object>>) request.get("header");

        Headers headers = new Headers();
        if (headerList != null) {
            for (Map<String, Object> headerMap : headerList) {
                String key = (String) headerMap.get("key");
                String value = (String) headerMap.get("value");
                String description = (String) headerMap.get("description");
                String type = (String) headerMap.get("type");

                if (key != null && value != null) {
                    key = key.trim();
                    value = value.trim();

                    Header header = new Header(key, value,
                            description != null ? description.trim() : null,
                            type != null ? type.trim() : null);
                    headers.addHeader(header); // Assuming addHeader method exists in Headers class
                }
            }
        }

        return headers;
    }
}
