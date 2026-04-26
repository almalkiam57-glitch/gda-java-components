/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 - 2025 by Andrew D. King
 */ 

package programmingtheiot.integration.connection;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.*;
import programmingtheiot.gda.connection.*;

public class MqttClientControlPacketTest
{
	private static final Logger _Logger =
		Logger.getLogger(MqttClientControlPacketTest.class.getName());

	private MqttClientConnector mqttClient = null;

	@Before
	public void setUp() throws Exception
	{
		this.mqttClient = new MqttClientConnector();
	}

	@After
	public void tearDown() throws Exception
	{
	}

	// Generates: CONNECT, CONNACK, DISCONNECT
	@Test
	public void testConnectAndDisconnect()
	{
		assertTrue(this.mqttClient.connectClient());
		assertFalse(this.mqttClient.connectClient());

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// ignore
		}

		assertTrue(this.mqttClient.disconnectClient());
		assertFalse(this.mqttClient.disconnectClient());
	}

	// Generates: CONNECT, CONNACK, PINGREQ, PINGRESP, DISCONNECT
	@Test
	public void testServerPing()
	{
		int keepAlive = ConfigUtil.getInstance().getInteger(
			ConfigConst.MQTT_GATEWAY_SERVICE,
			ConfigConst.KEEP_ALIVE_KEY,
			ConfigConst.DEFAULT_KEEP_ALIVE);

		assertTrue(this.mqttClient.connectClient());

		// Wait long enough to trigger PINGREQ / PINGRESP
		try {
			Thread.sleep(keepAlive * 1000 + 5000);
		} catch (Exception e) {
			// ignore
		}

		assertTrue(this.mqttClient.disconnectClient());
	}

	// Generates: CONNECT, CONNACK,
	//            SUBSCRIBE, SUBACK,
	//            PUBLISH, PUBACK (QoS 1),
	//            PUBLISH, PUBREC, PUBREL, PUBCOMP (QoS 2),
	//            UNSUBSCRIBE, UNSUBACK,
	//            DISCONNECT
	@Test
	public void testPubSub()
	{
		assertTrue(this.mqttClient.connectClient());

		// Subscribe to topic
		assertTrue(this.mqttClient.subscribeToTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, 0));

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// ignore
		}

		// QoS 1 - generates PUBLISH + PUBACK
		assertTrue(this.mqttClient.publishMessage(
			ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE,
			"Test QoS 1 message", 1));

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// ignore
		}

		// QoS 2 - generates PUBLISH + PUBREC + PUBREL + PUBCOMP
		assertTrue(this.mqttClient.publishMessage(
			ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE,
			"Test QoS 2 message", 2));

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// ignore
		}

		// Unsubscribe - generates UNSUBSCRIBE + UNSUBACK
		assertTrue(this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE));

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// ignore
		}

		assertTrue(this.mqttClient.disconnectClient());
	}
} 
