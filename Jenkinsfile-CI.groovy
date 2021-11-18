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
                            echo
                            echo "!!! WE TEST ALL DATA COLLECTORS THAT HAVE BEEN CHANGED SINCE LAST COMMIT !!!"
                            echo
                            DELTA_FILES=$(git diff --name-only HEAD "$GIT_PREVIOUS_SUCCESSFUL_COMMIT" | grep data-collectors/ || true)
                            echo "Changed Data Collectors:"
                            declare -a DC_SET
                            for RECORD in ${DELTA_FILES[@]}; do
                                RECORD=$(echo "$RECORD" | sed -E 's#data-collectors/(.*)/.*$#\1#g')
                                DC_SET+=("$RECORD")
                            done
                            DC_SET=($(printf "%s\n" "${DC_SET[@]}" | sort -u))
                            cd data-collectors
                            for DC_NAME in "${DC_SET[@]}"; do
                                (cd "$DC_NAME" && mvn -B -U clean compile test)
                            done
                            echo
                            echo "!!! Ready !!!"
                            echo
                        '''

                    }
                }
            }
        }
    }
}
