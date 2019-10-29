node {
  try {
	def mvnHome 
	stage ('prep paths') {
	 echo sh(returnStdout: true, script: 'env')
	 env.JAVA_HOME = tool 'JDK8'
	 echo("Using Java from ${env.JAVA_HOME}")
  	 mvnHome = tool 'Maven_3.2.5'
	 echo("Using Maven from ${mvnHome}")
     env.PATH = "${mvnHome}/bin:${env.JAVA_HOME}/bin:${env.PATH}"
	}
	
	stage ('Checkout from SCM'){
		checkout scm
	}

	stage ('build') {
		def server = Artifactory.server "QA_Artifactory"
        def buildInfo = Artifactory.newBuildInfo()
        buildInfo.env.capture = true
        def rtMaven = Artifactory.newMavenBuild()
        rtMaven.tool = 'Maven_3.2.5' // Tool name from Jenkins configuration
        //rtMaven.opts = "-Denv=dev"
        rtMaven.deployer releaseRepo:'QBIT_Builds', snapshotRepo:'QBIT_Snapshots', server: server
        rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: server

        rtMaven.run pom: 'pom.xml', goals: '-X clean install -U', buildInfo: buildInfo

        buildInfo.retention maxBuilds: 10, maxDays: 7, deleteBuildArtifacts: true
        // Publish build info.
        server.publishBuildInfo buildInfo
	}
	
	
  } catch (e) {
    echo("Build failed. ${e}")
	throw e
  }
}