pipeline{
    agent any
    tools {
        maven 'maven_3_9_5'
    }
    stages {
        stage ('Build Maven') {
            steps {
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/lxndr-reset/crypto-technicals-parser']])
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Build Docker image') {
            steps{
                script{
                    sh 'docker-compose down'
                    sh 'docker-compose up --build -d'
                    sh 'docker image prune -af'
                }
            }
        }

        stage('Push to DockerHub') {
            steps{
                script{
                    withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerhubpwd'), string(credentialsId: 'dockerhub-uname', variable: 'dockerhubusername')]) {
                        sh 'docker login -u ${dockerhubusername} -p ${dockerhubpwd}'
                    }
                    sh 'docker push lxndrreset/technicals-parser'
                }
            }
        }
    }
}