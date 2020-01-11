pipeline {
    agent { dockerfile true }
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
                sh 'xvfb-run mvn -B integration-test -DskipTests -DskipITs=false'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
//        stage('Deliver') { 
//            steps {
//                sh 'build-scripts/deliver.sh' 
//            }
//        }
    }
}
