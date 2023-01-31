package com.opendatahub.bdp.commons.dc.bikeboxes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class OAuthWebClient {
    private static final String REGISTRATION_ID = "odh";
    private static final Logger LOG = LoggerFactory.getLogger(OAuthWebClient.class);

    @Value("${endpoint.uri}")
    private String endpointBaseUri;

    @Value("${endpoint.oauth.uri}")
    private String uri;
    @Value("${endpoint.oauth.clientId}")
    private String clientId;
    @Value("${endpoint.oauth.clientSecret}")
    private String clientSecret;

    @Bean(name = "bikeParkingWebClient")
    WebClient webClient() {
        ClientRegistration registration = ClientRegistration
                .withRegistrationId(REGISTRATION_ID)
                .tokenUri(uri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(new AuthorizationGrantType("client_credentials"))
                .build();

        InMemoryReactiveClientRegistrationRepository registrationRepo = new InMemoryReactiveClientRegistrationRepository(
                registration);
        InMemoryReactiveOAuth2AuthorizedClientService clientService = new InMemoryReactiveOAuth2AuthorizedClientService(
                registrationRepo);
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                registrationRepo, clientService);

        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                authorizedClientManager);

        oauth.setDefaultClientRegistrationId(REGISTRATION_ID);

        return WebClient.builder()
                .filter(oauth)
                .baseUrl(endpointBaseUri)
                .build();
    }
}
