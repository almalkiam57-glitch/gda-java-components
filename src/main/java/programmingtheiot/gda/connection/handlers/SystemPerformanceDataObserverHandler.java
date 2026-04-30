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
import programmingtheiot.data.SystemPerformanceData;

public class SystemPerformanceDataObserverHandler implements CoapHandler
{
	private static final Logger _Logger =
		Logger.getLogger(SystemPerformanceDataObserverHandler.class.getName());

	private IDataMessageListener dataMsgListener = null;

	public SystemPerformanceDataObserverHandler()
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
		_Logger.warning("Handling CoAP error for SystemPerformanceData observer...");
	}

	@Override
	public void onLoad(CoapResponse response)
	{
		if (response != null) {
			_Logger.info("Received CoAP response (SystemPerformanceData JSON): " + response.getResponseText());

			if (this.dataMsgListener != null) {
				try {
					SystemPerformanceData spd =
						DataUtil.getInstance().jsonToSystemPerformanceData(response.getResponseText());
					this.dataMsgListener.handleSystemPerformanceMessage(
						ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, spd);
				} catch (Exception e) {
					_Logger.warning("Failed to decode SystemPerformanceData. Ignoring: " + response.getResponseText());
				}
			}
		} else {
			_Logger.warning("CoAP response is null. Ignoring.");
		}
	}
}
