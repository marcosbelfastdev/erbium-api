/**
 * Fetches Postman collections from the Postman API and manages local caching of collections.
 * <p>
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */
package br.com.erbium.core.postman;

import br.com.erbium.utils.FileRepositoryUtil;
import lombok.Getter;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

/**
 * Fetches Postman collections from the Postman API and manages local caching of collections.
 */
public class PostmanCollectionFetcher {

    private static final String POSTMAN_API_BASE_URL = "https://api.getpostman.com/collections/";
    private static final String API_KEY_HEADER = "X-Api-Key";
    private String defaultCollectionFilePath;
    public static final Map<String, File> multithreadFileMap = new ConcurrentHashMap<>();

    private String apiKey;
    private String collectionUid;
    @Getter
    private File lastFetchedFile;

    /**
     * Default constructor for PostmanCollectionFetcher.
     */
    public PostmanCollectionFetcher() {
    }

    /**
     * Constructs a PostmanCollectionFetcher with the specified collection UID and API key.
     *
     * @param collectionUid The Postman collection UID.
     * @param apiKey        The Postman API key.
     */
    public PostmanCollectionFetcher(String collectionUid, String apiKey) {
        this.collectionUid = collectionUid;
        this.apiKey = apiKey;
    }

    /**
     * Sets the API key for Postman API requests.
     *
     * @param apiKey The Postman API key.
     * @return This PostmanCollectionFetcher instance for chaining.
     */
    public PostmanCollectionFetcher setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Sets the collection UID for Postman API requests.
     *
     * @param collectionUid The Postman collection UID.
     * @return This PostmanCollectionFetcher instance for chaining.
     */
    public PostmanCollectionFetcher setCollectionUid(String collectionUid) {
        this.collectionUid = collectionUid;
        return this;
    }

    /**
     * Fetches the Postman collection using the configured UID and API key.
     * Uses the default cache duration.
     *
     * @return The fetched collection file.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     * @throws GeneralSecurityException If a security error occurs during SSL context creation.
     */
    public File fetchCollection() throws IOException, InterruptedException, GeneralSecurityException {
        return fetchCollection(null);
    }

    /**
     * Fetches the Postman collection using the configured UID and API key, with optional cache duration.
     * If a cached file exists and is recent enough, it is returned instead of fetching from the API.
     *
     * @param duration The cache duration. If null, always fetches from the API.
     * @return The fetched or cached collection file.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     * @throws GeneralSecurityException If a security error occurs during SSL context creation.
     */
    public synchronized File fetchCollection(Duration duration) throws IOException, InterruptedException, GeneralSecurityException {

        defaultCollectionFilePath = System.getProperty("user.home") + "/.erbium/" + collectionUid;
        String lastKnownGoodFilePath = System.getProperty("user.home") + "/.erbium/good_" + collectionUid;
        File lastKnownGoodFile = null;

        lastKnownGoodFile = new File(lastKnownGoodFilePath);
        if (duration != null) {
            if (lastKnownGoodFile.exists()) {
                if (System.currentTimeMillis() - lastKnownGoodFile.lastModified() < duration.toMillis()) {
                    return lastKnownGoodFile;
                }
            }
        }

        // Create SSLContext for TLSv1.2
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());

        SSLParameters sslParameters = new SSLParameters();
        sslParameters.setProtocols(new String[]{"TLSv1.2"});

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .sslContext(sslContext)
                .sslParameters(sslParameters)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(POSTMAN_API_BASE_URL + collectionUid))
                .header(API_KEY_HEADER, apiKey)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignore) {
            System.out.println("SEVERE WARNING: Failed to connect to Postman API.");
        }

        if (response == null || response.statusCode() != 200) {
            System.out.println("An attempt will be made to retrieve last known good file stored in Erbium default config directory: " + lastKnownGoodFilePath + ".json");
            lastKnownGoodFile = new File(lastKnownGoodFilePath);
            if (lastKnownGoodFile.exists()) {
                return lastKnownGoodFile;
            } else {
                throw new IOException("Failed to fetch endpointsCollection: HTTP " + response.statusCode() + "\n" + response.body() + ". No last known good file found.");
            }
        }

        String json = response.body();
        FileRepositoryUtil.writeFile(lastKnownGoodFile, json, true);


        return lastKnownGoodFile;
    }


}