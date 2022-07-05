package it.bz.odh.spreadsheets.services.microsoft;

import com.google.common.io.ByteStreams;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * Generates the authentication token to make requests on Microsoft's resources
 * like Sharepoint API, Graph API...
 * It uses a Certificate to authenticate demon applications with no signed in
 * user.
 * <p>
 * Microsoft Authentication Library (MSAL) for Java is used.
 * https://github.com/AzureAD/microsoft-authentication-library-for-java/tree/v1.8.1
 * <p>
 * Further reading for authentication flow wit daemons:
 * https://docs.microsoft.com/en-us/azure/active-directory/develop/scenario-daemon-overview
 */
@Service
public class AuthTokenGenerator {

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenGenerator.class);

	@Value("${auth.tenantId}")
	private String tenantId;

	@Value("${auth.clientId}")
	private String clientId;

	@Value("${sharepoint.host}")
	private String host;

	@Value("${auth.keyPath}")
	private Resource key;

	@Value("${auth.certPath}")
	private Resource cert;

	private String authority = "https://login.microsoftonline.com/%s/oauth2/token";

	private String scope;

	private String token;

	private Date tokenExpireDate;

	@PostConstruct
	private void postConstruct() throws Exception {

		// check that properties are set correct
		if (tenantId == null || tenantId.length() == 0)
			throw new InvalidConfigurationPropertyValueException("tenantId", tenantId,
					"tenantId must be set in .env file and can't be empty");

		if (clientId == null || clientId.length() == 0)
			throw new InvalidConfigurationPropertyValueException("clientId", clientId,
					"clientId must be set in .env file and can't be empty");

		scope = " https://" + host + "/.default";

		// Test if scope URL is valid
		URL scopeTest = new URL(scope);
		scopeTest.toURI();

		// create authority for authentication flow
		authority = String.format(authority, tenantId);

		// create token on startup
		logger.info("Creating token...");

		IAuthenticationResult result = getAccessTokenByClientCredentialGrant();
		token = result.accessToken();
		tokenExpireDate = result.expiresOnDate();

		logger.info("Token, will expire {}", tokenExpireDate);

	}

	/**
	 * Returns the authentication token of the Tenant,
	 * to authenticate requests to Microsoft's Services
	 * that are associated to the Tenant
	 *
	 * @return the token
	 * @throws Exception
	 */
	public String getToken() throws Exception {

		logger.info("Checking validity of token...");

		if (tokenExpireDate == null || tokenExpireDate.before(new Date())) {

			logger.info("Token expired, get new token");

			IAuthenticationResult result = getAccessTokenByClientCredentialGrant();
			token = result.accessToken();
			tokenExpireDate = result.expiresOnDate();

			logger.info("New token, will expire {}", tokenExpireDate);

		} else
			logger.info("Token still valid until {}", tokenExpireDate);

		return token;
	}

	private IAuthenticationResult getAccessTokenByClientCredentialGrant() throws Exception {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(ByteStreams.toByteArray(key.getInputStream()));
		PrivateKey key = KeyFactory.getInstance("RSA").generatePrivate(spec);

		InputStream certStream = new ByteArrayInputStream(ByteStreams.toByteArray(cert.getInputStream()));
		X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
				.generateCertificate(certStream);

		ConfidentialClientApplication app = ConfidentialClientApplication.builder(
				clientId,
				ClientCredentialFactory.createFromCertificate(key, cert))
				.authority(authority)
				.build();

		// With client credentials flows the scope is ALWAYS of the shape
		// "resource/.default", as the
		// application permissions need to be set statically (in the portal), and then
		// granted by a tenant administrator
		ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
				Collections.singleton(scope))
				.build();

		CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
		return future.get();
	}

}
