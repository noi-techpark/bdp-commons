package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.face.DataServiceFace;
import it.bz.idm.bdp.augeg4.fun.push.DataPusherAuge;
import it.bz.idm.bdp.augeg4.fun.push.DataPusherHub;
import it.bz.idm.bdp.augeg4.util.AugeMqttConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 * XXX Do not forget to configure it!
 */
@Component
@PropertySource({"classpath:/META-INF/spring/application.properties"})
public class JobScheduler {

    private static final Logger LOG = LogManager.getLogger(JobScheduler.class.getName());

    private DataServiceFace dataService;



    public JobScheduler(
            DataRetrieverFace retriever,
            DataPusherHub pusherHub,
            DataPusherAuge pusherAuge
    ) {
        dataService = new DataService(retriever, pusherHub, pusherAuge);
    }

    @Autowired
    public JobScheduler(
            DataRetrieverFace retriever,
            DataPusherHub pusherHub,
            ConnectorConfig config
    ) {
        DataPusherAuge pusherAuge = new DataPusherAuge(AugeMqttConfiguration.buildMqttPublisherConfiguration(config));
        dataService = new DataService(retriever, pusherHub, pusherAuge);
    }


    /**
     * Called when the data collector has been deployed
     */
    @PostConstruct
    public void onDeploy() throws Exception {
        LOG.info("onDeploy() called");
        dataService.loadPreviouslySyncedStations();
    }


    /**
     * JOB 1
     */
    public void pushDataTypes() throws Exception {
        LOG.info("pushDataTypes() called.");
        dataService.syncDataTypes();
    }


    /**
     * JOB 2
     */
    public void pushStations() throws Exception {
        LOG.info("pushStations() called");
        dataService.syncStations();
    }


    /**
     * JOB 3
     */
    public void pushData() throws Exception {
        LOG.info("pushData() called.");
        dataService.pushData();
    }


}
