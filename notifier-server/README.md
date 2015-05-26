# Notifier (server)

Tool for notifying sip client about events

## Usage

All configuration properties are into `application.properties` file

## Running


1. You can run client from source code:

        mvn spring-boot:run

    
2. You can build executable jar file and run it. At first you must build jar file:

        mvn package

    Then run jar file:

        java -jar notifier-server-1.0.0-SNAPSHOT.jar

    log file `notifier-server.log` will be in folder with jar.
