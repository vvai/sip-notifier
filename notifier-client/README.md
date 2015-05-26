# Notifier (client)

Tool for notifying sip client about events

## Usage

required environment variable:

    LD_LIBRARY_PATH=/opt/ibm/lotus/notes
  
Also this project depends from `Notes.jar` which you cannot take in any maven repository. You can install it in your 
local repository:

    mvn install:install-file -Dfile="/opt/ibm/lotus/notes/jvm/lib/ext/Notes.jar" -DgroupId=lotus-notes
         -DartifactId=notes -Dversion=6.1 -Dpackaging=jar
         
or uncomment in `pom.xml` line `SystemPath`

    <systemPath>/opt/ibm/lotus/notes/jvm/lib/ext/Notes.jar</systemPath>
    
## Running


1. You can run client from source code:

        mvn spring-boot:run

    
2. You can build executable jar file and run it. At first you must build jar file:

        mvn package

    then in folder with jar file create `app.properties` file (or take example from this git repository) and change
    property `com.ericpol.notifier.client.lotus_pass`. Then run jar file:

        java -jar notifier-client-1.0.0-SNAPSHOT.jar

    log file `notifier.log` will be in folder with jar.
