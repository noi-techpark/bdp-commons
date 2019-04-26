package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.face.DataConverterFace;
import it.bz.idm.bdp.augeg4.face.DataLinearizerFace;
import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.fun.linearize.DataLinearizer;
import it.bz.idm.bdp.augeg4.fun.push.DataPusher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 * XXX Do not forget to configure it!
 */
@Component
public class JobScheduler {

    private static final Logger LOG = LogManager.getLogger(JobScheduler.class.getName());

    @Autowired
    private Environment env;

    @Autowired
    private DataPusher pusher;

    @Autowired
    private DataRetrieverFace retrieval;

    private DataLinearizerFace linearizer = new DataLinearizer();

    @Autowired
    private DataConverterFace converter;

    private DataService dataService;

    /**
     * JOB 1
     */
    public void pushStations() throws Exception {
        LOG.info("pushStations() called");
        getDataService().pushStations(env.getProperty("station.type"), env.getProperty("origin"));
    }

    /**
     * JOB 2
     */
    public void pushDataTypes() throws Exception {
        LOG.info("pushDataTypes() called.");
        getDataService().pushDataTypes(env.getProperty("period", Integer.class));
    }

    /**
     * JOB 3
     */
    public void pushData() throws Exception {
        LOG.info("pushData() called.");
        getDataService().pushData();
    }

    private DataService getDataService() {
        if (dataService == null)
            this.dataService = new DataService(pusher, retrieval, linearizer, converter);
        return this.dataService;
    }
}
