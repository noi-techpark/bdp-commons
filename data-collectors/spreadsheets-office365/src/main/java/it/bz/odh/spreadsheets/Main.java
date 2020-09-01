package it.bz.odh.spreadsheets;


import com.microsoft.graph.models.extensions.Drive;
import com.microsoft.graph.models.extensions.User;
import it.bz.odh.spreadsheets.services.GraphApiAuthenticator;
import it.bz.odh.spreadsheets.utils.Graph;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
public class Main {
    public static void main(String[] args) {



        String token = GraphApiAuthenticator.getUserAccessToken();
        System.out.println(token);
        Drive drive = Graph.getDrive(token);
        System.out.println(drive.items);
        Graph.getWorksheetUsedRange(token);

        //foe now just try to fetch data from worksheet, Speing boot will be activated in next step
//        SpringApplication.run(Main.class, args);
    }
}
