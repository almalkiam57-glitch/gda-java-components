=========================
📄 GDA README — Lab Module 05
=========================

## Overview
In Lab Module 05, I implemented data containers for the Gateway Device
Application (GDA), updated the SystemPerformanceManager to use the
SystemPerformanceData container, implemented DataUtil for JSON conversion,
created the DeviceDataManager, and connected it to the GatewayDeviceApp.

## What I Implemented
I implemented/updated the following components:
- ActuatorData
- SensorData
- SystemPerformanceData
- SystemPerformanceManager
- DataUtil
- DeviceDataManager
- GatewayDeviceApp

## How It Works

### Data Containers (ActuatorData, SensorData, SystemPerformanceData)
- All three extend `BaseIotData` and implement `handleUpdateData()`.
- Each has private class-scoped variables with getters and setters.
- `SystemPerformanceData` sets its name to `ConfigConst.SYS_PERF_DATA`
  in its constructor.

### SystemPerformanceManager
- Updated `handleTelemetry()` to create a `SystemPerformanceData` instance
  each polling cycle, setting CPU and memory utilization values.
- Calls `handleSystemPerformanceMessage()` on the `IDataMessageListener`
  if one is registered.
- Added `setDataMessageListener()` to register the callback listener.

### DataUtil
- Implemented full bidirectional JSON conversion using Google's `Gson` library:
  - `ActuatorData` ↔ JSON
  - `SensorData` ↔ JSON
  - `SystemPerformanceData` ↔ JSON
  - `SystemStateData` ↔ JSON
- Singleton pattern using a private constructor and static `getInstance()`.

### DeviceDataManager
- Central manager class that implements `IDataMessageListener`.
- Reads configuration flags to determine which components to enable.
- Creates and manages a `SystemPerformanceManager` instance.
- Implements all callback methods: `handleActuatorCommandResponse()`,
  `handleIncomingMessage()`, `handleSensorMessage()`,
  `handleSystemPerformanceMessage()`.

### GatewayDeviceApp
- Removed direct `SystemPerformanceManager` reference.
- Now creates and manages a `DeviceDataManager` instance.
- `startApp()` calls `dataMgr.startManager()`.
- `stopApp()` calls `dataMgr.stopManager()`.

## Testing
I tested the implementation using Maven:

### Unit Tests
```bash
cd ~/IoT_labs_TELE6530/gda-java-components
mvn test -Dtest="ActuatorDataTest,SensorDataTest,SystemPerformanceDataTest"
mvn test -Dtest="programmingtheiot.unit.data.DataUtilTest"
```

### Integration Tests
```bash
mvn test -Dtest="programmingtheiot.integration.system.SystemPerformanceManagerTest"
mvn test -Dtest="programmingtheiot.integration.data.DataIntegrationTest"
mvn test -Dtest="programmingtheiot.integration.app.DeviceDataManagerNoCommsTest"
mvn test -Dtest="programmingtheiot.integration.app.GatewayDeviceAppTest"
```

## Summary
This lab added structured data containers to the GDA, implemented JSON
serialization via DataUtil, and introduced DeviceDataManager as the central
hub for data processing and component management, wiring it into the
GatewayDeviceApp lifecycle.
