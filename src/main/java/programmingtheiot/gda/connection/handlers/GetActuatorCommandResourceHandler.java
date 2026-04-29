package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.IActuatorDataListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;

public class GetActuatorCommandResourceHandler extends CoapResource
	implements IActuatorDataListener
{
	// static

	private static final Logger _Logger =
		Logger.getLogger(GetActuatorCommandResourceHandler.class.getName());

	// params

	private ActuatorData actuatorData = null;


	// constructors

	public GetActuatorCommandResourceHandler(String resourceName)
	{
		super(resourceName);

		// set the resource to be observable
		super.setObservable(true);

		this.actuatorData = new ActuatorData();

		_Logger.info("GetActuatorCommandResourceHandler initialized for resource: " + resourceName);
	}


	// public methods

	@Override
	public boolean onActuatorDataUpdate(ActuatorData data)
	{
		if (data != null && this.actuatorData != null) {
			this.actuatorData.updateData(data);

			// notify all connected clients
			super.changed();

			_Logger.fine("Actuator data updated for URI: " + super.getURI() +
				": Data value = " + this.actuatorData.getValue());

			return true;
		}

		return false;
	}

	@Override
	public void handleGET(CoapExchange context)
	{
		_Logger.info("GET request received for actuator command resource: " + this.getName());

		context.accept();

		String jsonData = DataUtil.getInstance().actuatorDataToJson(this.actuatorData);

		context.respond(ResponseCode.CONTENT, jsonData, MediaTypeRegistry.APPLICATION_JSON);
	}

	@Override
	public void handlePUT(CoapExchange context)
	{
		_Logger.info("PUT request received for actuator command resource: " + this.getName());
		context.respond(ResponseCode.CHANGED);
	}

	@Override
	public void handlePOST(CoapExchange context)
	{
		_Logger.info("POST request received for actuator command resource: " + this.getName());
		context.respond(ResponseCode.CHANGED);
	}

	@Override
	public void handleDELETE(CoapExchange context)
	{
		_Logger.info("DELETE request received for actuator command resource: " + this.getName());
		context.respond(ResponseCode.DELETED);
	}
}
