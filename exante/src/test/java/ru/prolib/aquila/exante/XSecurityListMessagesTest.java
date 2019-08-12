package ru.prolib.aquila.exante;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;

import static org.easymock.EasyMock.*;

import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import quickfix.field.CFICode;
import quickfix.field.SecurityListRequestType;
import quickfix.field.SecurityReqID;
import quickfix.field.Symbol;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;
import quickfix.fix44.SecurityList;
import quickfix.fix44.SecurityListRequest;

public class XSecurityListMessagesTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private IMocksControl control;
	private XMessageDispatcher dispMock;
	private XRepo repoMock;
	private XResponseHandler rhMock;
	private XSecurityListMessages service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispMock = control.createMock(XMessageDispatcher.class);
		repoMock = control.createMock(XRepo.class);
		rhMock = control.createMock(XResponseHandler.class);
		service = new XSecurityListMessages(dispMock, repoMock);
	}
	
	@Test
	public void testList1() throws Exception {
		expect(repoMock.newRequest(rhMock)).andReturn("102763");
		Capture<Message> msg_cap = Capture.newInstance();
		dispMock.send(capture(msg_cap));
		control.replay();
		
		service.list(rhMock);
		
		control.verify();
		SecurityListRequest expected = new SecurityListRequest();
		expected.set(new SecurityReqID("102763"));
		expected.set(new SecurityListRequestType(SecurityListRequestType.ALL_SECURITIES));
		assertEquals(expected.toRawString(), msg_cap.getValue().toRawString());
	}
	
	@Test
	public void testList2_CFICode() throws Exception {
		expect(repoMock.newRequest(rhMock)).andReturn("926212");
		Capture<Message> msg_cap = Capture.newInstance();
		dispMock.send(capture(msg_cap));
		control.replay();
		
		service.list(new CFICode("XOPMOP"), rhMock);
		
		control.verify();
		SecurityListRequest expected = new SecurityListRequest();
		expected.set(new SecurityReqID("926212"));
		expected.set(new SecurityListRequestType(SecurityListRequestType.SECURITYTYPE_AND_OR_CFICODE));
		expected.set(new CFICode("XOPMOP"));
		assertEquals(expected.toRawString(), msg_cap.getValue().toRawString());
	}
	
	@Test
	public void testList2_Symbol() throws Exception {
		expect(repoMock.newRequest(rhMock)).andReturn("441672");
		Capture<Message> msg_cap = Capture.newInstance();
		dispMock.send(capture(msg_cap));
		control.replay();
		
		service.list(new Symbol("AAPL"), rhMock);
		
		control.verify();
		SecurityListRequest expected = new SecurityListRequest();
		expected.set(new SecurityReqID("441672"));
		expected.set(new SecurityListRequestType(SecurityListRequestType.SYMBOL));
		expected.set(new Symbol("AAPL"));
		assertEquals(expected.toRawString(), msg_cap.getValue().toRawString());
	}
	
	@Test
	public void testApprove() throws Exception {
		SecurityListRequest message = new SecurityListRequest();
		message.set(new SecurityReqID("716221"));
		repoMock.approve("716221", message);
		control.replay();
		
		service.approve(message);
		
		control.verify();
	}
	
	@Test
	public void testRejected() throws Exception {
		BusinessMessageReject message = new BusinessMessageReject();
		repoMock.rejected(message);
		control.replay();
		
		service.rejected(message);
		
		control.verify();
	}
	
	@Test
	public void testResponse() throws Exception {
		SecurityList message = new SecurityList();
		message.set(new SecurityReqID("716272"));
		repoMock.response("716272", message);
		control.replay();
		
		service.response(message);
		
		control.verify();
	}

}
