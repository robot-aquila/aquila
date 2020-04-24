pipeline {
    agent {
        dockerfile {
            args '-v /root/.m2:/root/.m2'
        }
    }
	environment {
		MAVEN_OPTS='-XX:-OmitStackTraceInFastThrow'
	}
    stages {
        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests clean package' 
            }
        }
        stage('Test') {
            steps {
                sh 'xvfb-run mvn -B test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        stage('Install') {
            steps {
                sh 'mvn -B -DskipTests install'
            }
        }
        stage('Deploy') {
            steps {
                sh 'chmod +x utils/finexp-futures/ci-scripts/deploy-ff-release.sh'
                sh 'utils/finexp-futures/ci-scripts/deploy-ff-release.sh'
            }
        }
    }
}
