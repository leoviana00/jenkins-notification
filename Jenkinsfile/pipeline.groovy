pipeline {
  agent any

  environment {
    CHAT_ID = "$(CHAT_ID}"
    MSG_FINISHED = ""
    MSG_SUCCESS = ""
    MSG_FAILURE = ""
  }

  stages {

    stage('Clonar Repositório') {
      steps {
        script {
          try {
            git(
              branch: '**',
              credentialsId: 'JenkinsInGitlab',
              url: 'git@gitlab.mateus:leonardo.viana/jenkinsnotifytelramslack.git'
            )
          } catch(Exception error) {
            notifyBuild(
              "Failed in *${env.STAGE_NAME}*"
            )
          }
        }
      }
    }

    stage("Deploy Develop") {
      when { branch 'develop' }
      steps { 
        echo "Deploying to Develop" 
        sh "false"
      }
    }

    stage("Deploy Production") {
      when { branch 'master' }
      steps {
        echo "Deploying to Master"
        sh "true"
      }
    }
  }
}

def notifyBuild(String message) {
  telegramSend(
    message: """
      ${message}
      Projeto: *${currentBuild.fullDisplayName}*
      Branch: *${env.BRANCH_NAME}*
      ${env.BUILD_URL}
      Result: ${env.result}
    """,
    chatId: "${CHAT_ID}"
  )
}
 