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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;

/**
 * Manager class to coordinate system performance telemetry collection in the GDA.
 */
public class SystemPerformanceManager {
    // static logging instance
    private static final Logger _Logger = 
        Logger.getLogger(SystemPerformanceManager.class.getName());

    // private members
    private int pollRate = ConfigConst.DEFAULT_POLL_CYCLES;
    private ScheduledExecutorService schedExecSvc = null;
    private SystemCpuUtilTask sysCpuUtilTask = null;
    private SystemMemUtilTask sysMemUtilTask = null;
    private Runnable taskRunner = null;
    private boolean isStarted = false;

    /**
     * Public default constructor.
     * Initializes configuration settings and task instances.
     */
    public SystemPerformanceManager() {
        // Use ConfigUtil to retrieve the poll cycle property
        this.pollRate = ConfigUtil.getInstance().getInteger(
            ConfigConst.GATEWAY_DEVICE, 
            ConfigConst.POLL_CYCLES_KEY, 
            ConfigConst.DEFAULT_POLL_CYCLES);
        
        // Sanity check: ensure pollRate is positive
        if (this.pollRate <= 0) {
            this.pollRate = ConfigConst.DEFAULT_POLL_CYCLES;
        }

        // Initialize the scheduler and task workers
        this.schedExecSvc   = Executors.newScheduledThreadPool(1);
        this.sysCpuUtilTask = new SystemCpuUtilTask();
        this.sysMemUtilTask = new SystemMemUtilTask();
        
        // Define the task runner lambda to call handleTelemetry
        this.taskRunner = () -> {
            this.handleTelemetry();
        };
    }

    /**
     * Retrieves CPU and Memory utilization values and logs them.
     */
    public void handleTelemetry() {
        float cpuUtil = this.sysCpuUtilTask.getTelemetryValue();
        float memUtil = this.sysMemUtilTask.getTelemetryValue();
        
        // Logging results at info level for visibility during testing
        _Logger.info("Handle telemetry results: cpuUtil=" + cpuUtil + ", memUtil=" + memUtil);
    }

    /**
     * Starts the manager and schedules the telemetry task runner.
     * @return boolean True if successfully started.
     */
    public boolean startManager() {
        if (! this.isStarted) {
            _Logger.info("SystemPerformanceManager is starting...");
            
            // Schedule the task runner to execute at a fixed rate
            this.schedExecSvc.scheduleAtFixedRate(this.taskRunner, 1L, this.pollRate, TimeUnit.SECONDS);
            
            this.isStarted = true;
        } else {
            _Logger.info("SystemPerformanceManager is already started.");
        }
        
        return this.isStarted;
    }

    /**
     * Stops the manager and shuts down the scheduler.
     * @return boolean True if successfully stopped.
     */
    public boolean stopManager() {
        _Logger.info("SystemPerformanceManager is stopping...");
        
        this.schedExecSvc.shutdown();
        this.isStarted = false;
        
        _Logger.info("SystemPerformanceManager is stopped.");
        
        return true;
    }
} 
