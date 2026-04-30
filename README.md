=========================
📄 GDA README — Lab Module 09
=========================

## Overview
In Lab Module 09, I implemented a full CoAP client into the Gateway Device Application (GDA) using the Eclipse Californium library, wired it into the DeviceDataManager, and verified Discovery, GET, PUT, POST, DELETE, and Observe requests against the GDA's own CoAP server.

## What I Implemented
I implemented/updated the following components:
- CoapClientConnector
- GenericCoapResponseHandler (already existed - verified)
- SensorDataObserverHandler (new)
- SystemPerformanceDataObserverHandler (new)
- DeviceDataManager
- PiotConfig.props (enableCoapClient = True)

## How It Works

### CoapClientConnector
- Uses Eclipse Californium CoapClient connected to coap://localhost:5683
- Reads host and port from PiotConfig.props via ConfigUtil under [Coap.GatewayService]
- initClient() creates the CoapClient instance using CoapConfig and UdpConfig registration
- sendDiscoveryRequest() uses clientConn.discover() to list all registered resources
- sendGetRequest() issues CON or NON GET using useCONs()/useNONs() and clientConn.get()
- sendPutRequest() issues CON or NON PUT with JSON payload
- sendPostRequest() issues CON or NON POST with JSON payload
- sendDeleteRequest() issues CON or NON DELETE
- startObserver() subscribes to observe notifications using SensorDataObserverHandler
- stopObserver() cancels the observe subscription via proactiveCancel()

### SensorDataObserverHandler
- Implements CoapHandler for observe callbacks
- onLoad() receives SensorData JSON and forwards to IDataMessageListener

### SystemPerformanceDataObserverHandler
- Implements CoapHandler for observe callbacks
- onLoad() receives SystemPerformanceData JSON and forwards to IDataMessageListener

### DeviceDataManager
- Updated to create CoapClientConnector if enableCoapClient is True in config
- CoAP client is stateless so no start/stop calls needed

## Testing

    # Terminal 1 - Start GDA
    cd ~/IoT_labs_TELE6530/gda-java-components
    java -jar target/gateway-device-app-0.0.1-jar-with-dependencies.jar

    # Terminal 2 - Run all CoAP client tests
    cd ~/IoT_labs_TELE6530/gda-java-components
    mvn test -Dtest=CoapClientConnectorTest -pl .

## Tests Passed

| Task | Test | Result |
|------|------|--------|
| PIOT-GDA-09-001 | CoapClientConnector created and wired into DeviceDataManager | PASSED |
| PIOT-GDA-09-002 | GenericCoapResponseHandler verified | PASSED |
| PIOT-GDA-09-003 | sendDiscoveryRequest() - testConnectAndDiscover | PASSED |
| PIOT-GDA-09-004 | sendGetRequest() CON - testGetRequestCon | PASSED |
| PIOT-GDA-09-004 | sendGetRequest() NON - testGetRequestNon | PASSED |
| PIOT-GDA-09-005 | sendPutRequest() CON - testPutRequestCon | PASSED |
| PIOT-GDA-09-005 | sendPutRequest() NON - testPutRequestNon | PASSED |
| PIOT-GDA-09-006 | sendPostRequest() CON - testPostRequestCon | PASSED |
| PIOT-GDA-09-006 | sendPostRequest() NON - testPostRequestNon | PASSED |
| PIOT-GDA-09-007 | sendDeleteRequest() CON - testDeleteRequestCon | PASSED |
| PIOT-GDA-09-007 | sendDeleteRequest() NON - testDeleteRequestNon | PASSED |
| PIOT-GDA-09-008 | startObserver()/stopObserver() - testObserve | PASSED |

## Wireshark CoAP Protocol Output (PIOT-STU-09-002)

### CON GET Request
    Constrained Application Protocol, Confirmable, GET, MID:22415
        01.. .... = Version: 1
        ..00 .... = Type: Confirmable (0)
        .... 1000 = Token Length: 8
        Code: GET (1)
        Message ID: 22415
        Token: 1c55fae96cac3103
        Opt Name: #1: Uri-Host: localhost
        Opt Name: #2: Uri-Path: PIOT
        Opt Name: #3: Uri-Path: GatewayDevice
        Opt Name: #4: Uri-Path: MgmtStatusMsg
        [Uri-Path: coap://localhost/PIOT/GatewayDevice/MgmtStatusMsg]

### NON GET Request
    Constrained Application Protocol, Non-Confirmable, GET, MID:22416
        01.. .... = Version: 1
        ..01 .... = Type: Non-Confirmable (1)
        .... 1000 = Token Length: 8
        Code: GET (1)
        Message ID: 22416
        Token: dc0278baf5247355
        Opt Name: #1: Uri-Host: localhost
        Opt Name: #2: Uri-Path: PIOT
        Opt Name: #3: Uri-Path: GatewayDevice
        Opt Name: #4: Uri-Path: MgmtStatusMsg
        [Uri-Path: coap://localhost/PIOT/GatewayDevice/MgmtStatusMsg]

### CON PUT Request
    Constrained Application Protocol, Confirmable, PUT, MID:22411
        01.. .... = Version: 1
        ..00 .... = Type: Confirmable (0)
        .... 1000 = Token Length: 8
        Code: PUT (3)
        Message ID: 22411
        Token: 3440bf4ba76c20fc
        Opt Name: #1: Uri-Host: localhost
        Opt Name: #2: Uri-Path: PIOT
        Opt Name: #3: Uri-Path: GatewayDevice
        Opt Name: #4: Uri-Path: MgmtStatusMsg
        Opt Name: #5: Content-Format: application/json
        End of options marker: 255
        Payload: Payload Content-Format: application/json, Length: 200
        [Uri-Path: coap://localhost/PIOT/GatewayDevice/MgmtStatusMsg]

### NON PUT Request
    Constrained Application Protocol, Non-Confirmable, PUT, MID:22412
        01.. .... = Version: 1
        ..01 .... = Type: Non-Confirmable (1)
        .... 1000 = Token Length: 8
        Code: PUT (3)
        Message ID: 22412
        Token: a0ad57a841587593
        Opt Name: #1: Uri-Host: localhost
        Opt Name: #2: Uri-Path: PIOT
        Opt Name: #3: Uri-Path: GatewayDevice
        Opt Name: #4: Uri-Path: MgmtStatusMsg
        Opt Name: #5: Content-Format: application/json
        End of options marker: 255
        Payload: Payload Content-Format: application/json, Length: 200
        [Uri-Path: coap://localhost/PIOT/GatewayDevice/MgmtStatusMsg]

### CON POST Request
    Constrained Application Protocol, Confirmable, POST, MID:22413
        01.. .... = Version: 1
        ..00 .... = Type: Confirmable (0)
        .... 1000 = Token Length: 8
        Code: POST (2)
        Message ID: 22413
        Token: 64748596d91b1134
        Opt Name: #1: Uri-Host: localhost
        Opt Name: #2: Uri-Path: PIOT
        Opt Name: #3: Uri-Path: GatewayDevice
        Opt Name: #4: Uri-Path: MgmtStatusMsg
        Opt Name: #5: Content-Format: application/json
        End of options marker: 255
        Payload: Payload Content-Format: application/json, Length: 200
        [Uri-Path: coap://localhost/PIOT/GatewayDevice/MgmtStatusMsg]
        [Response In: 446]

### NON POST Request
    Constrained Application Protocol, Non-Confirmable, POST, MID:22414
        01.. .... = Version: 1
        ..01 .... = Type: Non-Confirmable (1)
        .... 1000 = Token Length: 8
        Code: POST (2)
        Message ID: 22414
        Token: 64be636a03d62af1
        Opt Name: #1: Uri-Host: localhost
        Opt Name: #2: Uri-Path: PIOT
        Opt Name: #3: Uri-Path: GatewayDevice
        Opt Name: #4: Uri-Path: MgmtStatusMsg
        Opt Name: #5: Content-Format: application/json
        End of options marker: 255
        Payload: Payload Content-Format: application/json, Length: 200
        [Uri-Path: coap://localhost/PIOT/GatewayDevice/MgmtStatusMsg]

### CON DELETE Request
    Constrained Application Protocol, Confirmable, DELETE, MID:22417
        01.. .... = Version: 1
        ..00 .... = Type: Confirmable (0)
        .... 1000 = Token Length: 8
        Code: DELETE (4)
        Message ID: 22417
        Token: 24fc88c667c8f659
        Opt Name: #1: Uri-Host: localhost
        Opt Name: #2: Uri-Path: PIOT
        Opt Name: #3: Uri-Path: GatewayDevice
        Opt Name: #4: Uri-Path: MgmtStatusCmd
        [Uri-Path: coap://localhost/PIOT/GatewayDevice/MgmtStatusCmd]
        [Response In: 463]

### NON DELETE Request
    Constrained Application Protocol, Non-Confirmable, DELETE, MID:22418
        01.. .... = Version: 1
        ..01 .... = Type: Non-Confirmable (1)
        .... 1000 = Token Length: 8
        Code: DELETE (4)
        Message ID: 22418
        Token: 1060a591fc74d960
        Opt Name: #1: Uri-Host: localhost
        Opt Name: #2: Uri-Path: PIOT
        Opt Name: #3: Uri-Path: GatewayDevice
        Opt Name: #4: Uri-Path: MgmtStatusCmd
        [Uri-Path: coap://localhost/PIOT/GatewayDevice/MgmtStatusCmd]

## Summary
This lab added full CoAP client capability to the GDA using the Eclipse Californium library. The CoapClientConnector was implemented with support for Discovery, GET, PUT, POST, DELETE, and Observe operations, wired into DeviceDataManager alongside the existing MQTT client and CoAP server, and verified using integration tests against the GDA's own CoAP server. All 10 tests passed successfully.
ENDOFFILE
