pipeline { 
    agent any 

    environment { 
        // 👉 你需要在 Jenkins 凭据中添加，并填写这里的 ID
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub_credentials') 

        // 👉 你需要替换为你自己的 DockerHub 仓库名（格式为 用户名/仓库名）
        DOCKER_IMAGE = 'gongjunzhe/teedy' 

        DOCKER_TAG = "${env.BUILD_NUMBER}" 
    } 

    tools {
        // 👉 如果你在 Global Tool Configuration 中配置了 Maven 名称不是 'M3'，请改成你设置的名字
        maven 'M3' 
    }

    stages { 
        stage('Build') { 
            steps { 
                // 👉 你需要填写自己的 GitHub 仓库地址
                checkout scmGit( 
                    branches: [[name: '*/master']],  
                    extensions: [],  
                    userRemoteConfigs: [[url: 'https://github.com/Gongjunzhe12210401/Teedy.git']] 
                ) 

                // Maven 构建 WAR 包
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

        stage('Push Docker Image') { 
            steps { 
                script { 
                    docker.withRegistry('https://registry-1.docker.io/v2/', DOCKER_HUB_CREDENTIALS) { 
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push() 
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push('latest') 
                    } 
                } 
            } 
        } 

        stage('Run 3 Containers') { 
            steps { 
                script {
                    // 运行三个容器，分别监听 8082/8083/8084
                    ['8082', '8083', '8084'].each { port ->
                        def containerName = "teedy-container-${port}"
                        sh "docker rm -f ${containerName} || true"
                        sh "docker run -d -p ${port}:8080 --name ${containerName} ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    }

                    // 查看容器运行状态
                    sh 'docker ps --filter "name=teedy-container"'
                }
            } 
        } 
    } 
}
