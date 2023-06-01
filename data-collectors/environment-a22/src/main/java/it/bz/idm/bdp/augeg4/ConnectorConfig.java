// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConnectorConfig {

    public boolean mqtt_unit_test=false;

    @Value("${mqtt.clean_session_flag}")
    public boolean mqtt_clean_session_flag=false;

    @Value("${mqtt.quality_of_service}")
    public int mqtt_quality_of_service=1;



    @Value("${mqtt.subscriber.username}")
    public String mqtt_subscriber_username="testMQTT";

    @Value("${mqtt.subscriber.password}")
    public String mqtt_subscriber_password="testMQTT";

    @Value("${mqtt.subscriber.uri}")
    public String mqtt_subscriber_uri="tcp://mqtt-test.algorab.net";

    @Value("${mqtt.subscriber.port}")
    public String mqtt_subscriber_port="61619";

    @Value("${mqtt.subscriber.clientid}")
    public String mqtt_subscriber_clientid="airQuinoLinearization";

    @Value("${mqtt.subscriber.topic}")
    public String mqtt_subscriber_topic="AirQuino/RawData";



    @Value("${mqtt.publisher.username}")
    public String mqtt_publisher_username="testMQTT";

    @Value("${mqtt.publisher.password}")
    public String mqtt_publisher_password="testMQTT";

    @Value("${mqtt.publisher.uri}")
    public String mqtt_publisher_uri="tcp://mqtt-test.algorab.net";

    @Value("${mqtt.publisher.port}")
    public String mqtt_publisher_port="61619";

    @Value("${mqtt.publisher.clientid}")
    public String mqtt_publisher_clientid="airQuinoLinearization";

    @Value("${mqtt.publisher.topic}")
    public String mqtt_publisher_topic="AirQuino/LinearizedData";

}
