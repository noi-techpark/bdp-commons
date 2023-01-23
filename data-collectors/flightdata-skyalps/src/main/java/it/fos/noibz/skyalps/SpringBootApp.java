package it.fos.noibz.skyalps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@ComponentScan({"it.fos.noibz", "it.bz.idm.bdp" })
@SpringBootApplication
public class SpringBootApp {
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootApp.class, args);
	}

}
