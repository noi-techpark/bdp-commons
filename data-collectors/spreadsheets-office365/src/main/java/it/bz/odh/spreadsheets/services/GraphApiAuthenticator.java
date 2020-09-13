package it.bz.odh.spreadsheets.services;


import com.microsoft.aad.msal4j.*;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@Service
public class GraphApiAuthenticator {


    //TODO use ${value} to access properties
    private static String authority;
    private static String clientId;
    private static String scope;
    private static String keyPath;
    private static String certPath;


    public static IAuthenticationResult getAccessTokenByClientCredentialGrant() throws Exception {

        setUpSampleData();

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Files.readAllBytes(Paths.get(keyPath)));
        PrivateKey key = KeyFactory.getInstance("RSA").generatePrivate(spec);

        InputStream certStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(certPath)));
        X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(certStream);

        ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                clientId,
                ClientCredentialFactory.createFromCertificate(key, cert))
                .authority(authority)
                .build();

        // With client credentials flows the scope is ALWAYS of the shape "resource/.default", as the
        // application permissions need to be set statically (in the portal), and then granted by a tenant administrator

        ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                Collections.singleton(scope))
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
        return future.get();
    }

    /**
     * Helper function unique to this sample setting. In a real application these wouldn't be so hardcoded, for example
     * different users may need different authority endpoints and the key/cert paths could come from a secure keyvault
     */
    private static void setUpSampleData() throws IOException {
        // Load properties file and set properties used throughout the sample
        Properties properties = new Properties();
        properties.load(new FileInputStream(Thread.currentThread().getContextClassLoader().getResource("").getPath() + "application.properties"));
        authority = properties.getProperty("AUTHORITY");
        clientId = properties.getProperty("CLIENT_ID");
        keyPath = properties.getProperty("KEY_PATH");
        certPath = properties.getProperty("CERT_PATH");
        scope = properties.getProperty("SCOPE");
    }


}
