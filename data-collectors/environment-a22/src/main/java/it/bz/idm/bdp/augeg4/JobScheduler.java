package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.face.DataConverterFace;
import it.bz.idm.bdp.augeg4.face.DataLinearizerFace;
import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.face.DataServiceFace;
import it.bz.idm.bdp.augeg4.fun.push.DataPusher;
import it.bz.idm.bdp.augeg4.fun.retrieve.DataRetrieverMock;
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

    private DataPusher pusher;

    private DataRetrieverFace retriever;

    private DataLinearizerFace linearizer;

    private DataConverterFace converter;

    private DataServiceFace dataService;

    @Autowired
    public JobScheduler(DataPusher pusher, DataRetrieverFace retriever, DataLinearizerFace linearizer, DataConverterFace converter) {
        this.pusher = pusher;
        this.retriever = retriever;
        this.linearizer = linearizer;
        this.converter = converter;
        dataService = new DataService(pusher, linearizer, converter);
        retriever.setDataService(dataService);
    }

    /**
     * Called when the data collector has been deployed
     */
    @PostConstruct
    public void onDeploy () throws Exception {
        LOG.info("onDeploy() called");
        loadPreviouslySyncedStations();
        pushDataTypes();
    }


    void loadPreviouslySyncedStations () throws Exception {
        LOG.info("loadPreviouslySyncedStations() called");
        dataService.loadPreviouslySyncedStations();
    }

    void pushDataTypes() throws Exception {
        LOG.info("pushDataTypes() called.");
        dataService.syncDataTypesWithHub();
    }

    /**
     * JOB 1
     */
    public void pushStations() throws Exception {
        LOG.info("pushStations() called");
        dataService.syncStationsWithHub();
    }

    /**
     * JOB 2
     */
    public void pushData() throws Exception {
        LOG.info("pushData() called.");
        dataService.pushData();
    }

    /**
     * JOB 3
     */
    public void mockDataRetrievedFromAlgorab() {
        LOG.info("mockDataRetrievedFromAlgorab() called.");
        if (retriever instanceof DataRetrieverMock) {
            DataRetrieverMock mock = (DataRetrieverMock) retriever;
            mock.mockDataRetrievedFromAlgorab();
        }
    }

}
