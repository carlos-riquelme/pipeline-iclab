/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
    pipeline {
        agent any
            environment {
                NEXUS_USER = credentials('NEXUS-USER')
                NEXUS_PASSWORD = credentials('NEXUS-PASS')
            }
            parameters {
                choice choices: ['maven', 'gradle'], description: 'Seleccione una herramienta para preceder a compilar', name: 'compileTool'
                text description: 'Enviar los stages separados por ";"... Vacío si necesita todos los stages', name: 'stages'
                gitParameter branchFilter: 'origin/(.*)', defaultValue: 'develop', name: 'BRANCH', type: 'PT_BRANCH'
            }
            stages {
                stage("Pipeline"){
                    when {
                        branch 'feature-library'
                    }
                    steps {
                        script{
                            sh "env"
                            env.TAREA = ""
                            if(params.compileTool == 'maven'){
                                maven.call(params.stages);
                            }else{
                                gradle.call(params.stages)
                            }
                        }
                    }
                    post{
                        success{
                            slackSend color: 'good', message: "[Carlos  Riquelme] [${JOB_NAME}] [${BUILD_TAG}] Ejecucion Exitosa", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: '09bb43a7-7b85-4368-8bcb-b6cc51f1010e'
                        }
                        failure{
                            echo 'La etapa falló por alguna razón. ¡A revisar los logs! '
                            slackSend color: 'danger', message: "[Carlos Riquelme] [${env.JOB_NAME}] [${BUILD_TAG}] Ejecucion fallida en stage [${env.TAREA}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: '09bb43a7-7b85-4368-8bcb-b6cc51f1010e'
                        }
                    }
                }
            }
    }
}
return this;