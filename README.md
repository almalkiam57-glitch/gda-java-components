=========================
📄 GDA README — Lab Module 02
=========================
Overview

In Lab Module 02, I implemented system performance monitoring for the Gateway Device Application (GDA). The application now collects CPU and memory utilization data and logs it periodically.

What I Implemented

I implemented the following components:

GatewayDeviceApp
SystemPerformanceManager
SystemCpuUtilTask
SystemMemUtilTask
BaseSystemUtilTask

I also connected the performance manager to the application lifecycle.

How It Works
The SystemPerformanceManager uses a scheduled executor.
It runs periodically based on the configured poll rate.
Each cycle:
CPU utilization is retrieved using OperatingSystemMXBean
Memory utilization is retrieved using MemoryMXBean
Results are logged to the console.
Testing

I tested the implementation using Maven unit and integration tests.

Build:
mvn install -DskipTests
Tests:
mvn test -Dtest=SystemCpuUtilTaskTest -DforkCount=0
mvn test -Dtest=SystemMemUtilTaskTest -DforkCount=0
mvn test -Dtest=SystemPerformanceManagerTest -DforkCount=0
mvn test -Dtest=GatewayDeviceAppTest -DforkCount=0
Combined Test:
mvn test -Dtest=GatewayDeviceAppTest,SystemPerformanceManagerTest,SystemCpuUtilTaskTest,SystemMemUtilTaskTest -DforkCount=0

Note:
Running full mvn test shows failures for future labs (MQTT, CoAP, Persistence), which are not part of Lab 2.

Summary

This lab added system monitoring capabilities to GDA using Java system APIs and scheduling, enabling real-time performance tracking of CPU and memory usage.
