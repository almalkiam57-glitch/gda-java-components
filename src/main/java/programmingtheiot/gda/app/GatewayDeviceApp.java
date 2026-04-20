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

package programmingtheiot.gda.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.gda.system.SystemPerformanceManager;

public class GatewayDeviceApp
{
	private static final Logger _Logger =
		Logger.getLogger(GatewayDeviceApp.class.getName());

	private SystemPerformanceManager sysPerfMgr = null;

	public GatewayDeviceApp()
	{
		this(null);
	}

	public GatewayDeviceApp(String[] args)
	{
		super();

		_Logger.info("Initializing GDA...");

		this.sysPerfMgr = new SystemPerformanceManager();

		parseArgs(args);
	}

	public void startApp()
	{
		_Logger.info("Starting GDA...");

		try {
			this.sysPerfMgr.startManager();
			_Logger.info("GDA started successfully.");
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to start GDA.", e);
			stopApp(-1);
		}
	}

	public void stopApp(int code)
	{
		_Logger.info("Stopping GDA...");

		try {
			if (this.sysPerfMgr != null) {
				this.sysPerfMgr.stopManager();
			}
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to stop GDA.", e);
		}

		_Logger.info("GDA stopped successfully with exit code " + code + ".");
	}

	private void initConfig(String fileName)
	{
		if (fileName != null) {
			_Logger.info("Attempting to load configuration: " + fileName);
		} else {
			_Logger.info("Attempting to load configuration: Default.");
		}
	}

	private void parseArgs(String[] args)
	{
		if (args == null || args.length == 0) {
			_Logger.info("No command line args to parse.");
		} else {
			_Logger.info("Parsing command line args.");
		}

		initConfig(null);
	}

	public static void main(String[] args)
	{
		GatewayDeviceApp gwApp = new GatewayDeviceApp(args);

		gwApp.startApp();

		try {
			Thread.sleep(65000L);
		} catch (InterruptedException e) {
			// ignore
		}

		gwApp.stopApp(0);
	}
}