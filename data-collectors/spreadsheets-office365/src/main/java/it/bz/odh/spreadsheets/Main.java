package it.bz.odh.spreadsheets;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
public class Main {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }
}
