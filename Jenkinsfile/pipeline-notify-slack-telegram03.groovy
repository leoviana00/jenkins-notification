
def LAST_STAGE_NAME = ''
node {
    try {        
        notifyBuild('STARTED')
        
        stage('Clone repositório') { 
        LAST_STAGE_NAME = 'Clone repositório'
        git branch: 'master',
                  credentialsId: 'JenkinsInGitlab',
                  url: 'git@gitlab.mateus:leonardo.viana/deploy-aws-teste.git'
            sh "ls -lat"
        } 
        
        stage ('Build com Maven'){
            LAST_STAGE_NAME = 'Build com Maven'
                sh 'mvn clean package -DskipTests=true'        
        }
      
        stage ('Testes com o Maven'){
            LAST_STAGE_NAME = 'Testes com o Maven'
            sh 'mvnn test'     
        } 
             
  } catch (Exception error) {
    // If there was an exception thrown, the build failed
    currentBuild.result = "FAILED"
    telegramSend(message: "Stage: ${LAST_STAGE_NAME}\n${env.BUILD_URL}consoleText\nErro :"+error,chatId:${chat_id})
    slackSend (message: "Stage: ${LAST_STAGE_NAME}\n${env.BUILD_URL}consoleText\nErro :"+error)
    error "Exception occurred, aborting"
    throw error
  } finally {
    // Success or failure, always send notifications
    notifyBuild(currentBuild.result) 
  }
} 
  def notifyBuild(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus =  buildStatus ?: 'SUCCESSFUL'

  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}\nNome do Job: '${env.JOB_NAME}'\n[#${env.BUILD_NUMBER}]"
  def summary = "${subject}\n(${env.BUILD_URL}consoleText)"
  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'SUCCESSFUL') {
    color = 'GREEN'
    colorCode = '#00FF00'
  } else {
    color = 'RED'
    colorCode = '#FF0000'
  }
   slackSend (color: colorCode, message: subject)
   telegramSend(message: "${buildStatus}\nNome do Job: '${env.JOB_NAME}'\nNúmero do Build: [#${env.BUILD_NUMBER}]",chatId:${chat_id})   
      
}
