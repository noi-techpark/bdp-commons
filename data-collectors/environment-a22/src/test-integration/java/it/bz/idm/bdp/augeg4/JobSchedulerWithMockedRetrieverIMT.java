// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.augeg4.fun.push.DataPusherHub;
import it.bz.idm.bdp.augeg4.mock.DataRetrieverMock;

@ContextConfiguration(locations = {"classpath:/META-INF/spring/applicationContext.xml"})
public class JobSchedulerWithMockedRetrieverIMT extends AbstractJUnit4SpringContextTests {

    @Autowired
    private DataPusherHub pusherHub;

    private DataRetrieverMock retriever = new DataRetrieverMock();

    @Autowired
    private ConnectorConfig config;

    @Test
    public void test_scheduled_push_data_with_mocked_retriever () throws Exception {
        // given
        JobScheduler scheduler = new JobScheduler(retriever, pusherHub, config);

        // when
        scheduler.pushData();
        scheduler.pushDataTypes();
        scheduler.pushStations();

        // then no exception is thrown
    }
}
