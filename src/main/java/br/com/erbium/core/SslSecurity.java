package br.com.erbium.core;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

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

public class SslSecurity {

    private static SslSecurity instance;
    @Getter @Setter @Accessors(fluent = true)
    TrustManager[] trustManager;

    @Getter
    private SSLContext sslContext;
    private String protocol;
    private Boolean usingTrustAll = false;
    SSLParameters sslParameters;

    public static SslSecurity getInstance() {
        if (instance != null) {
            return instance;
        }
        return setInstance();
    }

    public synchronized static SslSecurity setInstance() {
        instance = new SslSecurity();
        return instance;
    }


    public SslSecurity setProtocol(String protocol) {

        if (sslContext == null) {
            this.protocol = (protocol == null || protocol.isBlank()) ? "TLSv1.2" : protocol;
            try {
                TrustManager[] trustManager = new TrustManager[] {
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }
                };
                trustManager(trustManager);
                sslContext = SSLContext.getInstance(this.protocol);
                sslContext.init(null, trustManager, new SecureRandom());

                sslParameters = new SSLParameters();
                sslParameters.setProtocols(new String[]{ this.protocol });

            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException("Failed to initialize SSL context with protocol: " + this.protocol, e);
            }
        }

        return this;
    }

    public SslSecurity trustAll() {
        if (sslContext != null)
            return this;

        try {
            TrustManager[] trustAll = new TrustManager[] {
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            };
            trustManager(trustAll);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, new SecureRandom());
            sslParameters = new SSLParameters();
            sslParameters.setProtocols(new String[]{"TLS"});
            usingTrustAll = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create relaxed SSL context", e);
        }
        return this;
    }

    public String getProtocol() {
        String message = usingTrustAll ? "TLS: Relaxed HTTP Validation (Trust All Certificates)" : "";
        return protocol == null ? message : protocol + ": " + message;
    }

}
