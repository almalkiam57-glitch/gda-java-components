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
import java.lang.management.OperatingSystemMXBean;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;

/**
 * Task for retrieving CPU utilization telemetry from the host system.
 */
public class SystemCpuUtilTask extends BaseSystemUtilTask
{
	// constructors
	
	/**
	 * Default constructor that initializes the task with CPU identity constants.
	 */
	public SystemCpuUtilTask()
	{
		// Use specific CPU constants instead of NOT_SET
		super(ConfigConst.CPU_UTIL_NAME, ConfigConst.CPU_UTIL_TYPE);
	}
	
	
	// public methods
	
	/**
	 * Overrides the base method to retrieve actual CPU load average.
	 * @return float The system load average (percentage or -1.0 if not supported).
	 */
	@Override
	public float getTelemetryValue()
	{
		// Use ManagementFactory to get the OperatingSystemMXBean
		OperatingSystemMXBean mxBean = ManagementFactory.getOperatingSystemMXBean();
		
		// Retrieve the system load average
		double cpuUtil = mxBean.getSystemLoadAverage();
		
		return (float) cpuUtil;
	}
} 

