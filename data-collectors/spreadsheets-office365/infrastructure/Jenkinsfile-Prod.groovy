pipeline {
    agent any
    
    environment {
        PROJECT = "spreadsheets-office365"
        PROJECT_FOLDER = "data-collectors/${PROJECT}"
        LOG_FOLDER = "/var/log/opendatahub/data-collectors"
        ARTIFACT_NAME = "dc-${PROJECT}"
        DOCKER_IMAGE = '755952719952.dkr.ecr.eu-west-1.amazonaws.com/dc-spreadsheets-office365-noiplaces'
        DOCKER_TAG = "prod-$BUILD_NUMBER"
        DATACOLLECTORS_CLIENT_SECRET = credentials('keycloak-datacollectors-secret-prod')
        APP_KEY = credentials('office-365-pkcs8-key')
        APP_KEY_PATH= 'auth/pkcs8_key'
        APP_CRT = credentials('office-365-crt')
        APP_CRT_PATH = 'auth/cert.crt'
        APP_TENANT_ID = credentials('office365-tenant-id')
        APP_CLIENT_ID = credentials('office365-client-id')

        JAVA_OPTIONS = "-Xms128m -Xmx512m"
    }

    stages {
        stage('Configure') {
            steps {
                sh """
                    cd ${PROJECT_FOLDER}
                    echo 'COMPOSE_PROJECT_NAME=${ARTIFACT_NAME}' > .env
                    echo 'DOCKER_IMAGE=${DOCKER_IMAGE}' >> .env
                    echo 'DOCKER_TAG=${DOCKER_TAG}' >> .env
                    echo 'LOG_LEVEL=INFO' >> .env
                    echo 'ARTIFACT_NAME=${ARTIFACT_NAME}' >> .env
                    echo 'origin=NOI Techpark' >> .env
                    echo 'SHEET_NAME=NOIbuildings.xlsx' >> .env
                    echo 'SHAREPOINT_HOST=noibz.sharepoint.com' >> .env
                    echo 'SHAREPOINT_SITE_ID=UNITshrdTech-TransferDigital' >> .env
                    echo 'SHAREPOINT_PATH_TO_DOC=General/NOI-Techpark-MapsBackend.xlsx' >> .env
                    echo 'CRON=0 * * * * *'>> .env
                    echo 'TENANT_ID=${APP_TENANT_ID}' >>.env
                    echo 'CLIENT_ID=${APP_CLIENT_ID}' >> .env
                    echo 'KEY_PATH=classpath:${APP_KEY_PATH}' >> .env
                    echo 'CERT_PATH=classpath:${APP_CRT_PATH}' >> .env

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
                    echo 'JAVA_OPTIONS=${JAVA_OPTIONS}' >> .env
                """
                sh "cat ${APP_CRT} > ${PROJECT_FOLDER}/src/main/resources/${APP_CRT_PATH}"
                sh "cat ${APP_KEY} > ${PROJECT_FOLDER}/src/main/resources/${APP_KEY_PATH}"
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
                        (cd ${PROJECT_FOLDER}/infrastructure/ansible && ansible-playbook --limit=prod deploy.yml --extra-vars "release_name=${BUILD_NUMBER} project_name=${PROJECT}")
                    """
                }
            }
        }
    }
}
