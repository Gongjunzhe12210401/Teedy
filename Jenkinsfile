pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'gongjunzhe/teedy'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    tools {
        maven 'M3'
    }

    stages {
        stage('Build') {
            steps {
                checkout scm
                sh 'mvn -B -DskipTests clean package'
            }
        }

        stage('Login to DockerHub (manual)') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub_credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_TOKEN')]) {
                    sh '''
                    echo "$DOCKER_TOKEN" | docker login -u "$DOCKER_USER" --password-stdin
                    '''
                }
            }
        }

        stage('Build Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE:$DOCKER_TAG .'
            }
        }

        stage('Push Image') {
            steps {
                sh '''
                docker push $DOCKER_IMAGE:$DOCKER_TAG
                docker tag $DOCKER_IMAGE:$DOCKER_TAG $DOCKER_IMAGE:latest
                docker push $DOCKER_IMAGE:latest
                '''
            }
        }

        stage('Run Containers') {
            steps {
                script {
                    ['8082', '8083', '8084'].each { port ->
                        sh "docker rm -f teedy-container-${port} || true"
                        sh "docker run -d -p ${port}:8080 --name teedy-container-${port} $DOCKER_IMAGE:$DOCKER_TAG"
                    }
                }
            }
        }
    }
}
