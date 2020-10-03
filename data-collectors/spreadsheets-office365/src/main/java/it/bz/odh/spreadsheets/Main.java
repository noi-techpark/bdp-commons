package it.bz.odh.spreadsheets;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
public class Main {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

//    @Bean
//    public ServletRegistrationBean exampleServletBean() {
//        ServletRegistrationBean bean = new ServletRegistrationBean(
//                new DispatcherServlet(), "/spreadsheet-dc/*");
//        bean.setLoadOnStartup(1);
//        return bean;
//    }
}
