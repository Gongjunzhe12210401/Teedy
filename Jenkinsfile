// pipeline {
//     agent any

//     environment {
//         DOCKER_IMAGE = 'gongjunzhe/teedy'
//         DOCKER_TAG = "${env.BUILD_NUMBER}"
//     }

//     tools {
//         maven 'M3'
//     }

//     stages {
//         stage('Build') {
//             steps {
//                 checkout scm
//                 sh 'mvn -B -DskipTests clean package'
//             }
//         }

//         stage('Login to DockerHub (manual)') {
//             steps {
//                 withCredentials([usernamePassword(credentialsId: 'dockerhub_credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_TOKEN')]) {
//                     sh '''
//                     echo "$DOCKER_TOKEN" | docker login -u "$DOCKER_USER" --password-stdin
//                     '''
//                 }
//             }
//         }

//         stage('Build Image') {
//             steps {
//                 sh 'docker build -t $DOCKER_IMAGE:$DOCKER_TAG .'
//             }
//         }

//         stage('Push Image') {
//             steps {
//                 sh '''
//                 docker push $DOCKER_IMAGE:$DOCKER_TAG
//                 docker tag $DOCKER_IMAGE:$DOCKER_TAG $DOCKER_IMAGE:latest
//                 docker push $DOCKER_IMAGE:latest
//                 '''
//             }
//         }

//         stage('Run Containers') {
//             steps {
//                 script {
//                     ['8082', '8083', '8084'].each { port ->
//                         sh "docker rm -f teedy-container-${port} || true"
//                         sh "docker run -d -p ${port}:8080 --name teedy-container-${port} $DOCKER_IMAGE:$DOCKER_TAG"
//                     }
//                 }
//             }
//         }
//     }
// }

pipeline {
    agent any

    environment {
        DEPLOYMENT_NAME = "teedy-deploy"
        CONTAINER_NAME = "teedy"  // 容器名应与你 image 的 Dockerfile 保持一致
        IMAGE_NAME = "your-dockerhub-id/teedy:tag"
    }

    stages {
        stage('Start Minikube') {
            steps {
                sh '''
                    if ! minikube status | grep -q "Running"; then
                        minikube start
                    else
                        echo "Minikube is already running"
                    fi
                '''
            }
        }

        stage('Load Image') {
            steps {
                sh '''
                    docker pull ${IMAGE_NAME}
                    minikube image load ${IMAGE_NAME}
                '''
            }
        }

        stage('Set Image') {
            steps {
                sh '''
                    kubectl set image deployment/${DEPLOYMENT_NAME} ${CONTAINER_NAME}=${IMAGE_NAME} || \
                    kubectl create deployment ${DEPLOYMENT_NAME} --image=${IMAGE_NAME}
                '''
            }
        }

        stage('Expose Service') {
            steps {
                sh '''
                    kubectl expose deployment ${DEPLOYMENT_NAME} --type=LoadBalancer --port=8080 || echo "Already exposed"
                '''
            }
        }

        stage('Verify') {
            steps {
                sh '''
                    kubectl rollout status deployment/${DEPLOYMENT_NAME}
                    kubectl get pods
                    kubectl get services
                '''
            }
        }
    }
}

