package it.bz.odh.spreadsheets.services;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import it.bz.odh.spreadsheets.dto.SubscriptionDto;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


//@Component
public class GraphChangeNotificationClient {

    private static final String SUBSCRIPTION_URL = "https://graph.microsoft.com/v1.0/subscriptions";
    //    @Value("callback")
    private String callbackUrl = "https://dull-dingo-63.loca.lt";
    private RestTemplate restTemplate;

    public GraphChangeNotificationClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public void makeSubscription(String token) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization","Bearer " +token);

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setChangeType("updated");
        subscriptionDto.setNotificationUrl(callbackUrl+"/trigger/notification");
        subscriptionDto.setExpirationDateTime("2020-10-04T04:30:28.2257768Z");
        subscriptionDto.setResource("/drives/b!xai8c3iLQEag4k-gtiSDJ-SuPmIk8xpOg71pKMIh2mxfi3aBVuSLTougIClWC5qj/root");



//        headers.set("Content-Length",body.length()+"");

        HttpEntity<SubscriptionDto> entity = new HttpEntity<SubscriptionDto>(subscriptionDto, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(SUBSCRIPTION_URL, entity, String.class);
        System.out.println(response.getBody());
    }

    public void renewSubscription() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>("{" +
                "  'expirationDateTime': '2019-03-14T04:33:36.2394526+00:00'" +
                "}", headers);
        ResponseEntity<String> response = restTemplate.postForObject(SUBSCRIPTION_URL, entity, ResponseEntity.class);
    }

}
