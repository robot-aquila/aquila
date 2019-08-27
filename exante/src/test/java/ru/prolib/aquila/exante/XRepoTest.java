package ru.prolib.aquila.exante;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import quickfix.field.MsgSeqNum;
import quickfix.field.RefSeqNum;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;
import ru.prolib.aquila.exante.XRepo.RequestIDSequence;

public class XRepoTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	
	private IMocksControl control;
	private XResponseHandler rhMock1, rhMock2, rhMock3;
	private RequestIDSequence request_id_seqMock;
	private Map<String, XResponseHandler> req_id_to_handler;
	private Map<String, Integer> req_id_to_msg_seq_num;
	private Map<Integer, String> msg_seq_num_to_req_id;
	private XRepo service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rhMock1 = control.createMock(XResponseHandler.class);
		rhMock2 = control.createMock(XResponseHandler.class);
		rhMock3 = control.createMock(XResponseHandler.class);
		request_id_seqMock = control.createMock(RequestIDSequence.class);
		req_id_to_handler = new HashMap<>();
		req_id_to_msg_seq_num = new HashMap<>();
		msg_seq_num_to_req_id = new HashMap<>();
		service = new XRepo(request_id_seqMock, req_id_to_handler, req_id_to_msg_seq_num, msg_seq_num_to_req_id);
	}
	
	@Test
	public void testNewRequest() {
		expect(request_id_seqMock.next()).andReturn("827");
		control.replay();
		
		String actual = service.newRequest(rhMock1);

		control.verify();
		Map<String, XResponseHandler> expected_map = new HashMap<>();
		expected_map.put("827", rhMock1);
		assertEquals("827", actual);
		assertEquals(expected_map, req_id_to_handler);
		assertEquals(new HashMap<>(), req_id_to_msg_seq_num);
		assertEquals(new HashMap<>(), msg_seq_num_to_req_id);
	}
	
	@Test
	public void testApprove() throws Exception {
		Message message = new Message();
		message.getHeader().setField(new MsgSeqNum(215));
		req_id_to_handler.put("886", rhMock2);
		control.replay();
		
		service.approve("886", message);
		
		control.verify();
		Map<Integer, String> expected_msg2req = new HashMap<>();
		expected_msg2req.put(215, "886");
		assertEquals(expected_msg2req, msg_seq_num_to_req_id);
		Map<String, Integer> expected_req2msg = new HashMap<>();
		expected_req2msg.put("886", 215);
		assertEquals(expected_req2msg, req_id_to_msg_seq_num);
	}
	
	@Test
	public void testApprove_ThrowsIfNotFound() throws Exception {
		Message message = new Message();
		message.getHeader().setField(new MsgSeqNum(215));
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Handler not found: request_id=886");
		
		service.approve("886", message);
	}
	
	@Test
	public void testRejected() throws Exception {
		BusinessMessageReject message = new BusinessMessageReject();
		message.set(new RefSeqNum(661));
		msg_seq_num_to_req_id.put(821, "65571"); // handler #1
		req_id_to_msg_seq_num.put("65571", 821);
		req_id_to_handler.put("65571", rhMock1);
		msg_seq_num_to_req_id.put(661, "71829"); // handler #2
		req_id_to_msg_seq_num.put("71829", 661);
		req_id_to_handler.put("71829", rhMock2);
		msg_seq_num_to_req_id.put(881, "21551"); // handler #3
		req_id_to_msg_seq_num.put("21551", 881);
		req_id_to_handler.put("21551", rhMock3);
		rhMock2.onReject(message);
		rhMock2.close();
		control.replay();
		
		service.rejected(message);
		
		control.verify();
		Map<Integer, String> expected_msg2req = new HashMap<>();
		expected_msg2req.put(821, "65571");
		expected_msg2req.put(881, "21551");
		assertEquals(expected_msg2req, msg_seq_num_to_req_id);
		Map<String, Integer> expected_req2msg = new HashMap<>();
		expected_req2msg.put("65571", 821);
		expected_req2msg.put("21551", 881);
		assertEquals(expected_req2msg, req_id_to_msg_seq_num);
		Map<String, XResponseHandler> expected_req2hdr = new HashMap<>();
		expected_req2hdr.put("65571", rhMock1);
		expected_req2hdr.put("21551", rhMock3);
		assertEquals(expected_req2hdr, req_id_to_handler);
	}
	
	@Test
	public void testRejected_ThrowsIfNotFound_MsgSeqNum() throws Exception {
		BusinessMessageReject message = new BusinessMessageReject();
		message.set(new RefSeqNum(661));
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Request ID not found: msg_seq_num=661");
		
		service.rejected(message);
	}
	
	@Test
	public void testRejected_ThrowsIfNotFound_ReqID() throws Exception {
		BusinessMessageReject message = new BusinessMessageReject();
		message.set(new RefSeqNum(821));
		msg_seq_num_to_req_id.put(821, "65571"); // handler #1
		req_id_to_msg_seq_num.put("65571", 821);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Handler not found: request_id=65571");
		
		service.rejected(message);
	}
	
	@Test
	public void testResponse() throws Exception {
		Message message = new Message();
		req_id_to_handler.put("66812", rhMock1);
		expect(rhMock1.onMessage(message)).andReturn(false);
		control.replay();
		
		service.response("66812", message);
		
		control.verify();
		Map<String, XResponseHandler> expected_map = new HashMap<>();
		expected_map.put("66812", rhMock1);
		assertEquals(expected_map, req_id_to_handler);
	}
	
	@Test
	public void testResponse_WhenDone() throws Exception {
		Message message = new Message();
		req_id_to_handler.put("66812", rhMock1);
		expect(rhMock1.onMessage(message)).andReturn(true);
		rhMock1.close();
		control.replay();
		
		service.response("66812", message);
		
		control.verify();
		assertEquals(new HashMap<>(), req_id_to_handler);
	}
	
	@Test
	public void testResponse_ThrowsIfNotFound() throws Exception {
		Message message = new Message();
		control.replay();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Handler not found: request_id=66812");
		
		service.response("66812", message);
	}

}
