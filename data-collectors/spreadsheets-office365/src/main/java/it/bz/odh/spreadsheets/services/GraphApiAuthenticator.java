package it.bz.odh.spreadsheets.services;


import com.microsoft.aad.msal4j.*;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

@Component
public class GraphApiAuthenticator {

    @Value("${AUTHORITY}")
    private String authority;

    @Value("${CLIENT_ID}")
    private String clientId;

    private String scope = "https://graph.microsoft.com/.default";

    @Value("${KEY_PATH}")
    private String keyPath;

    @Value("${CERT_PATH}")
    private String certPath;

    private String token;

    @PostConstruct
    private void postConstruct() throws Exception {
        token = getAccessTokenByClientCredentialGrant().accessToken();
    }

    public String token(){
        return token;
    }


    private IAuthenticationResult getAccessTokenByClientCredentialGrant() throws Exception {
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


}
