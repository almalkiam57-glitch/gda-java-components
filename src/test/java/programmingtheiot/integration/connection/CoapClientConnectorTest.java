/**
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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import programmingtheiot.common.DefaultDataMessageListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SystemStateData;
import programmingtheiot.gda.connection.*;

public class CoapClientConnectorTest
{
	public static final int DEFAULT_TIMEOUT = 5;
	public static final boolean USE_DEFAULT_RESOURCES = true;

	private static final Logger _Logger =
		Logger.getLogger(CoapClientConnectorTest.class.getName());

	private CoapClientConnector coapClient = null;
	private IDataMessageListener dataMsgListener = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		this.coapClient = new CoapClientConnector();
		this.dataMsgListener = new DefaultDataMessageListener();
		this.coapClient.setDataMessageListener(this.dataMsgListener);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testConnectAndDiscover()
	{
		assertTrue(this.coapClient.sendDiscoveryRequest(DEFAULT_TIMEOUT));
	}

	@Test
	public void testGetRequestCon()
	{
		assertTrue(this.coapClient.sendGetRequest(
			ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, null, true, DEFAULT_TIMEOUT));
	}

	@Test
	public void testGetRequestNon()
	{
		assertTrue(this.coapClient.sendGetRequest(
			ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, null, false, DEFAULT_TIMEOUT));
	}

	@Test
	public void testPostRequestCon()
	{
		int actionCmd = 1;
		SystemStateData ssd = new SystemStateData();
		ssd.setCommand(actionCmd);
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPostRequest(
			ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, null, true, ssdJson, DEFAULT_TIMEOUT));
	}

	@Test
	public void testPostRequestNon()
	{
		int actionCmd = 1;
		SystemStateData ssd = new SystemStateData();
		ssd.setCommand(actionCmd);
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPostRequest(
			ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, null, false, ssdJson, DEFAULT_TIMEOUT));
	}

	@Test
	public void testPutRequestCon()
	{
		int actionCmd = 2;
		SystemStateData ssd = new SystemStateData();
		ssd.setCommand(actionCmd);
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPutRequest(
			ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, null, true, ssdJson, DEFAULT_TIMEOUT));
	}

	@Test
	public void testPutRequestNon()
	{
		int actionCmd = 2;
		SystemStateData ssd = new SystemStateData();
		ssd.setCommand(actionCmd);
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPutRequest(
			ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, null, false, ssdJson, DEFAULT_TIMEOUT));
	}

	@Test
	public void testDeleteRequestCon()
	{
		assertTrue(this.coapClient.sendDeleteRequest(
			ResourceNameEnum.GDA_MGMT_STATUS_CMD_RESOURCE, null, true, DEFAULT_TIMEOUT));
	}

	@Test
	public void testDeleteRequestNon()
	{
		assertTrue(this.coapClient.sendDeleteRequest(
			ResourceNameEnum.GDA_MGMT_STATUS_CMD_RESOURCE, null, false, DEFAULT_TIMEOUT));
	}

	@Test
	public void testObserve()
	{
		assertTrue(this.coapClient.startObserver(
			ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, null, 30));

		try {
			Thread.sleep(30000L);
		} catch (InterruptedException e) {
			// ignore
		}

		assertTrue(this.coapClient.stopObserver(
			ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, null, 5));
	}
}
