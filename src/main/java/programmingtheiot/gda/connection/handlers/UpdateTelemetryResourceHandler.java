package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;

public class UpdateTelemetryResourceHandler extends CoapResource
{
	private static final Logger _Logger =
		Logger.getLogger(UpdateTelemetryResourceHandler.class.getName());

	private IDataMessageListener dataMsgListener = null;

	public UpdateTelemetryResourceHandler(String resourceName)
	{
		super(resourceName);
	}

	public void setDataMessageListener(IDataMessageListener listener)
	{
		if (listener != null) {
			this.dataMsgListener = listener;
		}
	}

	@Override
	public void handleGET(CoapExchange context)
	{
		_Logger.info("GET request received for resource: " + this.getName());
		context.respond(ResponseCode.CONTENT, "Telemetry Resource - GET OK", MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePUT(CoapExchange context)
	{
		ResponseCode code = ResponseCode.NOT_ACCEPTABLE;
		context.accept();

		if (this.dataMsgListener != null) {
			try {
				String jsonData = new String(context.getRequestPayload());
				SensorData sensorData =
					DataUtil.getInstance().jsonToSensorData(jsonData);
				this.dataMsgListener.handleSensorMessage(
					ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sensorData);
				code = ResponseCode.CHANGED;
			} catch (Exception e) {
				_Logger.warning("Failed to handle PUT request. Message: " + e.getMessage());
				code = ResponseCode.BAD_REQUEST;
			}
		} else {
			_Logger.info("No callback listener for request. Ignoring PUT.");
			code = ResponseCode.CONTINUE;
		}

		context.respond(code, "Update telemetry data request handled: " + super.getName());
	}

	@Override
	public void handlePOST(CoapExchange context)
	{
		_Logger.info("POST request received for resource: " + this.getName());
		context.respond(ResponseCode.CHANGED);
	}

	@Override
	public void handleDELETE(CoapExchange context)
	{
		_Logger.info("DELETE request received for resource: " + this.getName());
		context.respond(ResponseCode.DELETED);
	}
}
