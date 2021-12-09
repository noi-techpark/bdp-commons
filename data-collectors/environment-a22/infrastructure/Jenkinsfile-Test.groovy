pipeline {
    agent any

    environment {
        LIMIT = "test"
        PROJECT = "environment-a22"
        PROJECT_FOLDER = "data-collectors/${PROJECT}"
        ARTIFACT_NAME = "dc-${PROJECT}"
        DOCKER_IMAGE = "755952719952.dkr.ecr.eu-west-1.amazonaws.com/${ARTIFACT_NAME}"
        DOCKER_TAG = "$LIMIT-$BUILD_NUMBER"
        DATACOLLECTORS_CLIENT_SECRET = credentials("keycloak-datacollectors-secret-${LIMIT}")
        KEYCLOAK_URL = "https://auth.opendatahub.testingmachine.eu"
        WRITER_URL = "https://mobility.share.opendatahub.testingmachine.eu"
        LOG_LEVEL = "info"
        MQTT_USERNAME = credentials('a22-mqtt-username')
        MQTT_PASSWORD = credentials('a22-mqtt-password')
        MQTT_URI = credentials('a22-mqtt-uri')
        MQTT_PORT = credentials('a22-mqtt-port')
    }

    stages {
        stage('Configure') {
            steps {
                sh """
                    cd ${PROJECT_FOLDER}
                    echo 'COMPOSE_PROJECT_NAME=${ARTIFACT_NAME}' > .env
                    echo 'DOCKER_IMAGE=${DOCKER_IMAGE}' >> .env
                    echo 'DOCKER_TAG=${DOCKER_TAG}' >> .env
                    echo 'LOG_LEVEL=${LOG_LEVEL}' >> .env
                    echo 'ARTIFACT_NAME=${ARTIFACT_NAME}' >> .env
                    echo 'authorizationUri=${KEYCLOAK_URL}/auth' >> .env
                    echo 'tokenUri=${KEYCLOAK_URL}/auth/realms/noi/protocol/openid-connect/token' >> .env
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
                    echo 'BASE_URI=${WRITER_URL}/json' >> .env
                    echo 'MQTT_USERNAME=${MQTT_USERNAME}' >> .env
                    echo 'MQTT_PASSWORD=${MQTT_PASSWORD}' >> .env
                    echo 'MQTT_URI=${MQTT_URI}' >> .env
                    echo 'MQTT_PORT=${MQTT_PORT}' >> .env
                """
            }
        }
        stage('Test & Build') {
            steps {
                sh """
                    cd ${PROJECT_FOLDER}
                    aws ecr get-login --region eu-west-1 --no-include-email | bash
                    docker-compose --no-ansi -f infrastructure/docker-compose.build.yml build --pull
                    docker-compose --no-ansi -f infrastructure/docker-compose.build.yml push
                """
            }
        }
        stage('Deploy') {
            steps {
               sshagent(['jenkins-ssh-key']) {
                    sh """
                        cd ${PROJECT_FOLDER}/infrastructure/ansible
                        ansible-galaxy install -f -r requirements.yml
                        ansible-playbook --limit=test deploy.yml --extra-vars "release_name=${BUILD_NUMBER} project_name=${ARTIFACT_NAME}"
                    """
                }
            }
        }
    }
}
