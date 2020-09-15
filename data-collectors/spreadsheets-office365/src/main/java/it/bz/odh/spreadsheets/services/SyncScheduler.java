package it.bz.odh.spreadsheets.services;

import it.bz.odh.spreadsheets.utils.GraphDataFetcher;
import it.bz.odh.spreadsheets.utils.XLSXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SyncScheduler.class);

    @Autowired
    private GraphDataFetcher graphDataFetcher;

    @Autowired
    XLSXReader xlsxReader;

    @Scheduled(cron = "${CRON}")
    public void fetchSheet() throws IOException {
        logger.debug("Fetch sheet started");
        graphDataFetcher.fetchSheet();
        xlsxReader.readSheet();
        logger.debug("Fetch sheet done");
    }
}

