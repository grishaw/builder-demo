# builder-demo
This is a demo for remote builder application. The application provides REST API for building maven projects. 

The user builds projects by submitting GET requests of the type http://domain/builder-demo/rest/build/project-id (returns unique build-id). 
The user checks the status of his build requests by submitting GET requests of the type http://domain/builder-demo/rest/status/build-id (returns build status). 

Build status may be one of the following:
 *   0 - ACCEPTED
 *   1 - QUEUED
 *   2 - RUNNING
 *   3 - DONE
 *   4 - FAILED
    
## Configurations
- The location of the Maven installation should be defined under maven.home property in pom.xml.
- The location of the builds outputs should be defined under build.output.dir in builder.properties.
- The mapping between the project ids and their locations should be defined in projects.properties.

###To build the project
mvn clean install

###To run the project
mvn tomcat7:run



