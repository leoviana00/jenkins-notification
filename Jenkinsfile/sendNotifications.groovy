#!/usr/bin/env groovy

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