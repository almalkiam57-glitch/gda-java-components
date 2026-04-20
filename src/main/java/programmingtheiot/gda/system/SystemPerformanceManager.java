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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;

public class SystemPerformanceManager
{
	private static final Logger _Logger =
		Logger.getLogger(SystemPerformanceManager.class.getName());

	private int pollRate = ConfigConst.DEFAULT_POLL_CYCLES;

	private ScheduledExecutorService schedExecSvc = null;
	private SystemCpuUtilTask sysCpuUtilTask = null;
	private SystemMemUtilTask sysMemUtilTask = null;

	private Runnable taskRunner = null;
	private boolean isStarted = false;

	public SystemPerformanceManager()
	{
		this.pollRate =
			ConfigUtil.getInstance().getInteger(
				ConfigConst.GATEWAY_DEVICE,
				ConfigConst.POLL_CYCLES_KEY,
				ConfigConst.DEFAULT_POLL_CYCLES);

		if (this.pollRate <= 0) {
			this.pollRate = ConfigConst.DEFAULT_POLL_CYCLES;
		}

		this.schedExecSvc   = Executors.newScheduledThreadPool(1);
		this.sysCpuUtilTask = new SystemCpuUtilTask();
		this.sysMemUtilTask = new SystemMemUtilTask();

		this.taskRunner = () -> {
			this.handleTelemetry();
		};
	}

	public void handleTelemetry()
	{
		float cpuUtil = this.sysCpuUtilTask.getTelemetryValue();
		float memUtil = this.sysMemUtilTask.getTelemetryValue();

		_Logger.info("Handle telemetry results: cpuUtil=" + cpuUtil + ", memUtil=" + memUtil);
	}

	public void startManager()
	{
		if (!this.isStarted) {
			_Logger.info("SystemPerformanceManager is starting...");

			this.schedExecSvc.scheduleAtFixedRate(
				this.taskRunner,
				1L,
				this.pollRate,
				TimeUnit.SECONDS
			);

			this.isStarted = true;
		} else {
			_Logger.info("SystemPerformanceManager is already started.");
		}
	}

	public void stopManager()
	{
		this.schedExecSvc.shutdown();
		this.isStarted = false;

		_Logger.info("SystemPerformanceManager is stopped.");
	}
}