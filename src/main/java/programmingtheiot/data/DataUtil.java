/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * You may find it more helpful to your design to adjust the
 * functionality, constants and interfaces (if there are any)
 * provided within in order to meet the needs of your specific
 * Programming the Internet of Things project.
 */

package programmingtheiot.data;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;

import programmingtheiot.common.ConfigConst;

public class DataUtil
{
	// static

	private static final Logger _Logger =
		Logger.getLogger(DataUtil.class.getName());

	private static final DataUtil _Instance = new DataUtil();

	public static final DataUtil getInstance()
	{
		return _Instance;
	}


	// constructors

	private DataUtil()
	{
		super();
	}


	// public methods

	public String actuatorDataToJson(ActuatorData actuatorData)
	{
		String jsonData = null;

		if (actuatorData != null) {
			Gson gson = new Gson();
			jsonData = gson.toJson(actuatorData);
			_Logger.info("ActuatorData to JSON: " + jsonData);
		}

		return jsonData;
	}

	public String actuatorDataToTimeAndValueJson(ActuatorData actuatorData)
	{
		String jsonData = null;

		if (actuatorData != null) {
			Gson gson = new Gson();
			jsonData = gson.toJson(actuatorData);
			_Logger.info("ActuatorData to TimeAndValue JSON: " + jsonData);
		}

		return jsonData;
	}

	public String sensorDataToJson(SensorData sensorData)
	{
		String jsonData = null;

		if (sensorData != null) {
			Gson gson = new Gson();
			jsonData = gson.toJson(sensorData);
			_Logger.info("SensorData to JSON: " + jsonData);
		}

		return jsonData;
	}

	public String sensorDataToTimeAndValueJson(SensorData sensorData)
	{
		String jsonData = null;

		if (sensorData != null) {
			Gson gson = new Gson();
			jsonData = gson.toJson(sensorData);
			_Logger.info("SensorData to TimeAndValue JSON: " + jsonData);
		}

		return jsonData;
	}

	public String systemPerformanceDataToJson(SystemPerformanceData sysPerfData)
	{
		String jsonData = null;

		if (sysPerfData != null) {
			Gson gson = new Gson();
			jsonData = gson.toJson(sysPerfData);
			_Logger.info("SystemPerformanceData to JSON: " + jsonData);
		}

		return jsonData;
	}

	public String systemStateDataToJson(SystemStateData sysStateData)
	{
		String jsonData = null;

		if (sysStateData != null) {
			Gson gson = new Gson();
			jsonData = gson.toJson(sysStateData);
			_Logger.info("SystemStateData to JSON: " + jsonData);
		}

		return jsonData;
	}

	public ActuatorData jsonToActuatorData(String jsonData)
	{
		ActuatorData data = null;

		if (jsonData != null && jsonData.trim().length() > 0) {
			Gson gson = new Gson();
			data = gson.fromJson(jsonData, ActuatorData.class);
			_Logger.info("JSON to ActuatorData: " + data);
		}

		return data;
	}

	public SensorData jsonToSensorData(String jsonData)
	{
		SensorData data = null;

		if (jsonData != null && jsonData.trim().length() > 0) {
			Gson gson = new Gson();
			data = gson.fromJson(jsonData, SensorData.class);
			_Logger.info("JSON to SensorData: " + data);
		}

		return data;
	}

	public SystemPerformanceData jsonToSystemPerformanceData(String jsonData)
	{
		SystemPerformanceData data = null;

		if (jsonData != null && jsonData.trim().length() > 0) {
			Gson gson = new Gson();
			data = gson.fromJson(jsonData, SystemPerformanceData.class);
			_Logger.info("JSON to SystemPerformanceData: " + data);
		}

		return data;
	}

	public SystemStateData jsonToSystemStateData(String jsonData)
	{
		SystemStateData data = null;

		if (jsonData != null && jsonData.trim().length() > 0) {
			Gson gson = new Gson();
			data = gson.fromJson(jsonData, SystemStateData.class);
			_Logger.info("JSON to SystemStateData: " + data);
		}

		return data;
	}

} 

