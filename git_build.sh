#!/bin/bash
 
module=$1
function setRstate {
        revision="R1A"
	
	if git for-each-ref --sort=creatordate --format '%(refname)' refs/tags | grep $revision; then
        	rstate=`git for-each-ref --sort=creatordate --format '%(refname)' refs/tags | grep ${revision} | tail -1 | sed s/refs['/']tags['/']// | perl -nle 'sub nxt{$_=shift;$l=length$_;sprintf"%0${l}d",++$_}print $1.nxt($2) if/^(.*?)(\d+$)/';`
        else
		ammendment_level=1
	        rstate=$revision$ammendment_level
	fi
		echo "Building rstate:$rstate"
}
function Arm104nexusDeploy {
	RepoURL=https://arm1s11-eiffel013.eiffel.gic.ericsson.se:8443/nexus/content/repositories/assure-releases 
	GroupId=com.ericsson.eniq.netanbo
	ArtifactId=$module
	zipName=NetAnBO
	
	echo "****"	
	echo "Deploying the zip /$zipName-21.1-dist.zip as ${ArtifactId}${rstate}.zip to Nexus...."
        mv target/$zipName-21.1-dist.zip target/${ArtifactId}.zip
	echo "****"	
  	mvn -B deploy:deploy-file \
	        	-Durl=${RepoURL} \
		        -DrepositoryId=assure-releases \
		        -DgroupId=${GroupId} \
		        -Dversion=${rstate} \
		        -DartifactId=${ArtifactId} \
		        -Dfile=target/${ArtifactId}.zip
		      
}
function Arm104nexusDeployTemp {
	RepoURL=https://arm1s11-eiffel013.eiffel.gic.ericsson.se:8443/nexus/content/repositories/assure-releases
	GroupId=com.ericsson.eniq.netanbo.LatestRelease
	ArtifactId=$module
	zipName=NetAnBO
	
	echo "****"	
	echo "Deploying the zip /$zipName-21.1-dist.zip as ${ArtifactId}${rstate}.zip to Nexus...."
        mv target/$zipName-21.1-dist.zip target/${ArtifactId}.zip
	echo "****"	
  	mvn -B deploy:deploy-file \
	        	-Durl=${RepoURL} \
		        -DrepositoryId=assure-releases \
		        -DgroupId=${GroupId} \
		        -Dversion=${rstate} \
		        -DartifactId=${ArtifactId} \
		        -Dfile=target/${ArtifactId}.zip
		#mv target/${ArtifactId}.zip/* target/	      
}
setRstate
#add maven command here
mvn package
#comment
#nexus deploy
Arm104nexusDeploy
Arm104nexusDeployTemp
rsp=$?
if [ $rsp == 0 ]; then
  git tag $rstate
  git push origin $rstate 
fi
exit $rsp