=========================
📄 Project README — Lab Module 12
=========================

## Overview

In Lab Module 12, I implemented the semester project across both the Constrained Device Application (CDA) and Gateway Device Application (GDA) — a Petrochemical Facility Air Quality and Worker Safety Monitoring System. The CDA now simulates three hazardous gas sensors (CO, H2S, VOC) and activates a ventilation actuator when dangerous thresholds are exceeded. The GDA forwards all 9 sensor variables to the Ubidots cloud dashboard via secure MQTT/TLS, and routes actuation commands back down to the CDA.

---

## What I Implemented

### CDA — New Sensor Simulation
- CoSensorSimTask
- H2sSensorSimTask
- VocSensorSimTask

### CDA — New Actuator Simulation
- VentilationActuatorSimTask

### CDA — Updated Managers
- SensorAdapterManager
- ActuatorAdapterManager
- DeviceDataManager

### CDA — Updated Constants
- ConfigConst (new gas sensor types, names, thresholds)

### GDA — Updated Connection
- CloudClientConnector (fixed async connection timing + connection guard)

### Cloud — Ubidots Dashboard
- 9 live variables streaming every 5 seconds
- Gas gauges, thermometer, tank, charts, indicator, switch

---

## How It Works

The system follows a full three-tier data pipeline:

**Edge tier (CDA):**
- Gas sensors generate simulated values every 5 seconds
- SensorAdapterManager collects all sensor data
- DeviceDataManager processes the data
- Decision logic checks gas thresholds
- ActuatorAdapterManager sends ventilation commands
- VentilationActuator executes ON/OFF

**Gateway tier (GDA):**
- MqttClientConnector receives data from local broker
- DeviceDataManager routes data to cloud
- CloudClientConnector forwards to Ubidots via TLS
- Actuation commands from Ubidots routed back to CDA

**Cloud tier (Ubidots):**
- 9 variables update live every 5 seconds
- Dashboard shows gauges, charts, indicators
- Switch widget sends actuation commands back to CDA

**Example logic:**

CDA threshold checks:
- If CO > 35 ppm → Ventilation ON
- If H2S > 1 ppm → Ventilation ON
- If VOC > 200 index → Ventilation ON
- If all gases safe → Ventilation OFF

Gas spike simulation:
- CO: 5% chance per poll → spikes to 80–250 ppm
- H2S: 3% chance per poll → spikes to 10–75 ppm
- VOC: 6% chance per poll → spikes to 280–480 index

---

## Testing

I tested the implementation step by step across CDA, GDA, and Cloud.

**CDA — Sensor Simulation**
```bash
python -m pytest tests/unit/sim/test_CoSensorSimTask.py -q
python -m pytest tests/unit/sim/test_H2sSensorSimTask.py -q
python -m pytest tests/unit/sim/test_VocSensorSimTask.py -q
```

**CDA — Actuator Simulation**
```bash
python -m pytest tests/unit/sim/test_VentilationActuatorSimTask.py -q
```

**CDA — ActuatorAdapterManager**
```bash
python -m pytest tests/integration/system/test_ActuatorAdapterManager.py -q
```

**CDA — DeviceDataManager**
```bash
python -m pytest tests/integration/app/test_DeviceDataManagerNoComms.py -q
```

**CDA — Full Application**
```bash
python programmingtheiot/cda/app/ConstrainedDeviceApp.py
```

**GDA — CloudClientConnector**
```bash
cd gda-java-components
mvn test -Dtest=CloudClientConnectorTest
```

**GDA — Full Application**
```bash
mvn exec:java -Dexec.mainClass="programmingtheiot.gda.app.GatewayDeviceApp"
```

**End-to-End — Full Pipeline**
```bash
# Terminal 1 — GDA
cd ~/IoT_labs_TELE6530/gda-java-components
mvn exec:java -Dexec.mainClass="programmingtheiot.gda.app.GatewayDeviceApp"

# Terminal 2 — CDA
cd ~/IoT_labs_TELE6530
source venv/bin/activate
export PYTHONPATH=/home/nawaf/IoT_labs_TELE6530
python programmingtheiot/cda/app/ConstrainedDeviceApp.py
```

---

## Test Results

| Component | Test | Result |
|---|---|---|
| CDA | CO sensor polling and spike injection | ✅ PASSED |
| CDA | H2S sensor polling and spike injection | ✅ PASSED |
| CDA | VOC sensor polling and spike injection | ✅ PASSED |
| CDA | Ventilation ON when CO > 35 ppm | ✅ PASSED |
| CDA | Ventilation ON when H2S > 1 ppm | ✅ PASSED |
| CDA | Ventilation ON when VOC > 200 | ✅ PASSED |
| CDA | Ventilation OFF when all gases safe | ✅ PASSED |
| CDA | All 6 sensors publishing to local MQTT | ✅ PASSED |
| GDA | TLS connection to Ubidots:8883 | ✅ PASSED |
| GDA | Cloud connects before data arrives | ✅ PASSED |
| GDA | All 9 variables live in Ubidots | ✅ PASSED |
| GDA | Actuation command forwarded to CDA | ✅ PASSED |
| Cloud | Dashboard updating every 5 seconds | ✅ PASSED |

---

## Note

The GDA required a fix to CloudClientConnector because MqttAsyncClient connects asynchronously — data was arriving before the connection was fully established causing Client is not connected (32104) errors on every publish. A retry wait loop and connection guard were added to resolve this. The Ubidots API Key was also replaced with the correct Token in the credentials file — the API Key cannot be used for MQTT authentication.

---

## Summary

This lab implemented the full semester project — a Petrochemical Facility Air Quality and Worker Safety Monitoring System. The CDA now generates simulated gas sensor data with probabilistic spike events that simulate real leak scenarios, processes it locally with threshold logic, and automatically activates emergency ventilation when dangerous levels are detected. The GDA forwards all 9 sensor variables securely to the Ubidots cloud dashboard every 5 seconds where live gauges, charts, and indicators give supervisors complete real-time visibility. The architecture is designed so that replacing simulated sensors with real physical gas detectors requires only minimal changes — making this a credible, deployable foundation for real worker safety at petrochemical facilities like SABIC, Dow Chemical, or Saudi Aramco.
