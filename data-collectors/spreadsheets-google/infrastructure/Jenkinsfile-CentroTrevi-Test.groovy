pipeline {
    agent any
    
    environment {
        SERVER_PORT="1007"
        PROJECT = "spreadsheets-google"
        PROJECT_FOLDER = "data-collectors/${PROJECT}"
        LOG_FOLDER = "/var/log/opendatahub/data-collectors"
        ARTIFACT_NAME = "dc-${PROJECT}"
        GOOGLE_SECRET=credentials('spreadsheets.client_secret.json')
        GOOGLE_CREDENTIALS=credentials('google-spreadsheet-api-credentials')
        KEYCLOAK_CONFIG=credentials('test-authserver-datacollector-client-config')
        DOCKER_IMAGE = '755952719952.dkr.ecr.eu-west-1.amazonaws.com/spreadsheets-google-noiplaces'
        DOCKER_TAG = "test-$BUILD_NUMBER"
        VENDOR = "centroTrevi"
    }

    stages {
        stage('Configure') {
            steps {
                sh """
                    cd ${PROJECT_FOLDER}
                    echo 'SERVER_PORT=${SERVER_PORT}' > .env
                    echo 'COMPOSE_PROJECT_NAME=${VENDOR}' >> .env
                    echo 'DOCKER_IMAGE=${DOCKER_IMAGE}' >> .env
                    echo 'DOCKER_TAG=${DOCKER_TAG}' >> .env
                    echo 'LOG_LEVEL=DEBUG' >> .env
                    echo 'LOG_FOLDER=data-collectors/${PROJECT}' >> .env
                    echo 'ARTIFACT_NAME=${ARTIFACT_NAME}' >> .env
                    echo 'VENDOR=${VENDOR}' >> .env
                    echo 'spreadsheetId=1aJW6sEGo40hWeL_B2yK4N7CIGwRlmwVpAkwqxjF1ruA' >> .env
                    echo 'suportedLanguages=en,de,it,lad' >> .env
                    echo 'headers_nameId=it:name' >> .env
                    echo 'headers_addressId=it:address' >> .env
                    echo 'headers_metaDataId=event-id' >> .env
                    echo 'spreadsheet_range=A1:Z' >> .env
                    echo 'spreadsheet_notificationUrl=https://spreadsheets.testingmachine.eu/centro-trevi/trigger' >> .env
                    echo 'stationtype=Culture' >> .env
                    echo 'composite_unique_key=id' >> .env
                    echo 'origin=municipality bolzano' >> .env
                    echo -n 'provenance_version=' >> .env
                    xmlstarlet sel -N pom=http://maven.apache.org/POM/4.0.0 -t -v '/pom:project/pom:version' pom.xml >> .env
                    echo '' >> .env
                    echo -n 'provenance_name=' >> .env 
                    xmlstarlet sel -N pom=http://maven.apache.org/POM/4.0.0 -t -v '/pom:project/pom:artifactId' pom.xml >> .env
                    echo '' >> .env
                """
                sh "cat ${KEYCLOAK_CONFIG} >> ${PROJECT_FOLDER}/.env"
                
                sh "cat ${GOOGLE_SECRET} > ${PROJECT_FOLDER}/src/main/resources/META-INF/spring/client_secret.json"
                sh """cat "${GOOGLE_CREDENTIALS}" > "${PROJECT_FOLDER}"/src/main/resources/META-INF/credentials/StoredCredential"""
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
                        (cd ${PROJECT_FOLDER}/infrastructure/ansible && ansible-galaxy install -f -r requirements.yml)
                        (cd ${PROJECT_FOLDER}/infrastructure/ansible && ansible-playbook --limit=test deploy.yml --extra-vars "release_name=${BUILD_NUMBER} project_name=${PROJECT}-${VENDOR}")
                    """
                }
            }
        }
    }
}
