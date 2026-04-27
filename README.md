=========================
📄 GDA README — Lab Module 07
=========================
## Overview
In Lab Module 07, I implemented a full MQTT publish/subscribe client for the
Gateway Device Application (GDA) using the Paho MQTT Java library, wired it
into the DeviceDataManager, and verified all 14 MQTT 3.1.1 Control Packet
types using Wireshark.

## What I Implemented
I implemented/updated the following components:
- MqttClientConnector
- DeviceDataManager
- GatewayDeviceApp
- MqttClientControlPacketTest

## How It Works

### MqttClientConnector
- Implements the `IPubSubClient` and `MqttCallbackExtended` interfaces.
- Reads MQTT broker host, port, keepAlive, and useAsyncClient from
  `PiotConfig.props` via `ConfigUtil`.
- Generates a unique `clientID` using `MqttClient.generateClientId()`.
- `connectClient()` creates a Paho `MqttClient`, sets the callback to
  `this`, and connects to the broker using `MqttConnectOptions`.
- `disconnectClient()` disconnects cleanly from the broker.
- `connectComplete()`, `connectionLost()`, `deliveryComplete()`,
  `messageArrived()` callbacks log all MQTT events.
- `publishMessage()` validates the topic and QoS, builds an
  `MqttMessage`, and publishes it to the broker.
- `subscribeToTopic()` validates the topic and QoS and subscribes
  to the given resource.
- `unsubscribeFromTopic()` unsubscribes from the given resource.
- `setDataMessageListener()` registers an `IDataMessageListener` for
  incoming message callbacks.

### DeviceDataManager
- Updated `initManager()` to create an `MqttClientConnector` instance
  if `enableMqttClient` is true, and registers itself as the
  `IDataMessageListener`.
- `startManager()` calls `mqttClient.connectClient()` and subscribes
  to `GDA_MGMT_STATUS_MSG_RESOURCE`, `CDA_ACTUATOR_RESPONSE_RESOURCE`,
  `CDA_SENSOR_MSG_RESOURCE`, and `CDA_SYSTEM_PERF_MSG_RESOURCE`.
- `stopManager()` unsubscribes from all topics and calls
  `mqttClient.disconnectClient()`.

### GatewayDeviceApp
- Updated to use `DeviceDataManager` instead of `SystemPerformanceManager`
  directly, so the full GDA application now starts and stops the MQTT
  client through `DeviceDataManager`.

### MqttClientControlPacketTest
- Custom test class created to generate all 14 MQTT 3.1.1 Control Packets.
- `testConnectAndDisconnect()` generates CONNECT, CONNACK, and DISCONNECT
  by connecting and disconnecting from the broker.
- `testServerPing()` sustains the connection long enough to trigger
  the PINGREQ and PINGRESP keepAlive packets.
- `testPubSub()` uses both QoS 1 and QoS 2 to generate SUBSCRIBE,
  SUBACK, PUBLISH, PUBACK, PUBREC, PUBREL, PUBCOMP, UNSUBSCRIBE,
  and UNSUBACK packets.

## Testing

```bash
# Setup
cd ~/IoT_labs_TELE6530/gda-java-components

# Terminal 1 - Start Mosquitto broker
sudo systemctl start mosquitto

# PIOT-GDA-07-001 & 07-002 - Connect and Disconnect
mvn test -Dtest=MqttClientConnectorTest#testConnectAndDisconnect

# PIOT-GDA-07-003 - Publish and Subscribe
mvn test -Dtest=MqttClientConnectorTest#testPublishAndSubscribe

# PIOT-GDA-07-004 - DeviceDataManager with MQTT
# Terminal 1: mosquitto already running
# Terminal 2:
java -jar target/gateway-device-app-0.0.1-jar-with-dependencies.jar
# Terminal 3:
mvn test -Dtest=MqttClientConnectorTest#testPublishAndSubscribe

# PIOT-STU-07-002 - Wireshark capture of all 14 MQTT 3.1.1 Control Packets
wireshark &
mvn test -Dtest=MqttClientControlPacketTest
```

### Tests Passed

| Task | Test | Result | Time |
|------|------|--------|------|
| PIOT-GDA-07-001 & 07-002 | testConnectAndDisconnect | ✅ PASSED | 67.30s |
| PIOT-GDA-07-003 | testPublishAndSubscribe | ✅ PASSED | 98.93s |
| PIOT-GDA-07-004 | testPublishAndSubscribe (with GDA app) | ✅ PASSED | 99.14s |
| PIOT-STU-07-002 | testConnectAndDisconnect, testServerPing, testPubSub | ✅ PASSED | 78.31s |

### Wireshark — All 14 MQTT 3.1.1 Control Packets Captured

| # | Packet Type | QoS |
|---|------------|-----|
| 1 | CONNECT | - |
| 2 | CONNACK | - |
| 3 | DISCONNECT | - |
| 4 | SUBSCRIBE | - |
| 5 | SUBACK | - |
| 6 | PUBLISH | QoS 1 |
| 7 | PUBACK | QoS 1 |
| 8 | PUBLISH | QoS 2 |
| 9 | PUBREC | QoS 2 |
| 10 | PUBREL | QoS 2 |
| 11 | PUBCOMP | QoS 2 |
| 12 | UNSUBSCRIBE | - |
| 13 | UNSUBACK | - |
| 14 | PINGREQ | - |
| 15 | PINGRESP | - |

[Wireshark screenshots included in submission]

## Summary
This lab added full MQTT pub/sub capability to the GDA, implemented the
MqttClientConnector with all required callbacks in Java, wired it into
DeviceDataManager, updated GatewayDeviceApp to use DeviceDataManager,
and verified all 14 MQTT 3.1.1 Control Packet types using Wireshark
with both QoS 1 and QoS 2.

