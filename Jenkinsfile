pipeline {
  agent any

  environment {
    // 替换成你 Jenkins 中添加的 Docker Hub 凭据 ID
    DOCKER_HUB_CREDENTIALS = credentials('dockerhub_credentials')

    // 替换成你 Docker Hub 上的用户名（如 wangduane）
    DOCKER_IMAGE = '12210401/teedy'

    // 自动使用构建编号作为标签
    DOCKER_TAG = "${env.BUILD_NUMBER}"
  }

  stages {
    stage('Checkout Code') {
      steps {
        checkout scm
      }
    }

    stage('Build with Maven') {
      steps {
        sh 'mvn -B -DskipTests clean package'
      }
    }

    stage('Build Docker Image') {
      steps {
        script {
          docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
        }
      }
    }

    stage('Push Docker Image to Hub') {
      steps {
        script {
          docker.withRegistry('https://index.docker.io/v1/', DOCKER_HUB_CREDENTIALS) {
            docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
            docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push('latest')
          }
        }
      }
    }

    stage('Run 3 Containers') {
      steps {
        script {
          ['8082', '8083', '8084'].each { port ->
            sh "docker stop teedy-${port} || true"
            sh "docker rm teedy-${port} || true"
            sh "docker run -d -p ${port}:8080 --name teedy-${port} ${DOCKER_IMAGE}:${DOCKER_TAG}"
          }
        }
      }
    }
  }
}
