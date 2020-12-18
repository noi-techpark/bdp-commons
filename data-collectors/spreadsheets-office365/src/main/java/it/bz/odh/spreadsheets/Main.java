package it.bz.odh.spreadsheets;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

@EnableScheduling
@SpringBootApplication
public class Main {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

//    @Bean
//    public ServletRegistrationBean dispatcherServletBean() {
//        ServletRegistrationBean bean = new ServletRegistrationBean(
//                new DispatcherServlet(), "/spreadsheet-dc/*");
//        bean.setLoadOnStartup(1);
//        return bean;
//    }

    @Bean
    public WebClient webClient() {
        WebClient webClient = WebClient.builder().build();
        return webClient;
    }
}
