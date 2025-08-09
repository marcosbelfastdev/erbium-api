/**
 * Holds the committed (finalized) properties of a request, including URL, headers, body, SSL, and endpoint reference.
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

import br.com.erbium.core.enums.RequestType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;

import java.util.LinkedHashMap;

import static br.com.erbium.core.RequestManager.buildUrlWithParams;

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

public class CommittedRequestProperties {

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private String committedUrl;
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private Headers committedHeaders;
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private String committedBody;
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private RequestType requestType;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private LinkedHashMap<String, String> committedUrlEncodedBody;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private String method;
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private SSLContext committedSslContext;
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private SSLParameters committedSslParameters;
    @Getter @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private TrustManager[] trustManager;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    private Endpoint endpoint;


    public CommittedRequestProperties(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    
    public void runHeadersTriggers() {
        endpoint().headersManager().runHeaderTriggers();
        committedHeaders(endpoint().headersManager().committedHeaders());
    }

    
    public void commitSslParameters() {
        if (endpoint().sslSecurity() != null) {
            committedSslContext = endpoint().sslSecurity().getSslContext();
            committedSslParameters = endpoint().sslSecurity().sslParameters;
            trustManager = endpoint().sslSecurity().trustManager();
        }
    }

    
    public String runRequestTriggers() {
        if (committedBody != null) {
            return committedBody;
        }

        // *** DECIDIR DE SE PASSA CADA FORMATO DE MODE/REQUEST TYPE OU SE TRANSFORMA TUDO EM BODY

        endpoint().runRequestTriggers();
        requestType(endpoint().requestManager().requestType());

        if (requestType() == RequestType.JSON || requestType() == RequestType.XML) {
            committedBody = endpoint().requestManager().jsonRequest().getBody();
        } else if (requestType() == RequestType.URL_ENCODED) {
            committedUrlEncodedBody = endpoint().requestManager().urlEncoded().formData;
        } else {
            committedBody = "";
        }

        return committedBody;
    }

    
    public String commitUrl() {
        if (committedUrl != null) {
            return committedUrl;
        }
        committedUrl(
                endpoint().parentCollection().collectionEnvironment().replaceVars(
                        buildUrlWithParams(endpoint().requestManager().url(), endpoint().requestManager().params))
        );
        return committedUrl;
    }

    
    public void commit() {
        method(endpoint().requestManager().method());
        commitUrl();
        runHeadersTriggers();
        runRequestTriggers();
        commitSslParameters();
    }


}
