pipeline {
    agent {
        dockerfile {
            filename 'docker/dockerfile-java'
            additionalBuildArgs '--build-arg JENKINS_USER_ID=`id -u jenkins` --build-arg JENKINS_GROUP_ID=`id -g jenkins`'
        }
    }

    stages {
        stage('Test data collectors and web services') {
            stages {
                stage('API v1') {
                    steps {
                        sh 'cd webservices/bdp-api/ && mvn -B -U clean compile test'
                    }
                }
                stage('Data Collectors') {
                    steps {
                        sh '''
                            echo "!!! WE TEST ALL DATA COLLECTORS THAT HAVE BEEN CHANGED SINCE LAST COMMIT !!!"
                            readarray -d / -t GIT_DELTA <<< "$(git diff --name-only HEAD HEAD~2 | grep data-collectors/)"
                            echo "Change Data Collectors:"
                            for DC_NAME in "${GIT_DELTA[@]}"; do
                                echo "- $DC_NAME"
                            done
                            cd data-collectors
                            for DC_NAME in "${GIT_DELTA[@]}"; do
                                (cd "$DC_NAME" && mvn -B -U clean compile test)
                            done
                            echo "!!! Ready."
                        '''

                    }
                }
            }
        }
    }
}
