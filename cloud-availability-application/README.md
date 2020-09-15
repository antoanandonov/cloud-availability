# Cloud Availability Application

## Build
```
mvn clean install
-DcfUser=${CF_USER}
-DcfPass=${CF_PASS}
``` 

## Run Locally
To start the application, you must pass some variables and execute the following command:
```
mvn spring-boot:run 
-DcfUser=${CF_USER}
-DcfPass=${CF_PASS}
-Dorganization_name=${CF_ORG}
-Dspace_name= ${CF_SPACE}
-Dcf_api=${CF_API_PATH}
-Dport=${TOMCAT_PORT}
``` 