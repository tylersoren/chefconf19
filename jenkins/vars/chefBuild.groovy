def call(body) {
  // evaluate the body block, and collect configuration into the object
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  pipeline {
    agent {
        // Enter jenkins agent config here
      }
    environment {
     // Configure environment variables from inputs
     name = "${config.name}"
     s3Bucket = "${config.s3Bucket}"
    }
    stages{
      stage('linting') {
          steps{
                checkout scm
                dir('chef-build') {
                    git(
                            // enter the URL and credentials for connecting to this repo
                            url: '',
                            credentialsId: '',
                            branch: 'master')
                }
                //copy 
                sh "cp -v chef-build/docker/Makefile Makefile"
                sh 'make lint'
              }
          }
        stage('unit test') {
            steps{
                    sh 'make unit'
            }
        }
        stage('integration test') {
            steps{
                    sh 'make kitchen'
            }
        }
        stage('package and upload') {
            // Only run packaging and upload on master branch
            when { 
              branch 'master'
            }
            steps{
                sh 'make package'
                // Extract version number from metadata.  Determine if version has already been packaged
                // Upload if version is new
                sh '''raw_version=$(grep "version" metadata.rb | tr -s \' \' | cut -d \' \' -f2 | sed \'s/[^0-9.]*//g\')
                      VERSION="v${raw_version}"
                      if aws s3 ls s3://$s3Bucket/cookbooks/$name/$VERSION/$name-$VERSION.tar.gz; then
                        echo "Testing passed but version $VERSION already exists!!!  Update version number in metadata to upload"
                        currentBuild.Result = "FAILURE"
                        return
                      else
                        S3_BUCKET=${s3Bucket} COOKBOOK_NAME=${name} VERSION=${VERSION} make upload
                      fi'''
                }
            }
        
      }
    post {
      always {
          // cleanup the workspace. Destroy Test kitchen instance
          sh 'chef exec kitchen destroy'
      }
    }
  }
}
