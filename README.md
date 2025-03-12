GithubAplication is a simple application where you can check by endpoint public repositories of specific customer.


GET http://localhost:8080/api/github/repos/{{username}} 

In response you will acquired GeneralResponse
which have three fields in positive cases chould be returned status and object but in negative cases you will receive status and message why request failed.

To run application you need:
JDK 23, 
internet connection.

To run by cmd please:
1. Install Java, maven and build jar by mvn clean install
2. Run command: "java -jar Application.jar"
3. Use for example browser by pasting http://localhost:8080/api/github/repos/octocat in url

