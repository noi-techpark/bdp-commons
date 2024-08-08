// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.carpooling;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OTPAmarilloSync {

	private static final Logger LOG = LoggerFactory.getLogger(OTPAmarilloSync.class);

	@Value("${amarillo.url}")
	private String url;

	@Value("${amarillo.secret}")
	private String secret;

	public void triggerSync() {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.POST(HttpRequest.BodyPublishers.noBody())
				.header("X-API-Key", secret)
				.build();
		try {
			client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
		} catch (IOException | InterruptedException e) {
			LOG.error("error during sync with otp amarillo: {}", e.getMessage());
		}
	}
}
