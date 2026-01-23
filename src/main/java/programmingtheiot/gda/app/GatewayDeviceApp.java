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
import java.util.Map;
import java.util.HashMap; 

import programmingtheiot.gda.system.SystemPerformanceManager;
import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.gda.system.SystemPerformanceManager;

/**
 * Main application wrapper for the Gateway Device App (GDA).
 */
public class GatewayDeviceApp {
	private static final Logger _Logger = 
		Logger.getLogger(GatewayDeviceApp.class.getName());
	
	private SystemPerformanceManager sysPerfManager;

	/**
	 * Default constructor (No-Argument). 
	 * Required for Integration Tests.
	 */
	public GatewayDeviceApp() {
		super();
		_Logger.info("Initializing GDA...");
		
		// Initialize the performance manager
		this.sysPerfManager = new SystemPerformanceManager();
	}

	/**
	 * Constructor with arguments.
	 * Used by the main method to handle command line inputs.
	 * * @param args Command line arguments.
	 */
	public GatewayDeviceApp(String[] args) {
		this(); // Calls the no-arg constructor above to initialize sysPerfManager
		parseArgs(args);
	}

	/**
	 * Starts the GDA application and its managers.
	 */
	public void startApp() {
		_Logger.info("Starting GDA...");
		try {
			if (this.sysPerfManager != null) {
				this.sysPerfManager.startManager(); //
			}
			_Logger.info("GDA started successfully.");
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to start GDA. Exiting.", e);
			stopApp(-1);
		}
	}

	/**
	 * Stops the GDA application and its managers.
	 * @param code The exit code.
	 */
	public void stopApp(int code) {
		_Logger.info("Stopping GDA...");
		try {
			if (this.sysPerfManager != null) {
				this.sysPerfManager.stopManager(); //
			}
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to cleanly stop GDA. Exiting.", e);
		}
		_Logger.log(Level.INFO, "GDA stopped successfully with exit code {0}.", code);
		
		// Note: Avoid System.exit(0) if running within a test suite
		if (code != 0) {
			System.exit(code);
		}
	}

	/**
	 * Internal method to handle argument parsing.
	 */
	private Map<String, String> parseArgs(String[] args) {
		Map<String, String> argMap = new HashMap<>();
		if (args != null && args.length > 0) {
			_Logger.info("Parsing " + args.length + " command line args.");
		} else {
			_Logger.info("No command line args to parse.");
		}
		return argMap;
	}

	/**
	 * Main entry point for the GDA.
	 */
	public static void main(String[] args) {
		GatewayDeviceApp gwApp = new GatewayDeviceApp(args);
		gwApp.startApp();

		try {
			// Sleep for 65 seconds to allow telemetry collection
			Thread.sleep(65000L);
		} catch (InterruptedException e) {
			_Logger.log(Level.WARNING, "GDA thread interrupted.", e);
		}

		gwApp.stopApp(0);
	}
} 


