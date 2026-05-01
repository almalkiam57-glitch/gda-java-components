=========================
📄 GDA README — Lab Module 10
=========================

## Overview
In Lab Module 10, I implemented intelligent edge messaging using MQTT and CoAP to handle various messaging scenarios between the GDA and CDA. This includes TLS-encrypted MQTT connections, asynchronous MQTT client with inner message listener classes, humidity threshold analysis, actuation event publishing to the CDA, and performance testing of both MQTT and CoAP protocols.

## What I Implemented
I implemented/updated the following components:
- MqttClientConnector (TLS support, MqttAsyncClient, inner listener classes)
- DeviceDataManager (humidity threshold analysis, upstream transmission)
- PiotConfig.props (enableCrypt = True, certFile, securePort, humidity thresholds)
- MqttClientPerformanceTest (INT-10-001)
- CoapClientPerformanceTest (INT-10-002)
- DeviceDataManagerSimpleCdaActuationTest (GDA-10-003)

## How It Works

### MqttClientConnector (GDA-10-001, GDA-10-002)
- Added TLS support using SimpleCertManagementUtil with server.crt certificate
- initClientParameters() loads all config including enableCrypt, pemFileName, useAsyncClient
- initSecureConnectionParameters() sets up SSLSocketFactory and switches to port 8883
- initCredentialConnectionParameters() loads credentials from credFile if present
- Switched from synchronous MqttClient to asynchronous MqttAsyncClient
- connectComplete() callback subscribes to CDA topics with dedicated listener classes:
  - ActuatorResponseMessageListener: handles ActuatorData response messages
  - SensorDataMessageListener: handles SensorData messages from CDA
  - SystemPerformanceDataMessageListener: handles SystemPerformanceData messages
- All subscriptions moved from DeviceDataManager.startManager() to connectComplete()

### DeviceDataManager (GDA-10-003)
- Added humidity threshold class-scoped variables:
  - latestHumidifierActuatorData, latestHumiditySensorData, latestHumiditySensorTimeStamp
  - handleHumidityChangeOnDevice, triggerHumidifierFloor, triggerHumidifierCeiling
  - nominalHumiditySetting, humidityMaxTimePastThreshold
- handleSensorMessage() invokes handleIncomingDataAnalysis() and handleUpstreamTransmission()
- handleIncomingDataAnalysis() routes humidity data to handleHumiditySensorAnalysis()
- handleHumiditySensorAnalysis() tracks threshold crossings over time:
  - If humidity < floor for > humidityMaxTimePastThreshold seconds: send HVAC ON to CDA
  - If humidity returns to nominal: send HVAC OFF to CDA
- sendActuatorCommandtoCda() publishes ActuatorData to CDA_ACTUATOR_CMD_RESOURCE via MQTT
- handleUpstreamTransmission() logs TODO for future cloud service integration

### TLS Configuration (CFG-10-001)
- Generated CA and server certificates using openssl
- Configured Mosquitto with TLS on port 8883
- Copied server.crt to gda-java-components/certs/
- Updated PiotConfig.props with absolute cert path and enableCrypt = True

## Testing

    # Terminal 1 - Start Mosquitto (TLS enabled)
    sudo mosquitto -c /etc/mosquitto/mosquitto.conf

    # Terminal 2 - Start GDA
    cd ~/IoT_labs_TELE6530/gda-java-components
    java -jar target/gateway-device-app-0.0.1-jar-with-dependencies.jar

    # Terminal 3 - Start CDA
    cd ~/IoT_labs_TELE6530
    source venv/bin/activate
    python -m programmingtheiot.cda.app.ConstrainedDeviceApp

    # Run MQTT performance tests (INT-10-001)
    mvn test -Dtest=MqttClientPerformanceTest -pl .

    # Run CoAP performance tests (INT-10-002)
    mvn test -Dtest=CoapClientPerformanceTest -pl .

    # Run MQTT connector test (GDA-10-001, GDA-10-002)
    mvn test -Dtest=MqttClientConnectorTest -pl .

    # Run humidity actuation test (GDA-10-003)
    mvn test -Dtest=DeviceDataManagerSimpleCdaActuationTest -pl .

## Tests Passed

| Task | Test | Result |
|------|------|--------|
| PIOT-INT-10-001 | MQTT Performance - Connect/Disconnect | PASSED |
| PIOT-INT-10-001 | MQTT Performance - QoS 0 (10,000 msgs) | PASSED |
| PIOT-INT-10-001 | MQTT Performance - QoS 1 (10,000 msgs) | PASSED |
| PIOT-INT-10-001 | MQTT Performance - QoS 2 (10,000 msgs) | PASSED |
| PIOT-INT-10-002 | CoAP Performance - POST NON (10,000 msgs) | PASSED |
| PIOT-INT-10-002 | CoAP Performance - POST CON (10,000 msgs) | PASSED |
| PIOT-GDA-10-001 | MqttClientConnector TLS + auth support | PASSED |
| PIOT-GDA-10-002 | MqttClientConnector async + listener classes | PASSED |
| PIOT-GDA-10-003 | DeviceDataManager humidity actuation logic | PASSED |
| PIOT-INT-10-003 | CDA↔GDA MQTT integration (no TLS) | PASSED |
| PIOT-INT-10-004 | CDA↔GDA MQTT integration (TLS port 8883) | PASSED |

## GDA MQTT Client Performance Test Results (PIOT-STU-10-002)

Tests run with 10,000 messages against local Mosquitto broker (localhost:1883).
NOTE: Used synchronous MqttClient for performance tests (not MqttAsyncClient) per INT-10-001 requirements.

### Connect/Disconnect
    INFO: Connect and Disconnect [1]: 321 ms

### QoS 0 — 10,000 messages
    INFO: Publish message - QoS 0 [10000]: 12616 ms

### QoS 1 — 10,000 messages
    INFO: Publish message - QoS 1 [10000]: 18931 ms

### QoS 2 — 10,000 messages
    INFO: Publish message - QoS 2 [10000]: 30161 ms

| QoS Level | Time (ms) | vs QoS 0 Baseline |
|-----------|-----------|-------------------|
| QoS 0 | 12,616 ms | baseline |
| QoS 1 | 18,931 ms | +50% slower |
| QoS 2 | 30,161 ms | +139% slower |

- Fastest: QoS 0 (fire and forget, no acknowledgment)
- Slowest: QoS 2 (4-step handshake per message)

## GDA CoAP Client Performance Test Results (PIOT-STU-10-002)

Tests run with 10,000 messages against local GDA CoAP server (localhost:5683).

### POST NON — 10,000 messages
    INFO: POST message - useCON = false [10000]: 24101 ms

### POST CON — 10,000 messages
    INFO: POST message - useCON = true [10000]: 22755 ms

| Message Type | Time (ms) | vs NON Baseline |
|--------------|-----------|-----------------|
| NON | 24,101 ms | baseline |
| CON | 22,755 ms | -5.6% (CON faster) |

- Fastest: CON (flow control more efficient on loopback)
- Slowest: NON
- Note: CON was slightly faster than NON because on local loopback, acknowledgments are near-instant and CON flow control can be more efficient than NON retransmit handling.

## Integration Test Results

### PIOT-INT-10-003 — CDA↔GDA MQTT (No TLS)
- GDA received sensor messages from CDA via MQTT:
    INFO: MQTT message arrived on topic: 'PIOT/ConstrainedDevice/SensorMsg'
    INFO: MQTT message arrived on topic: 'PIOT/ConstrainedDevice/SystemPerfMsg'
    INFO: POST request received for resource: SensorMsg
    INFO: POST request received for resource: SystemPerfMsg
- Both applications ran for 5+ minutes without errors
- Clean shutdown confirmed

### PIOT-INT-10-004 — CDA↔GDA MQTT (TLS Encrypted)
- Mosquitto confirmed TLS connection on port 8883:
    1777661156: New connection from 127.0.0.1:33179 on port 8883.
- GDA connected via TLS using server.crt certificate
- All MQTT messages encrypted in transit
- Wireshark confirmed TLS handshake on loopback adapter

## Wireshark MQTT Protocol Output (PIOT-INT-10-003)

### MQTT CONNECT (No TLS - Port 1883)
    Message Type: Connect Command (1)
    Protocol Name: MQTT
    Protocol Level: 4 (MQTT 3.1.1)
    Connect Flags: 0x02
    Keep Alive: 60
    Client ID: gatewaydevice001

### MQTT SUBSCRIBE - CDA Topics
    Message Type: Subscribe Request (8)
    Topics:
      - PIOT/ConstrainedDevice/ActuatorResponse (QoS 1)
      - PIOT/ConstrainedDevice/SensorMsg (QoS 1)
      - PIOT/ConstrainedDevice/SystemPerfMsg (QoS 1)

### MQTT PUBLISH - Actuator Command to CDA
    Message Type: Publish Message (3)
    Topic: PIOT/ConstrainedDevice/ActuatorCmd
    QoS Level: At most once delivery (Fire and Forget) (0)
    Payload: {"typeID": 1001, "command": 1, "name": "HVAC", "value": 40.0}

### MQTT TLS Handshake (Port 8883)
    Transport Layer Security
      TLSv1.3 Record Layer: Handshake Protocol: Client Hello
      TLSv1.3 Record Layer: Handshake Protocol: Server Hello
      TLSv1.3 Record Layer: Change Cipher Spec Protocol
      TLSv1.3 Record Layer: Application Data Protocol: mqtt

## Summary
Lab Module 10 added intelligent edge messaging between the GDA and CDA using MQTT and CoAP. The GDA now uses an asynchronous MQTT client with dedicated inner listener classes for SensorData, SystemPerformanceData, and ActuatorData response messages. TLS-encrypted MQTT connections are supported on port 8883. The DeviceDataManager implements humidity threshold analysis and publishes actuation commands to the CDA when thresholds are exceeded. Performance testing confirmed QoS 0 is fastest for MQTT and CON was marginally faster than NON for CoAP on local loopback. All 11 tasks passed successfully.
