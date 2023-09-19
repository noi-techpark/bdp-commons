// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class A22ClientConfig {
        @Value("${a22.url}")
        private String url;

        @Value("${a22.username}")
        private String userName;

        @Value("${a22.password}")
        private String password;

        @Bean(name = "a22Client")
        WebClient webClient() {
                return WebClient.builder()
                                .defaultHeaders(header -> header.setBasicAuth(userName, password))
                                .baseUrl(url)
                                .build();
        }
}
