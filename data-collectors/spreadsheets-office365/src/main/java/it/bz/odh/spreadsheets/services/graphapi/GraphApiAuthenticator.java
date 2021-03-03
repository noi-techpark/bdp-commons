package it.bz.odh.spreadsheets.services.graphapi;


import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CompletableFuture;


/**
 * Authenticates the client with Microsoft Graph API and handles token generation and validation
 *
 * Token is valid for X time, then simply a new token gets requested
 */
@Service
public class GraphApiAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(GraphApiAuthenticator.class);

    @Value("${auth.tenantId}")
    private String tenantId;

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.scope}")
    private String scope;

    @Value("${auth.keyPath}")
    private String keyPath;

    @Value("${auth.certPath}")
    private String certPath;

    private String authority = "https://login.microsoftonline.com/%s/oauth2/token";


    private String token;

    private Date tokenExpireDate;


    @PostConstruct
    private void postConstruct() throws Exception {

        //check that properties are set correct
        if (tenantId == null || tenantId.length() == 0)
            throw new InvalidConfigurationPropertyValueException("tenantId", tenantId, "tenantId must be set in .env file and can't be empty");

        if (clientId == null || clientId.length() == 0)
            throw new InvalidConfigurationPropertyValueException("clientId", clientId, "clientId must be set in .env file and can't be empty");

        if (keyPath == null || keyPath.length() == 0)
            throw new InvalidConfigurationPropertyValueException("keyPath", keyPath, "keyPath must be set in .env file and can't be empty");

        if (certPath == null || certPath.length() == 0)
            throw new InvalidConfigurationPropertyValueException("certPath", certPath, "certPath must be set in .env file and can't be empty");

        if (scope == null || scope.length() == 0)
            throw new InvalidConfigurationPropertyValueException("scope", scope, "scope must be set in .env file and can't be empty");

        if (!scope.contains("/.default"))
            throw new InvalidConfigurationPropertyValueException("scope", scope, "scope must contain /.default");

        authority = String.format(authority, tenantId);
    }

    /**
     * Gets the token if not present yet or expired
     * Otherwise it just returns the existing token
     *
     * @return returns the token
     * @throws Exception
     */
    public String getToken() throws Exception {

        logger.info("Checking validity of token");

        if (tokenExpireDate == null || tokenExpireDate.before(new Date())) {

            logger.info("Token expired, get new token");

            IAuthenticationResult result = getAccessTokenByClientCredentialGrant();
            token = result.accessToken();
            tokenExpireDate = result.expiresOnDate();

            logger.info("New token, will expire " + tokenExpireDate);

        } else
            logger.info("Token still valid until " + tokenExpireDate);

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
