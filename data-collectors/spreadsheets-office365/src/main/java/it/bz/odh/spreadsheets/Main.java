package it.bz.odh.spreadsheets;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.DispatcherServlet;


@EnableScheduling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Main {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public ServletRegistrationBean dispatcherServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(
                new DispatcherServlet(), "/spreadsheet-dc/*");
        bean.setLoadOnStartup(1);
        return bean;
    }

    @Bean
    public WebClient webClientBean() {
        WebClient webClient = WebClient.builder().build();
        return webClient;
    }
}
