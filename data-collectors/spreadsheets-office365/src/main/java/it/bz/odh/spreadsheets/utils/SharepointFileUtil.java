package it.bz.odh.spreadsheets.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.bz.odh.spreadsheets.services.microsoft.AuthTokenGenerator;

/**
 * To fetch files from a Sharepoint site
 */
@Component
public class SharepointFileUtil {

	private static final Logger logger = LoggerFactory.getLogger(SharepointFileUtil.class);

	@Autowired
	private AuthTokenGenerator authTokenGenerator;

	@Autowired
	private SharepointDateUtil dateUtil;

	@Value("${sharepoint.host}")
	private String sharePointHost;

	@Value("${sharepoint.site-id}")
	private String siteId;

	@Value("${sharepoint.path-to-files}")
	private String pathToFiles;

	private URL pathToImage;

	public HttpURLConnection fetchFile(String name) throws Exception {

		String token = authTokenGenerator.getToken();

		pathToImage = new URL(
				"https://" + sharePointHost + "/sites/" + siteId + "/_api/web/getfilebyserverrelativeurl('/sites/"
						+ siteId + "/Shared%20Documents/" + pathToFiles + name + "')/$value");

		// Test that both URLs are valid
		pathToImage.toURI(); // does the extra checking required for validation of URI

		HttpURLConnection conn = (HttpURLConnection) pathToImage.openConnection();

		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Bearer " + token);

		int httpResponseCode = conn.getResponseCode();
		if (httpResponseCode == HTTPResponse.SC_OK) {
			logger.info("Fetching file done");
			return conn;

		} else {
			String errorMessage = String.format(
					"Fetching workbook failed. Connection returned HTTP code: %s with message: %s",
					httpResponseCode, conn.getResponseMessage());
			logger.error(errorMessage);
			throw new IOException(errorMessage);
		}
	}

	public Date getLastTimeModified(String name) throws Exception {

		String token = authTokenGenerator.getToken();

		pathToImage = new URL(
				"https://" + sharePointHost + "/sites/" + siteId + "/_api/web/getfilebyserverrelativeurl('/sites/"
						+ siteId + "/Shared%20Documents/" + pathToFiles + name + "')/TimeLastModified/$value");

		// Test that both URLs are valid
		pathToImage.toURI(); // does the extra checking required for validation of URI

		HttpURLConnection conn = (HttpURLConnection) pathToImage.openConnection();

		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Bearer " + token);
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("odata", "verbose");

		int httpResponseCode = conn.getResponseCode();
		if (httpResponseCode == HTTPResponse.SC_OK) {
			logger.info("Fetching file done");
			StringBuilder response;
			try (BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()))) {

				String inputLine;
				response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			}
			String dateTime = response.toString();

			logger.info("Getting TimeLastModified of file: {} done: {}", name, dateTime);
			return dateUtil.parseDate(dateTime);

		} else {
			String errorMessage = String.format(
					"Fetching last time modified of file: %s failed. Connection returned HTTP code: %s with message: %s",
					name, httpResponseCode, conn.getResponseMessage());
			logger.error(errorMessage);
			throw new IOException(errorMessage);
		}
	}
}
