pipeline {
    agent any
    environment {
            JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
        }

    stages {
        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh './gradlew build'
                    } else {
                        bat 'gradlew.bat build'
                    }
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh './gradlew test'
                    } else {
                        bat 'gradlew.bat clean test'
                    }
                }
            }
        }
    }
}
