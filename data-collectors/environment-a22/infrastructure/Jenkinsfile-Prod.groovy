pipeline {
    agent any

    environment {
        PROJECT = "environment-a22"
        PROJECT_FOLDER = "data-collectors/${PROJECT}"
        ARTIFACT_NAME = "dc-${PROJECT}"
        DOCKER_IMAGE = '755952719952.dkr.ecr.eu-west-1.amazonaws.com/dc-environment-a22'
        DOCKER_TAG = "prod-$BUILD_NUMBER"
        DATACOLLECTORS_CLIENT_SECRET = credentials('keycloak-datacollectors-secret-prod')
        A22_MQTT_USERNAME=credentials('a22-mqtt-username')
        A22_MQTT_PASSWORD=credentials('a22-mqtt-password')
        A22_MQTT_PORT=credentials('a22-mqtt-port')
        A22_MQTT_URI=credentials('a22-mqtt-uri')

    }

    stages {
        stage('Configure') {
            steps {
                sh """
                    cd ${PROJECT_FOLDER}
                    echo 'COMPOSE_PROJECT_NAME=${PROJECT}' > .env
                    echo 'DOCKER_IMAGE=${DOCKER_IMAGE}' >> .env
                    echo 'DOCKER_TAG=${DOCKER_TAG}' >> .env
                    echo 'LOG_LEVEL=info' >> .env
                    echo 'ARTIFACT_NAME=${ARTIFACT_NAME}' >> .env
                    echo 'authorizationUri=https://auth.opendatahub.bz.it/auth' >> .env
                    echo 'tokenUri=https://auth.opendatahub.bz.it/auth/realms/noi/protocol/openid-connect/token' >> .env
                    echo 'clientId=odh-mobility-datacollector' >> .env
                    echo 'clientName=odh-mobility-datacollector' >> .env
                    echo 'clientSecret=${DATACOLLECTORS_CLIENT_SECRET}' >> .env
                    echo 'scope=openid' >> .env
                    echo -n 'provenance_version=' >> .env
                    xmlstarlet sel -N pom=http://maven.apache.org/POM/4.0.0 -t -v '/pom:project/pom:version' pom.xml >> .env
                    echo '' >> .env
                    echo -n 'provenance_name=' >> .env
                    xmlstarlet sel -N pom=http://maven.apache.org/POM/4.0.0 -t -v '/pom:project/pom:artifactId' pom.xml >> .env
                    echo '' >> .env
                    echo 'BASE_URI=https://mobility.share.opendatahub.bz.it/json' >> .env
                    echo 'MQTT_USERNAME=${A22_MQTT_USERNAME}' >> .env
                    echo 'MQTT_PASSWORD=${A22_MQTT_PASSWORD}' >> .env
                    echo 'MQTT_PORT=${A22_MQTT_PORT}' >> .env
                    echo 'MQTT_URI=${A22_MQTT_URI}' >> .env
            }
        }
        stage('Test & Build') {
            steps {
                sh 'cd ${PROJECT_FOLDER} && mvn -B -U clean compile test'
            }
        }
        stage('Build') {
            steps {
                sh 'cd ${PROJECT_FOLDER} && mvn package'
            }
        }
        stage('Archive') {
            steps {
                sh 'cp ${PROJECT_FOLDER}/target/${ARTIFACT_NAME}.war ${ARTIFACT_NAME}.war'
                sh 'zip ${ARTIFACT_NAME}.zip .env ${ARTIFACT_NAME}.war'
                archiveArtifacts artifacts: "${ARTIFACT_NAME}.zip", onlyIfSuccessful: true
            }
        }
    }
}
