pipeline {
    agent {
        dockerfile {
            args '-v /root/.m2:/root/.m2'
        }
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
        stage('IT') {
            steps {
                sh 'cp -u web-utils/it-config/jbd.ini-template web-utils/it-config/jbd.ini'
                sh 'xvfb-run mvn -B verify -DskipTests -DskipITs=false'
            }
            post {
                always {
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }
    }
}
