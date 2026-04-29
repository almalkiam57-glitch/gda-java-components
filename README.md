=========================
📄 GDA README — Lab Module 08
=========================

## Overview
In Lab Module 08, I implemented a full CoAP server into the Gateway Device Application (GDA) using the Eclipse Californium library, wired it into the DeviceDataManager, and verified resource discovery and GET/PUT requests using the Californium CoAP client.

## What I Implemented
I implemented/updated the following components:
- CoapServerGateway
- GenericCoapResourceHandler
- GetActuatorCommandResourceHandler
- UpdateSystemPerformanceResourceHandler
- UpdateTelemetryResourceHandler
- DeviceDataManager

## How It Works

### CoapServerGateway
- Uses Eclipse Californium CoapServer on port 5683
- initServer() creates the CoapServer instance and calls initDefaultResources()
- initDefaultResources() registers three key handlers: GetActuatorCommandResourceHandler, UpdateTelemetryResourceHandler, and UpdateSystemPerformanceResourceHandler
- createAndAddResourceChain() builds the resource tree from ResourceNameEnum path names and adds them to the server
- addResource() allows external callers to register additional resource handlers
- startServer() starts the server and adds a MessageTracer to each endpoint
- stopServer() cleanly shuts down the server

### GetActuatorCommandResourceHandler
- Extends CoapResource and implements IActuatorDataListener
- Observable resource - notifies connected clients when actuator data changes via super.changed()
- handleGET() returns latest ActuatorData as JSON
- onActuatorDataUpdate() updates internal data and triggers observer notifications

### UpdateSystemPerformanceResourceHandler
- Extends CoapResource
- handlePUT() parses incoming SystemPerformanceData JSON and forwards to DeviceDataManager
- Responds with 2.04 CHANGED on success or 4.00 BAD_REQUEST on parse failure

### UpdateTelemetryResourceHandler
- Extends CoapResource
- handlePUT() parses incoming SensorData JSON and forwards to DeviceDataManager
- Responds with 2.04 CHANGED on success or 4.00 BAD_REQUEST on parse failure

### DeviceDataManager
- Updated initManager() to create CoapServerGateway if enableCoapServer is true
- startManager() calls coapServer.startServer()
- stopManager() calls coapServer.stopServer()
- setActuatorDataListener() registers an IActuatorDataListener for actuator callbacks
- handleIncomingDataAnalysis() forwards actuator data to the registered listener

## Testing

    # Terminal 1 - Start the GDA
    cd ~/IoT_labs_TELE6530/gda-java-components
    java -jar target/gateway-device-app-0.0.1-jar-with-dependencies.jar

    # Terminal 2 - CoAP Discovery
    cd ~/IoT_labs_TELE6530/californium.tools/cf-client/target
    java -jar cf-client-4.0.0-SNAPSHOT.jar -m GET coap://localhost:5683/.well-known/core

    # Terminal 2 - CoAP GET ActuatorCmd
    java -jar cf-client-4.0.0-SNAPSHOT.jar -m GET coap://localhost:5683/PIOT/ConstrainedDevice/ActuatorCmd

    # Terminal 2 - CoAP GET SystemPerfMsg
    java -jar cf-client-4.0.0-SNAPSHOT.jar -m GET coap://localhost:5683/PIOT/ConstrainedDevice/SystemPerfMsg

    # Terminal 2 - CoAP PUT SystemPerfMsg
    java -jar cf-client-4.0.0-SNAPSHOT.jar -m PUT coap://localhost:5683/PIOT/ConstrainedDevice/SystemPerfMsg --payload '{"cpuUtil":0.0,"diskUtil":0.0,"memUtil":0.0,"name":"SystemPerfMsg","timeStamp":"2026-04-29T00:00:00Z"}' --json

    # Terminal 2 - CoAP PUT SensorMsg
    java -jar cf-client-4.0.0-SNAPSHOT.jar -m PUT coap://localhost:5683/PIOT/ConstrainedDevice/SensorMsg --payload '{"name":"TempSensor","typeID":1013,"value":22.5,"timeStamp":"2026-04-29T00:00:00Z"}' --json

## Tests Passed

| Task | Test | Result |
|------|------|--------|
| PIOT-GDA-08-001 | CoAP server starts and stops via DeviceDataManager | PASSED |
| PIOT-GDA-08-002 | CoAP Discovery - all resources listed | PASSED |
| PIOT-GDA-08-002 | CoAP GET SystemPerfMsg - 2.05 CONTENT | PASSED |
| PIOT-GDA-08-002 | CoAP PUT SystemPerfMsg - 2.04 CHANGED | PASSED |
| PIOT-GDA-08-002 | CoAP PUT SensorMsg - 2.04 CHANGED | PASSED |
| PIOT-GDA-08-003 | GET ActuatorCmd - 2.05 CONTENT + JSON | PASSED |
| PIOT-GDA-08-004 | initDefaultResources() and addResource() updated | PASSED |

## CoAP Discovery Log Output (PIOT-GDA-08-002)

    ==[ CoAP Request ]=============================================
    MID    : 17658
    Token  : 408022526D6AE5D4
    Type   : CON
    Method : 0.01 - GET
    Options: {"Uri-Host":"localhost", "Uri-Path":[".well-known","core"]}
    Payload: 0 Bytes
    ===============================================================
    >>> UDP(localhost/127.0.0.1:5683)
    Time elapsed (ms): 85
    Discovered resources:
    </PIOT>
    </PIOT/ConstrainedDevice>
    </PIOT/ConstrainedDevice/ActuatorCmd>
            obs:
    </PIOT/ConstrainedDevice/SensorMsg>
    </PIOT/ConstrainedDevice/SystemPerfMsg>

## CoAP GET Log Output (PIOT-GDA-08-003)

    ==[ CoAP Request ]=============================================
    MID    : 25746
    Token  : AC3CEB041AE04793
    Type   : CON
    Method : 0.01 - GET
    Options: {"Uri-Host":"localhost", "Uri-Path":["PIOT","ConstrainedDevice","ActuatorCmd"]}
    Payload: 0 Bytes
    ===============================================================
    >>> UDP(localhost/127.0.0.1:5683)
    Time elapsed (ms): 114
    ==[ CoAP Response ]============================================
    MID    : 6496
    Token  : AC3CEB041AE04793
    Type   : CON
    Status : 2.05 - CONTENT
    Options: {"Content-Format":"application/json"}
    RTT    : 114 ms
    Payload: 258 Bytes
    {"command":0,"value":0.0,"isResponse":false,"name":"Not Set","timeStamp":"2026-04-29T20:34:20.455543996Z","statusCode":0,"typeID":0,"locationID":"gatewaydevice001"}
    ===============================================================

## CoAP PUT Log Output (PIOT-GDA-08-002)

    ==[ CoAP Request ]=============================================
    MID    : 23390
    Token  : 64978C461DEB6BB8
    Type   : CON
    Method : 0.03 - PUT
    Options: {"Uri-Host":"localhost", "Uri-Path":["PIOT","ConstrainedDevice","SystemPerfMsg"], "Content-Format":"application/json"}
    Payload: 102 Bytes
    {"cpuUtil":0.0,"diskUtil":0.0,"memUtil":0.0,"name":"SystemPerfMsg","timeStamp":"2026-04-29T00:00:00Z"}
    ===============================================================
    >>> UDP(localhost/127.0.0.1:5683)
    Time elapsed (ms): 36
    ==[ CoAP Response ]============================================
    MID    : 29834
    Token  : 64978C461DEB6BB8
    Type   : CON
    Status : 2.04 - CHANGED
    Options: {"Content-Format":"text/plain"}
    RTT    : 36 ms
    Update system perf data request handled: SystemPerfMsg
    ===============================================================

## Summary
This lab added full CoAP server capability to the GDA using the Eclipse Californium library. The CoapServerGateway was implemented with resource handlers for actuator commands, sensor telemetry, and system performance data, wired into DeviceDataManager alongside the existing MQTT client, and verified using the Californium CoAP client for discovery, GET, and PUT requests. The ActuatorCmd resource is observable, allowing connected clients to receive automatic updates when actuator data changes
