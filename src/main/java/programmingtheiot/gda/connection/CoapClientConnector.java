/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 */

package programmingtheiot.gda.connection;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.elements.exception.ConnectorException;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import org.eclipse.californium.core.CoapObserveRelation;

import programmingtheiot.gda.connection.handlers.GenericCoapResponseHandler;
import programmingtheiot.gda.connection.handlers.SensorDataObserverHandler;
import programmingtheiot.gda.connection.handlers.SystemPerformanceDataObserverHandler;

/**
 * CoAP client connector using Eclipse Californium.
 * Lab Module 09 - PIOT-GDA-09-001 through PIOT-GDA-09-008
 */
public class CoapClientConnector implements IRequestResponseClient
{
	private static final Logger _Logger =
		Logger.getLogger(CoapClientConnector.class.getName());

	private String   protocol;
	private String   host;
	private int      port;
	private String   serverAddr;
	private CoapClient clientConn;
	private CoapObserveRelation observeRelation = null;
	private IDataMessageListener dataMsgListener;

	public CoapClientConnector()
	{
		ConfigUtil config = ConfigUtil.getInstance();

		this.host = config.getProperty(
			ConfigConst.COAP_GATEWAY_SERVICE,
			ConfigConst.HOST_KEY,
			ConfigConst.DEFAULT_HOST);

		if (config.getBoolean(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.ENABLE_CRYPT_KEY)) {
			this.protocol = ConfigConst.DEFAULT_COAP_SECURE_PROTOCOL;
			this.port     = config.getInteger(
				ConfigConst.COAP_GATEWAY_SERVICE,
				ConfigConst.SECURE_PORT_KEY,
				ConfigConst.DEFAULT_COAP_SECURE_PORT);
		} else {
			this.protocol = ConfigConst.DEFAULT_COAP_PROTOCOL;
			this.port     = config.getInteger(
				ConfigConst.COAP_GATEWAY_SERVICE,
				ConfigConst.PORT_KEY,
				ConfigConst.DEFAULT_COAP_PORT);
		}

		this.serverAddr = this.protocol + "://" + this.host + ":" + this.port;

		initClient();

		_Logger.info("Using URL for server conn: " + this.serverAddr);
	}

	public CoapClientConnector(String host, boolean isSecure, boolean enableConfirmedMsgs)
	{
		this.host = host;

		if (isSecure) {
			this.protocol = ConfigConst.DEFAULT_COAP_SECURE_PROTOCOL;
			this.port     = ConfigConst.DEFAULT_COAP_SECURE_PORT;
		} else {
			this.protocol = ConfigConst.DEFAULT_COAP_PROTOCOL;
			this.port     = ConfigConst.DEFAULT_COAP_PORT;
		}

		this.serverAddr = this.protocol + "://" + this.host + ":" + this.port;

		initClient();

		_Logger.info("Using URL for server conn: " + this.serverAddr);
	}

	@Override
	public boolean sendDiscoveryRequest(int timeout)
	{
		_Logger.info("Issuing discover...");

		try {
			this.clientConn.setTimeout((long) timeout * 1000L);

			Set<WebLink> wlSet = this.clientConn.discover();

			if (wlSet != null) {
				for (WebLink wl : wlSet) {
					_Logger.info(" --> URI: " + wl.getURI() +
						". Attributes: " + wl.getAttributes());
				}
				return true;
			} else {
				_Logger.warning("Discovery request failed - no response.");
				return false;
			}
		} catch (ConnectorException | java.io.IOException e) {
			_Logger.log(Level.WARNING, "Discovery request failed.", e);
			return false;
		}
	}

	@Override
	public boolean sendDeleteRequest(ResourceNameEnum resource, String name,
		boolean enableCON, int timeout)
	{
		if (resource == null) {
			_Logger.warning("sendDeleteRequest: resource is null. Ignoring.");
			return false;
		}

		String resourcePath = getResourcePath(resource, name);
		_Logger.info("Sending DELETE to: " + resourcePath);

		try {
			if (enableCON) {
				this.clientConn.useCONs();
			} else {
				this.clientConn.useNONs();
			}

			this.clientConn.setURI(resourcePath);
			this.clientConn.setTimeout((long) timeout * 1000L);

			CoapResponse response = this.clientConn.delete();

			if (response != null) {
				_Logger.info("Handling DELETE. Response: " + response.isSuccess() +
					" - " + response.getOptions() +
					" - " + response.getCode() +
					" - " + response.getResponseText());

				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleIncomingMessage(
						resource, response.getResponseText());
				}

				return true;
			} else {
				_Logger.warning("Handling DELETE. No response received.");
				return false;
			}
		} catch (ConnectorException | java.io.IOException e) {
			_Logger.log(Level.WARNING, "DELETE request failed.", e);
			return false;
		}
	}

	@Override
	public boolean sendGetRequest(ResourceNameEnum resource, String name,
		boolean enableCON, int timeout)
	{
		if (resource == null) {
			_Logger.warning("sendGetRequest: resource is null. Ignoring.");
			return false;
		}

		String resourcePath = getResourcePath(resource, name);
		_Logger.info("Sending GET to: " + resourcePath);

		try {
			if (enableCON) {
				this.clientConn.useCONs();
			} else {
				this.clientConn.useNONs();
			}

			this.clientConn.setURI(resourcePath);
			this.clientConn.setTimeout((long) timeout * 1000L);

			CoapResponse response = this.clientConn.get();

			if (response != null) {
				_Logger.info("Handling GET. Response: " + response.isSuccess() +
					" - " + response.getOptions() +
					" - " + response.getCode() +
					" - " + response.getResponseText());

				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleIncomingMessage(
						resource, response.getResponseText());
				}

				return true;
			} else {
				_Logger.warning("Handling GET. No response received.");
				return false;
			}
		} catch (ConnectorException | java.io.IOException e) {
			_Logger.log(Level.WARNING, "GET request failed.", e);
			return false;
		}
	}

	@Override
	public boolean sendPostRequest(ResourceNameEnum resource, String name,
		boolean enableCON, String payload, int timeout)
	{
		if (resource == null || payload == null) {
			_Logger.warning("sendPostRequest: resource or payload is null. Ignoring.");
			return false;
		}

		String resourcePath = getResourcePath(resource, name);
                // _Logger.info("Sending POST to: " + resourcePath);

		try {
			if (enableCON) {
				this.clientConn.useCONs();
			} else {
				this.clientConn.useNONs();
			}

			this.clientConn.setURI(resourcePath);
			this.clientConn.setTimeout((long) timeout * 1000L);

			CoapResponse response =
				this.clientConn.post(payload, MediaTypeRegistry.APPLICATION_JSON);

			if (response != null) {
				_Logger.info("Handling POST. Response: " + response.isSuccess() +
					" - " + response.getOptions() +
					" - " + response.getCode() +
					" - " + response.getResponseText());

				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleIncomingMessage(
						resource, response.getResponseText());
				}

				return true;
			} else {
				_Logger.warning("Handling POST. No response received.");
				return false;
			}
		} catch (ConnectorException | java.io.IOException e) {
			_Logger.log(Level.WARNING, "POST request failed.", e);
			return false;
		}
	}

	@Override
	public boolean sendPutRequest(ResourceNameEnum resource, String name,
		boolean enableCON, String payload, int timeout)
	{
		if (resource == null || payload == null) {
			_Logger.warning("sendPutRequest: resource or payload is null. Ignoring.");
			return false;
		}

		String resourcePath = getResourcePath(resource, name);
                // _Logger.info("Sending PUT to: " + resourcePath);

		try {
			if (enableCON) {
				this.clientConn.useCONs();
			} else {
				this.clientConn.useNONs();
			}

			this.clientConn.setURI(resourcePath);
			this.clientConn.setTimeout((long) timeout * 1000L);

			CoapResponse response =
				this.clientConn.put(payload, MediaTypeRegistry.APPLICATION_JSON);

			if (response != null) {
				_Logger.info("Handling PUT. Response: " + response.isSuccess() +
					" - " + response.getOptions() +
					" - " + response.getCode() +
					" - " + response.getResponseText());

				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleIncomingMessage(
						resource, response.getResponseText());
				}

				return true;
			} else {
				_Logger.warning("Handling PUT. No response received.");
				return false;
			}
		} catch (ConnectorException | java.io.IOException e) {
			_Logger.log(Level.WARNING, "PUT request failed.", e);
			return false;
		}
	}

	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		if (listener != null) {
			this.dataMsgListener = listener;
			return true;
		}
		return false;
	}

	@Override
	public void clearEndpointPath()
	{
		this.clientConn.setURI(this.serverAddr);
		_Logger.info("Cleared endpoint path to: " + this.serverAddr);
	}

	@Override
	public void setEndpointPath(ResourceNameEnum resource)
	{
		if (resource != null) {
			String path = this.serverAddr + "/" + resource.getResourceName();
			this.clientConn.setURI(path);
			_Logger.info("Set endpoint path to: " + path);
		}
	}

	@Override
	public boolean startObserver(ResourceNameEnum resource, String name, int ttl)
	{
		if (resource == null) {
			_Logger.warning("startObserver: resource is null. Ignoring.");
			return false;
		}

		String resourcePath = getResourcePath(resource, name);
		_Logger.info("Observing resource [START]: " + resourcePath);

		this.clientConn.setURI(resourcePath);

		SensorDataObserverHandler handler = new SensorDataObserverHandler();
		handler.setDataMessageListener(this.dataMsgListener);

		this.observeRelation = this.clientConn.observe(handler);

                return (this.observeRelation != null && !this.observeRelation.isCanceled());

	}

	@Override
	public boolean stopObserver(ResourceNameEnum resourceType, String name, int timeout)
	{
		if (this.observeRelation != null) {
			_Logger.info("Observing resource [STOP]: " + resourceType);
			this.observeRelation.proactiveCancel();
			this.observeRelation = null;
			return true;
		} else {
			_Logger.warning("No active observe relation to cancel.");
			return false;
		}
	}

	private void initClient()
	{
		try {
			CoapConfig.register();
			UdpConfig.register();

			this.clientConn = new CoapClient(this.serverAddr);
			_Logger.info("Created client connection to server / resource: " + this.serverAddr);

		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to connect to server: " + this.serverAddr, e);
		}
	}

	private String getResourcePath(ResourceNameEnum resource, String name)
	{
		StringBuilder sb = new StringBuilder(this.serverAddr)
			.append("/")
			.append(resource.getResourceName());

		if (name != null && !name.isEmpty()) {
			sb.append("/").append(name);
		}

		return sb.toString();
	}
}
