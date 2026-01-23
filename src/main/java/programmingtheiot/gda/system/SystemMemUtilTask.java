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

package programmingtheiot.gda.system;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;

/**
 * Task for retrieving JVM memory utilization telemetry.
 */
public class SystemMemUtilTask extends BaseSystemUtilTask
{
	// constructors
	
	/**
	 * Default constructor that initializes the task with Memory identity constants.
	 */
	public SystemMemUtilTask()
	{
		// Correctly pass constants to BaseSystemUtilTask
		super(ConfigConst.MEM_UTIL_NAME, ConfigConst.MEM_UTIL_TYPE);
	}
	
	
	// public methods
	
	/**
	 * Overrides the base method to calculate actual JVM heap memory utilization.
	 * @return float The memory utilization percentage (0.0 to 100.0).
	 */
	@Override
	public float getTelemetryValue()
	{
		// Retrieve memory usage data from the JVM
		MemoryUsage memUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		
		double memUsed = (double) memUsage.getUsed();
		double memMax  = (double) memUsage.getMax();
		
		// Log the raw values for debugging purposes
		// Note: Ensure _Logger is 'protected' in BaseSystemUtilTask.java
		_Logger.fine("Mem used: " + memUsed + "; Mem Max: " + memMax);
		
		// Calculate the utilization percentage: (Used / Max) * 100
		double memUtil = (memUsed / memMax) * 100.0d;
		
		return (float) memUtil;
	}
} 

