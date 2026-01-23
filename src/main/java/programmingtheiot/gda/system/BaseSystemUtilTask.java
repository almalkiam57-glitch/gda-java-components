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

import java.util.logging.Logger;
import programmingtheiot.common.ConfigConst;

/**
 * Base abstract class for all system utility tasks in the GDA.
 */
public abstract class BaseSystemUtilTask
{
    // CHANGE THIS LINE: from private to protected
    protected static final Logger _Logger =
        Logger.getLogger(BaseSystemUtilTask.class.getName());
    
    // private variables stay private
    private String name   = ConfigConst.NOT_SET;
    private int    typeID = ConfigConst.DEFAULT_SENSOR_TYPE;
    
    // constructors
    public BaseSystemUtilTask(String name, int typeID)
    {
        super();
        
        if (name != null) {
            this.name = name;
        }
        
        this.typeID = typeID;
    }
    
    // public methods
    public String getName()
    {
        return this.name;
    }
    
    public int getTypeID()
    {
        return this.typeID;
    }
    
    public abstract float getTelemetryValue();
} 
