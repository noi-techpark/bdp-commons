pipeline {
    agent {
        dockerfile {
            filename 'docker/dockerfile-java'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=`id -u jenkins` --build-arg JENKINS_GROUP_ID=`id -g jenkins`'
        }
    }

    stages {
        // TODO Remove API v1 and nested stages when we deprecate the API
        stage('Test data collectors and web services') {
            stages {
                stage('API v1') {
                    steps {
                        sh 'true # cd webservices/bdp-api/ && mvn -B -U clean compile test'
                    }
                }
                stage('Data Collectors') {
                    steps {
                        sh './ci-check-data-collectors.sh'
                    }
                }
            }
        }
    }
}
