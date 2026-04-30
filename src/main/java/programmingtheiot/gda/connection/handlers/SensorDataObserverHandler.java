/**
 * This class is part of the Programming the Internet of Things project.
 */

package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;

public class SensorDataObserverHandler implements CoapHandler
{
	private static final Logger _Logger =
		Logger.getLogger(SensorDataObserverHandler.class.getName());

	private IDataMessageListener dataMsgListener = null;

	public SensorDataObserverHandler()
	{
		super();
	}

	public void setDataMessageListener(IDataMessageListener listener)
	{
		this.dataMsgListener = listener;
	}

	@Override
	public void onError()
	{
		_Logger.warning("Handling CoAP error for SensorData observer...");
	}

	@Override
	public void onLoad(CoapResponse response)
	{
		if (response != null) {
			_Logger.info("Received CoAP response (SensorData JSON): " + response.getResponseText());

			if (this.dataMsgListener != null) {
				try {
					SensorData sd =
						DataUtil.getInstance().jsonToSensorData(response.getResponseText());
					this.dataMsgListener.handleSensorMessage(
						ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
				} catch (Exception e) {
					_Logger.warning("Failed to decode SensorData. Ignoring: " + response.getResponseText());
				}
			}
		} else {
			_Logger.warning("CoAP response is null. Ignoring.");
		}
	}
}
