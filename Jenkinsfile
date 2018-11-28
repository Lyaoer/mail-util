node("${NODE_DEFINED}") {
    timestamps {
        stage('Checkout') {
	    checkout scm
        }


        withEnv(["DOCKER_TAG=${PAAS_ENV}-build-${env.BUILD_NUMBER}","DOCKER_REGISTRY=${HUB}"]) {
            stage("build") {
                sh '''
                    sed -i 's/mirrors.fzyun.io/mirrors.aliyun.com/g' /etc/apt/sources.list
                    apt-get update
                    apt-get install -y --no-install-recommends maven
                    mvn clean install -U -Pbuild-push-docker -Ddocker_tag=${PAAS_ENV}-build-${BUILD_NUMBER} -Dregistry_url=${HUB} -f ./pom.xml
                '''
            }
            stage("deploy") {
                sh '''
                    rancher-compose -p mail up -d --force-recreate --confirm-upgrade
                '''
            }
        }
    }
}
