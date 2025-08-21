package br.com.erbium.core;

import com.jayway.jsonpath.JsonPath;
import lombok.Getter;
import lombok.experimental.Accessors;
import okhttp3.*;
import okhttp3.Headers;

import java.io.IOException;

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

@Getter @Accessors(fluent = true)
public class ErbiumResponse {
    private int code;
    private String message;
    private ResponseBody responseBody;
    private String body;
    private Headers headers;
    private Protocol protocol;
    private Request request;
    private Handshake handshake;
    private Throwable throwable;
    private long time;

    public ErbiumResponse(Response response, Throwable throwable, long time) throws IOException {
        if (response != null) {
            this.code = response.code();
            this.message = response.message();
            this.headers = response.headers();
            this.protocol = response.protocol();
            this.request = response.request();
            this.handshake = response.handshake();

            // MUST be read only once
            this.responseBody = response.body();
            this.body = responseBody.string();
        }

        this.time = time;
        this.throwable = throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public <T> T jsonPath(String path) {
        return JsonPath.parse(body).read(path);
    }

    @Override
    /**
     * Returns a string representation of the response, including status, message, protocol, headers, and body.
     *
     * @return A string representation of the response.
     */
    public String toString() {
        return "Status: " + code + "\n" +
                "Message: " + message + "\n" +
                "Protocol: " + protocol + "\n" +
                "Headers: " + headers + "\n" +
                "Body: " + body;
    }
}
