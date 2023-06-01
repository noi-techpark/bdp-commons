// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4;

import org.junit.Test;

import it.bz.idm.bdp.augeg4.face.DataPusherAugeFace;
import it.bz.idm.bdp.augeg4.face.DataPusherHubFace;
import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.face.DataServiceFace;
import it.bz.idm.bdp.augeg4.fun.retrieve.DataRetriever;
import it.bz.idm.bdp.augeg4.mock.DataPusherAugeMock;
import it.bz.idm.bdp.augeg4.mock.DataPusherHubMockLog;

public class DataServiceWithPusherMocksIMT {


    private static final int PERIOD = 600;

    public static void main(String[] args) throws Exception {
        new DataServiceWithPusherMocksIMT().test_data_pipeline_from_retrieval_with_pusher_mock();
    }

    @Test
    public void test_data_pipeline_from_retrieval_with_pusher_mock() throws Exception {
        // given
        ConnectorConfig config = new ConnectorConfig();
        config.mqtt_unit_test=true;
        DataRetrieverFace retriever = new DataRetriever(config);
        DataPusherAugeFace pusherAuge = new DataPusherAugeMock();
        DataPusherHubFace pusherHub = new DataPusherHubMockLog(PERIOD);
        DataServiceFace dataService = new DataService(retriever, pusherHub, pusherAuge);
        // when
        dataService.pushData();
        // then
        // ... will print fetched auge data messages
    }

    @Test
    public void test_stations_pipeline_from_retrieval_with_pusher_mock() throws Exception {
        // given
        ConnectorConfig config = new ConnectorConfig();
        config.mqtt_unit_test=true;
        DataRetrieverFace retriever = new DataRetriever(config);
        DataPusherAugeFace pusherAuge = new DataPusherAugeMock();
        DataPusherHubFace pusherHub = new DataPusherHubMockLog(PERIOD);
        DataServiceFace dataService = new DataService(retriever, pusherHub, pusherAuge);
        // when
        dataService.syncStations();
        // then
        // ... will print fetched auge stations derived from messages
    }

    @Test
    public void test_datatypes_pipeline_from_retrieval_with_pusher_mock() throws Exception {
        // given
        ConnectorConfig config = new ConnectorConfig();
        config.mqtt_unit_test=true;
        DataRetrieverFace retriever = new DataRetriever(config);
        DataPusherAugeFace pusherAuge = new DataPusherAugeMock();
        DataPusherHubFace pusherHub = new DataPusherHubMockLog(PERIOD);
        DataServiceFace dataService = new DataService(retriever, pusherHub, pusherAuge);
        // when
        dataService.syncDataTypes();
        // then
        // ... will print fixed auge datatypes
    }

}
