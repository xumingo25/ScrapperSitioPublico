pipeline {
    agent any

    environment {
        // --- Email Configuration ---
        // Replace these values with your actual SMTP server details.
        SMTP_HOST = 'smtp.example.com'
        SMTP_PORT = '587'
        SMTP_USER = 'your-email@example.com'
        RECIPIENT_EMAIL = 'recipient@example.com'

        // This line demonstrates how to use Jenkins credentials for the password.
        // 1. In Jenkins, go to "Manage Jenkins" -> "Credentials".
        // 2. Add a new "Secret text" credential.
        // 3. Set the "ID" to "smtp-password" (or your preferred ID).
        // 4. Set the "Secret" to your email account password or an app password.
        SMTP_PASSWORD = credentials('smtp-password')
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
                        bat 'gradlew.bat test'
                    }
                }
            }
        }
    }
}
