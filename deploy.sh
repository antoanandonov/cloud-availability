#!/bin/bash

MANIFEST='manifest.yml'
PROJECT_NAME='cloud-availability'
SRV_MANIFEST="./${PROJECT_NAME}-service/${MANIFEST}"
WTC_MANIFEST="./${PROJECT_NAME}-watcher/${MANIFEST}"
APP_MANIFEST="./${PROJECT_NAME}-application/${MANIFEST}"

function checkExitCode() {
  if [ $1 -ne 0 ]; then
    echo "Process exited with $1"
    exit 1
  fi
}

function waitUntilFinish() {
  local pid=$1
  echo
  echo -n 'Processing'
  while ps -p "${pid}" >/dev/null; do
    printf "."
    sleep 1
  done
  echo
}

function push() {
  cf login --sso -a $1 -o $2 -s $3
  cf push -f $4 >/dev/null 2>&1 & waitUntilFinish $!
}

function deploy() {
  local app_name='cloud-availability-application'

  push $1 $2 $3 $4 # && sleep 10s

  # Provision CloudFoundry CLI
  cf enable-ssh "${app_name}"
  cf ssh -c 'curl -L "https://packages.cloudfoundry.org/stable?release=linux64-binary&source=github" | tar -zx && echo alias cf=${PWD}/cf >> ~/.bashrc && source ~/.bashrc' "${app_name}"
  cf restage "${app_name}" >/dev/null 2>&1 & waitUntilFinish $!
}

mvn clean install -DskipTests=true
checkExitCode $?

push 'https://api.cf.eu10.hana.ondemand.com' 'andonov_715f2354trial' 'dev' ${SRV_MANIFEST} && P1_EXIT_CODE=$?
push 'https://api.cf.eu10.hana.ondemand.com' 'andonov_897bee0atrial' 'dev' ${WTC_MANIFEST} && P2_EXIT_CODE=$?
push 'https://api.cf.eu10.hana.ondemand.com' 'andonov_6cb3f325trial' 'dev' ${APP_MANIFEST} && P3_EXIT_CODE=$?
push 'https://api.cf.us10.hana.ondemand.com' 'andonov_8c78b5abtrial' 'dev' ${APP_MANIFEST} && P4_EXIT_CODE=$?

checkExitCode $((P1_EXIT_CODE + P2_EXIT_CODE + P3_EXIT_CODE + P4_EXIT_CODE))
echo 'Deploy completed! 😎'
exit 0
