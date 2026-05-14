=========================
📄 GDA README — Lab Module 11
=========================

## Overview
In Lab Module 11, I implemented cloud integration functionality in the GDA using MQTT to connect to the Ubidots STEM cloud service. Building directly on the TLS-secured MQTT pipeline from Lab Module 10, the GDA now acts as a cloud gateway — forwarding telemetry (sensor data and system performance data) received from the CDA up to Ubidots, and relaying LED actuation commands from the cloud back down to the CDA.

## What I Implemented
I implemented/updated the following components:
- MqttClientConnector (cloud config support, IConnectionListener, protected pub/sub methods)
- ICloudClient (new interface in gda.connection package)
- IConnectionListener (existing interface in gda.connection package)
- CloudClientConnector (new class implementing ICloudClient + IConnectionListener)
- DeviceDataManager (cloud client wiring, handleIncomingMessage update)
- DataUtil (actuatorDataToTimeAndValueJson, sensorDataToTimeAndValueJson)
- PiotConfig.props (Cloud.GatewayService section with Ubidots credentials and cert)

## How It Works

### MqttClientConnector (GDA-11-001)
- Added two new class-scoped variables:
  - connListener: IConnectionListener reference for notifying CloudClientConnector
  - useCloudGatewayConfig: boolean flag to switch between local and cloud MQTT config
- Added three constructors:
  - Default: delegates to MqttClientConnector(false)
  - Boolean: switches config section based on useCloudGatewayConfig flag
  - String: loads config from specified section (MQTT or Cloud.GatewayService)
- setConnectionListener() stores IConnectionListener reference
- connectComplete() calls connListener.onConnect() after successful connection
- disconnectClient() calls connListener.onDisconnect() on clean disconnect
- Added protected String-based publish/subscribe/unsubscribe methods
- Public ResourceNameEnum-based methods now delegate to protected String-based methods
- connectComplete() skips CDA topic subscriptions when useCloudGatewayConfig is true

### ICloudClient (GDA-11-002)
- New interface in programmingtheiot.gda.connection package
- Defines: connectClient, disconnectClient, sendEdgeDataToCloud (x2),
  subscribeToCloudEvents, unsubscribeFromCloudEvents, setDataMessageListener

### DataUtil (GDA-11-002)
- Updated actuatorDataToTimeAndValueJson() to use TimeAndValuePayloadData
- Updated sensorDataToTimeAndValueJson() to use TimeAndValuePayloadData
- Both produce minimal value + timestamp payload for Ubidots compatibility

### CloudClientConnector (GDA-11-003, GDA-11-004)
- Implements ICloudClient and IConnectionListener
- Constructor loads baseTopic from Cloud.GatewayService in PiotConfig.props
- connectClient() creates MqttClientConnector(CLOUD_GATEWAY_SERVICE) and registers itself as IConnectionListener
- onConnect() publishes initial invalid ActuatorData to create LED topic on Ubidots, then subscribes to LED actuation topic
- sendEdgeDataToCloud(SensorData) converts using sensorDataToTimeAndValueJson and publishes
- sendEdgeDataToCloud(SystemPerformanceData) splits into CpuUtil and MemUtil SensorData instances
- LedEnablementMessageListener inner class parses incoming LED commands (ON=1, OFF=0) and forwards to DeviceDataManager

### DeviceDataManager (GDA-11-003, GDA-11-004)
- cloudClient field typed as ICloudClient
- initManager() instantiates CloudClientConnector when enableCloudClient is true
- startManager() connects cloud client first before starting sysPerfMgr
- stopManager() unsubscribes from cloud events and disconnects cloud client
- handleSensorMessage() forwards SensorData to cloudClient.sendEdgeDataToCloud()
- handleSystemPerformanceMessage() forwards SystemPerformanceData to cloudClient.sendEdgeDataToCloud()
- handleIncomingMessage() validates ActuatorData JSON and publishes to CDA via local MQTT

### Cloud Configuration (CFG-11-001)
- Created Ubidots STEM account at stem.ubidots.com
- Generated API token stored in UbidotsCloudCred.props (outside Git repo)
- Downloaded Ubidots TLS certificate chain (2 certs) as UbidotsCloudCert.pem
- Updated PiotConfig.props:
  - host = industrial.api.ubidots.com
  - securePort = 8883
  - enableCrypt = True
  - baseTopic = /v1.6/devices/
 
  Terminal 1 — Mosquitto
bashsudo mosquitto -c /etc/mosquitto/mosquitto.conf


Terminal 2 — GDA
bashcd /home/nawaf/IoT_labs_TELE6530/gda-java-components
mvn test -Dtest=CloudClientConnectorTest -Dsurefire.failIfNoSpecifiedTests=false 


Terminal 3 — CDA
bashcd /home/nawaf/IoT_labs_TELE6530
source venv/bin/activate
python -m programmingtheiot.cda.app.ConstrainedDeviceApp

## Tests

| Task | Test | Command |
|------|------|---------|
| PIOT-GDA-11-002 | TimeAndValuePayloadDataTest | `mvn test -Dtest=TimeAndValuePayloadDataTest` |
| PIOT-GDA-11-003 | CloudClientConnectorTest | `mvn test -Dtest=CloudClientConnectorTest` |
| PIOT-GDA-11-004 | CloudClientConnectorTest (full) | `mvn test -Dtest=CloudClientConnectorTest` |

## Test Results

| Task | Test | Result |
|------|------|--------|
| PIOT-GDA-11-001 | MqttClientConnector updated — compile check | PASSED |
| PIOT-GDA-11-002 | TimeAndValuePayloadDataTest | PASSED |
| PIOT-GDA-11-003 | CloudClientConnectorTest (integrated) | PASSED |
| PIOT-GDA-11-004 | CloudClientConnectorTest (integrated + LED) | PASSED |

## Integration Test Log Output

    INFO: CloudClientConnector created. Topic prefix: /v1.6/devices/
    INFO: Configuring TLS...
    INFO: PEM file valid. Using secure connection: .../UbidotsCloudCert.pem
    INFO: Successfully imported X.509 certificate UbidotsCloudCert.pem.3139.1
    INFO: Successfully imported X.509 certificate UbidotsCloudCert.pem.1939.2
    INFO: X.509 certificate load and TLSv1.2 socket init successful
    INFO: TLS enabled.
    INFO: Successfully loaded credentials from file: .../UbidotsCloudCred.props
    INFO: Credentials now set.
    INFO: Using URL for broker conn: ssl://industrial.api.ubidots.com:8883
    INFO: Setting connection listener.
    INFO: MQTT client connecting to broker: ssl://industrial.api.ubidots.com:8883
    INFO: Successfully connected cloud client.
    INFO: MQTT connection successful (is reconnect = false). Broker: ssl://localhost:8883
    INFO: Subscribing to topic: PIOT/ConstrainedDevice/ActuatorResponse
    INFO: Subscribing to topic: PIOT/ConstrainedDevice/SensorMsg
    INFO: Subscribing to topic: PIOT/ConstrainedDevice/SystemPerfMsg

    Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
    BUILD SUCCESS

## Summary
Lab Module 11 extended the GDA to connect the existing edge-tier IoT pipeline to the Ubidots STEM cloud service via TLS-secured MQTT. The GDA forwards sensor and system performance data from the CDA to Ubidots using the TimeAndValuePayloadData format. The CloudClientConnector implements both ICloudClient and IConnectionListener, delegating MQTT connectivity to an internally managed MqttClientConnector configured in cloud mode. Upon successful cloud connection, the GDA subscribes to the LED actuation topic and processes incoming commands via the LedEnablementMessageListener inner class, which forwards LED ON/OFF commands to the CDA via the local MQTT broker. All four required GDA tasks passed successfully.
