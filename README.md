Prerequisites

Download:
1. scenario-simulator-package-2.3.1.2.zip

To build Docker image call `./build.sh`

Building:
- provide env variable SIMULATOR_HOME pointing to an OC scenario simulator installation
- mvn install

Tests in ROBOT
   * General description and idea
      * OC scenario simulator with remote Robot libraries exposes API via HTTP/XMLRPC, default host/port is localhost/8270. If a different port is needed, it can be set by "com.computaris.robotremote.port" property (e.g. from scenario simulator starting script) 
      * All scenario simulator commands are exposed as robot keywords nearly 1:1
 