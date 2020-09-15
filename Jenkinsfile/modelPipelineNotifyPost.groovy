pipeline {
    stages { ... }
    post {
       // only triggered when blue or green sign
       success {
           slackSend ...
       }
       // triggered when red sign
       failure {
           slackSend ...
       }
       // trigger every-works
       always {
           slackSend ...
       }
    }
}