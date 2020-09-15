node {
    try {
        
        notifyBuild('STARTED')
        
       
        stage('Clone repositório') { 
        git branch: 'master',
                  credentialsId: 'JenkinsInGitlab',
                  url: 'git@gitlab.mateus:leonardo.viana/deploy-aws-teste.git'

            sh "ls -lat"
        } 
       
        stage ('Build com Maven'){
                sh 'mvn clean package -DskipTests=true'
        }
      
        stage ('Testes com o Maven'){
                sh 'mvn test'
        }
       
  } catch (Exception error) {
    // If there was an exception thrown, the build failed
    currentBuild.result = "FAILED"
    telegramSend(message: "Erro :"+error,chatId:chatId:${chattIdTelegram})
    slackSend (message: "Erro :"+error)
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
  def subject = "${buildStatus}\nNome do Job: '${env.JOB_NAME}' [#${env.BUILD_NUMBER}]"
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
  
   slackSend (color: colorCode, message: summary)
   telegramSend(message: "${buildStatus}\nNome do Job: '${env.JOB_NAME}'\nNúmero do Build: [#${env.BUILD_NUMBER}]\n(${env.BUILD_URL}consoleText)",chatId:${chattIdTelegram})
     
}
