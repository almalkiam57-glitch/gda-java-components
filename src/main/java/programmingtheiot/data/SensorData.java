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

import java.io.Serializable;

import programmingtheiot.common.ConfigConst;

public class SensorData extends BaseIotData implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int   sensorType = ConfigConst.DEFAULT_TYPE_ID;
	private float value      = ConfigConst.DEFAULT_VAL;

	public SensorData()
	{
		super();
	}

	public SensorData(int sensorType)
	{
		super();
		this.sensorType = sensorType;
	}

	public float getValue()
	{
		return this.value;
	}

	public void setValue(float val)
	{
		super.updateTimeStamp();
		this.value = val;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder(super.toString());

		sb.append(',');
		sb.append(ConfigConst.VALUE_PROP).append('=').append(this.getValue());

		return sb.toString();
	}

	protected void handleUpdateData(BaseIotData data)
	{
		if (data instanceof SensorData) {
			SensorData sData = (SensorData) data;
			this.setValue(sData.getValue());
		}
	}
} 
