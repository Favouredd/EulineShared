def call(String repoUrl){
  pipeline {
	agent any
	tools {maven 'Maven'}
	stages{
		stage('git-clone'){
			steps{
			     checkout([$class: 'GitSCM', branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'git-check', url: 'https://github.com/Favouredd/EulineShared.git']]])	
			}
		}
		stage('etech-hello'){
			steps{
				sh 'git version'
			}
		}
       stage('Build Artifact - Maven') {
          steps {
            sh "mvn clean package -DskipTests=true"
            archive 'target/*.jar'
            }
       }
       stage('Unit Tests - JUnit and JaCoCo') {
          steps {
             sh 'mvn test'
             }
             post {
               always {
                 junit 'target/surefire-reports/*.xml'
                 jacoco execPattern: 'target/jacoco.exec'
        }
     }
   }       
      stage('Mutation Tests - PIT') {
         steps {
           sh "mvn org.pitest:pitest-maven:mutationCoverage"
      }
          post {
            always {
          pitmutation mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
        }
      }
    }
   stage('CodeQuality-SAST') {
      steps {
         sh 'mvn clean verify sonar:sonar \
  -Dsonar.projectKey=devsecops-spring2-app \
  -Dsonar.host.url=http://azuredemo1.eastus.cloudapp.azure.com:9000 \
  -Dsonar.login=674c734985e4d9a6a99bb7d3ee5494e93b09ccc2'
    }     
  }
 }
}
		
		
		
		
		
		
		
