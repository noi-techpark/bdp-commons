package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.face.DataServiceFace;
import it.bz.idm.bdp.augeg4.fun.push.DataPusherAuge;
import it.bz.idm.bdp.augeg4.fun.push.DataPusherHub;
import it.bz.idm.bdp.augeg4.util.AugeMqttConfiguration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
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

    private static final Logger LOG = LoggerFactory.getLogger(JobScheduler.class.getName());

    private DataServiceFace dataService;

	private ConnectorConfig config;

	private DataRetrieverFace retriever;

	private DataPusherHub pusherHub;

    @Autowired
    public JobScheduler(
            DataRetrieverFace retriever,
            DataPusherHub pusherHub,
            ConnectorConfig config
    ) {
        LOG.info("mqtt push pass:"+config.mqtt_publisher_password);
        LOG.info("mqtt push user:"+config.mqtt_publisher_username);
        LOG.info("mqtt push client:"+config.mqtt_publisher_clientid);
        LOG.info("mqtt push port:"+config.mqtt_publisher_port);
        LOG.info("mqtt push topic:"+config.mqtt_publisher_topic);
        LOG.info("mqtt push uri:"+config.mqtt_publisher_uri);
        this.config = config;
        this.retriever =retriever;
        this.pusherHub = pusherHub;
    }


    /**
     * Called when the data collector has been deployed
     */
    @PostConstruct
    public void onDeploy() throws Exception {
        LOG.info("onDeploy() called");
        DataPusherAuge pusherAuge = new DataPusherAuge(AugeMqttConfiguration.buildMqttPublisherConfiguration(config));
        dataService = new DataService(retriever, pusherHub, pusherAuge);
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
        dataService.loadPreviouslySyncedStations();
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
