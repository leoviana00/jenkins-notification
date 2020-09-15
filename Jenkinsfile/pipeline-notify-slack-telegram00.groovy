pipeline{
   agent any

   environment {
      ARTIFACT = "pipeline-teste"
      VERSION = sh(script: 'echo "v."$((${BUILD_NUMBER}%5))', returnStdout: true).trim()
      BRANCH_NAME = "master"
   }
  
   stages {
       
       stage('Iniciando o Job') {
            steps {
                script{
                    withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                    string(credentialsId: 'telegramChatId', variable: 'CHAT_ID')]) {
                    sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d parse_mode="HTML" -d text="<b>Project</b> : contabil-jenkins-telegram \
                      <b>Job</b>: Iniciado \
                      <b>Status </b> : OK "'
        
                }
            }
        }
    } 
      stage('Clonando o repositório ') {
        
         steps {
            git branch: 'master',
                  credentialsId: 'JenkinsInGitlab',
                  url: 'git@gitlab.mateus:leonardo.viana/deploy-aws-teste.git'

            sh "ls -lat"
         }
      }
      
      stage('Clone repositório Notify') {
            steps {
                script{
                    withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                    string(credentialsId: 'telegramChatId', variable: 'CHAT_ID')]) {
                    sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d parse_mode="HTML" -d text="<b>Cone do repositório</b>: Finalizado \
                      <b>Status </b> : OK "'
        
                }
            }
         }
      } 

       stage ('Build com Maven'){
            steps{
                sh 'mvn clean package -DskipTests=true'
            }
        }
        
        stage('Build com Maven Notify') {
            steps {
                script{
                    withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                    string(credentialsId: 'telegramChatId', variable: 'CHAT_ID')]) {
                    sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d parse_mode="HTML" -d text="<b>Build com Maven</b>: Finalizado \
                      <b>Status </b> : OK "'
        
                }
            }
         }
      } 

        stage ('Testes com o Maven'){
            steps{
                sh 'mvn test'
            }
        }
        
        stage('Teste com o Maven Notify') {
            steps {
                script{
                    withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                    string(credentialsId: 'telegramChatId', variable: 'CHAT_ID')]) {
                    sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d parse_mode="HTML" -d text="<b>Testes Maven</b>: Finalizado \
                      <b>Status </b> : Passou "'
        
                }
            }
         }
      } 
        
        stage('Push Notification') {
            steps {
                script{
                    withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                    string(credentialsId: 'telegramChatId', variable: 'CHAT_ID')]) {
                    sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d parse_mode="HTML" -d text="<b>Branch</b>: master \
                      <b>Build </b> : Completo \
                      <b>Test suite</b> = Passed"'
    
                    }
                }
            }
        }
    }
 }