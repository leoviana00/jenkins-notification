node {
    try {
        
        notifyBuild('STARTED')
        
        try{
            
        //Stage 1 - Clone do repositorio
        stage('Clone repositório') { 
        git branch: 'master',
                  credentialsId: 'JenkinsInGitlab',
                  url: 'git@gitlab.mateus:leonardo.viana/deploy-aws-teste.git'

            sh "ls -lat"
        } 
        }
        
        catch(Exception error) {
        // Enviar notificação em caso de falha
        telegramSend(message: "${env.BUILD_URL}consoleText\nErro na stage: Clonar repositório :"+error ,chatId:-493170702)
        slackSend (message: "${env.BUILD_URL}consoleText\nErro na stage: Clonar repositório :"+error)
        error "Exception occurred, aborting"
        }
        // Enviar notificação de status ok
        telegramSend(message: "Stage: Clonar repositório\nStatus:OK" ,chatId:-493170702)
        slackSend (message: "Stage: Clonar repositório\nStatus:OK")
        
        try{
        //Stage 2 - Build com o maven
        stage ('Build com Maven'){
                sh 'mvn clean package -DskipTests=true'
        }
        }
        
        catch(Exception error) {
        // Enviar notificação em caso de falha
        telegramSend(message: "${env.BUILD_URL}consoleText\nErro na stage: Build com Maven :"+error,chatId:-493170702)
        slackSend (message: "${env.BUILD_URL}consoleText\nErro na stage:  Build com Maven :"+error)
        error "Exception occurred, aborting"
        }
        // Enviar notificação ede status ok
        telegramSend(message: "Stage: Build com o Maven\nStatus:OK" ,chatId:-493170702)
        slackSend (message: "Stage: Build com o Maven\nStatus:OK")
        
        try{
        //STage 3 - Teste com o Maven
        stage ('Testes com o Maven'){
                sh 'mvn test'
        }
        }
        
        catch(Exception error) {
        // Enviar notificação em caso de falha
        telegramSend(message: "${env.BUILD_URL}consoleText\nErro na stage: Testes com o Maven :"+error,chatId:-493170702)
        slackSend (message: "${env.BUILD_URL}consoleText\nErro na stage:  Testes com o Maven :"+error)
        error "Exception occurred, aborting"
        }
        // Enviar notificação ede status ok
        telegramSend(message: "Stage: Teste com o Maven\nStatus:OK" ,chatId:-493170702)
        slackSend (message: "Stage: Teste com o maven\nStatus:OK")

  } catch (e) {
    // Se houver uma excessão o build falha
    currentBuild.result = "FAILED"
    throw e
  } finally {
    // Sucesso ou falha, sempre enviar notificações
    notifyBuild(currentBuild.result)
    
  }
}

  def notifyBuild(String buildStatus = 'STARTED') {
  // Status de Build de nulo significa sucesso
  buildStatus =  buildStatus ?: 'SUCCESSFUL'

  // Valores padrã
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}\nNome do Job: '${env.JOB_NAME}' [#${env.BUILD_NUMBER}]"
  def summary = "${subject}\n(${env.BUILD_URL}consoleText)"
  // Substitua os valores padrão com base no status da compilação
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
  // Enviar notificação
  slackSend (color: colorCode, message: subject)
  telegramSend(message: "${buildStatus}\nNome do Job: '${env.JOB_NAME}'\nNúmero do Build: [#${env.BUILD_NUMBER}]",chatId:-493170702)
  
}
