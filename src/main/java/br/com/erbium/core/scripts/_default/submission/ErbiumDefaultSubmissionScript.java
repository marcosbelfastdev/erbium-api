package br.com.erbium.core.scripts._default.submission;

import br.com.erbium.core.*;
import br.com.erbium.core.Headers;
import br.com.erbium.core.base.scripts.ErbiumSubmissionScript;
import br.com.erbium.core.enums.RequestType;
import okhttp3.*;
import okio.Buffer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

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

public class ErbiumDefaultSubmissionScript extends ErbiumSubmissionScript {

    @Override
    public void run() {


        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder = createRequestHeaders(requestBuilder);
        requestBuilder = createRequestUrl(requestBuilder);
        RequestBody requestBody = createRequestBody();

        Request request = createRequest(requestBuilder, requestBody);

        OkHttpClient client = createClient(
                committedRequestProperties.committedSslContext(),
                committedRequestProperties.trustManager()
        );


        printRequestMethod(request);
        printUrl(request);
        printRequestHeaders(request);
        printRequestBody(requestBody);

        ErbiumResponse erbiumResponse = execute(client, request);
        printResponse(erbiumResponse);
        printTime(erbiumResponse);
    }

    public ErbiumResponse execute(OkHttpClient client, Request request) {
        ErbiumResponse erbiumResponse = null;
        Throwable throwable = null;
        long after;
        long before = System.currentTimeMillis();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throwable = e;
        }
        after = System.currentTimeMillis();
        try {
            erbiumResponse = new ErbiumResponse(response, throwable, after - before);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setResponse(erbiumResponse);
        return erbiumResponse;
    }

    public Request.Builder createRequestUrl(Request.Builder requestBuilder) {
        return requestBuilder.url(committedRequestProperties.committedUrl());
    }

    public Request.Builder createRequestHeaders(Request.Builder requestBuilder) {
        Headers committedHeaders = committedRequestProperties.committedHeaders();
        for (Header header : committedHeaders.headers()) {
            requestBuilder = requestBuilder.header(header.getKey(), header.getValue().toString());
        }
        return requestBuilder;
    }


    public RequestBody createRequestBody() {

        String contentType = committedRequestProperties.committedHeaders().headers().stream()
                .filter(header -> header.getKey().equals("Content-Type"))
                .map(header -> header.getValue().toString())
                .findFirst()
                .orElse(null);

        RequestBody requestBody = null;

        MediaType mediaType = null;
        if (contentType != null) {
            mediaType = MediaType.parse(contentType);
        }

        if (committedRequestProperties().requestType() == RequestType.JSON || committedRequestProperties().requestType() == RequestType.XML) {
            requestBody = RequestBody.create(mediaType, committedRequestProperties().committedBody());

        } else if (committedRequestProperties().requestType() == RequestType.URL_ENCODED) {
            LinkedHashMap<String,String> commitedUrlEncodedBody = new LinkedHashMap<>(committedRequestProperties.committedUrlEncodedBody());
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (String key : commitedUrlEncodedBody.keySet()) {
                formBodyBuilder.add(key, commitedUrlEncodedBody.get(key));
            }
            requestBody = formBodyBuilder.build();
        } else if(committedRequestProperties().requestType() == RequestType.MULTIPART_FORMDATA) {
            /*
            MUltipart has a property and a String field.
            If the field is set as a file, it is a String but the property is a file.
            Structure:
            It can be a list of the following:
            Field (String), Value (String path or File), Type (path, base64 or file)

             */
//            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
//            for (String key : committedRequestProperties.committedMultipartBody().keySet()) {
//                multipartBodyBuilder.addFormDataPart(key, committedRequestProperties.committedMultipart
        }

        return requestBody;
    }

    public Request createRequest(Request.Builder requestBuilder, RequestBody requestBody) {
        switch (committedRequestProperties.method()) {
            case "GET" -> requestBuilder = requestBuilder.get();
            case "POST" -> requestBuilder = requestBuilder.post(requestBody);
            case "PUT" -> requestBuilder = requestBuilder.put(requestBody);
            case "DELETE" -> requestBuilder = requestBuilder.delete();
            case "PATCH" -> requestBuilder = requestBuilder.patch(requestBody);
            case "HEAD" -> requestBuilder = requestBuilder.head();
            case "OPTIONS" -> requestBuilder = requestBuilder.method("OPTIONS", null);
            default -> {
                throw new UnsupportedOperationException("Unknown HTTP method: " + committedRequestProperties.method());
            }
        }
        return requestBuilder.build();
    }

    public OkHttpClient createClient(SSLContext sslContext, TrustManager[] trustAllCerts) {
        final int connectTimeout = 10;
        final int readTimeout = 10;
        final int writeTimeout = 30;
        final int callTimeout = 30;
        if (sslContext != null) {
            return new OkHttpClient.Builder()
                    .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                    .readTimeout(readTimeout, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                    .callTimeout(callTimeout, TimeUnit.SECONDS)
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        }
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .callTimeout(callTimeout, TimeUnit.SECONDS)
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    public void printRequestBody(RequestBody requestBody) {

        if (requestBody == null) {
            return;
        }

        Buffer buffer = new Buffer();
        try {
            requestBody.writeTo(buffer);
        } catch (IOException e) {

        }

//        if (request.body() instanceof FormBody) {
//            StringBuilder stringBuilder = new StringBuilder();
//            FormBody formBody = (FormBody) request.body();
//            for (int i = 0; i < formBody.size(); i++) {
//                stringBuilder.append(formBody.encodedName(i)).append("=").append(formBody.encodedValue(i)).append("\n");
//            }
//            System.out.println(stringBuilder.toString());
//            return;
//        }

        out().log("\n"+ buffer.readUtf8());
    }

    public void printRequestMethod(Request request) {
        out().log(EType.UDEF, EItem.REQUEST_METHOD, "\n" + request.method());
    }

    public void printUrl(Request request) {
        out().log(EType.UDEF, EItem.REQUEST_URL, request.url().url());
    }

    public void printRequestHeaders(Request request) {
        for (int i = 0; i < request.headers().size(); i++) {
            out().log(EType.UDEF, EItem.REQUEST_HEADERS, "\n" +
                    request.headers().name(i) + ": " + request.headers().value(i));
        }
    }

    public void printResponse(ErbiumResponse erbiumResponse) {
        String responseHeadersMessage = erbiumResponse.headers() == null ? "No response headers." : erbiumResponse.headers().toString();
        String responseBodyMessage = erbiumResponse.body() == null ? "No response body" : erbiumResponse.body().toString();
        int code = erbiumResponse.code();
        out().log("\n\nRESPONSE");
        out().log(EType.UDEF, EItem.RESPONSE_HEADERS, "\n" + responseHeadersMessage);
        out().log(EType.UDEF, EItem.RESPONSE_BODY, "\n\n" + responseBodyMessage);
        out().log(EType.UDEF, EItem.RESPONSE_CODE, "\n" + code);
    }

    public void printTime(ErbiumResponse erbiumResponse) {
        out().log(EType.UDEF, EItem.MESSAGE, "\n" + erbiumResponse.time() + " ms.");
    }
}
