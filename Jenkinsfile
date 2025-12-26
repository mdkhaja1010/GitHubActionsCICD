pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                bat 'java -version'
                bat 'mvn -v'
                bat 'mvn clean test'
            }
        }
    }

    post {
        always {
            echo 'Saving test reports'
            archiveArtifacts artifacts: 'reports/**'
        }
    }
}