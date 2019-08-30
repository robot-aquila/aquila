package ru.prolib.aquila.exante;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgSeqNum;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;

public class XRepo {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(XRepo.class);
	}
	
	public interface RequestIDSequence {
		String next();
	}
	
	public static class ALongRequestIDSequence implements RequestIDSequence {
		private final AtomicLong along;
		
		public ALongRequestIDSequence(AtomicLong along) {
			this.along = along;
		}
		
		public ALongRequestIDSequence() {
			this(new AtomicLong());
		}

		@Override
		public String next() {
			return Long.toUnsignedString(along.incrementAndGet());
		}
		
	}
	
	public static class UUIDRequestIDSequence implements RequestIDSequence {

		@Override
		public String next() {
			return UUID.randomUUID().toString();
		}
		
	}
	
	private final Object monitor = new Object();
	private final RequestIDSequence requestIdSeq;
	private final Map<String, XResponseHandler> reqIdToHandler;
	private final Map<String, Integer> reqIdToMsgSeqNum;
	private final Map<Integer, String>  msgSeqNumToReqID;

	public XRepo(RequestIDSequence request_id_seq,
			Map<String, XResponseHandler> req_id_to_handler,
			Map<String, Integer> req_id_to_msg_seq_num,
			Map<Integer, String> msg_seq_num_to_req_id)
	{
		this.requestIdSeq = request_id_seq;
		this.reqIdToHandler = req_id_to_handler;
		this.reqIdToMsgSeqNum = req_id_to_msg_seq_num;
		this.msgSeqNumToReqID = msg_seq_num_to_req_id;
	}
	
	public XRepo() {
		this(new UUIDRequestIDSequence(), new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	
	public RequestIDSequence getRequestIDSequence() {
		return requestIdSeq;
	}
	
	public String newRequest(XResponseHandler handler) {
		String request_id = requestIdSeq.next();
		synchronized ( monitor ) {
			reqIdToHandler.put(request_id, handler);
		}
		return request_id;
	}
	
	public void approve(String request_id, Message message) throws
		FieldNotFound,
		DoNotSend,
		IllegalStateException
	{
		MsgSeqNum msg_seq_num = new MsgSeqNum();
		message.getHeader().getField(msg_seq_num);
		link(request_id, msg_seq_num.getValue());
		logger.debug("Request approved: request_id={} msg_seq_num={}", request_id, msg_seq_num.getValue());
	}
	
	public void rejected(BusinessMessageReject message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType,
		IllegalStateException
	{
		String request_id = getRequestID(message.getRefSeqNum().getValue());
		XResponseHandler handler = getHandler(request_id);
		handler.onReject(message);
		removeHandler(request_id);
		logger.debug("Request rejected: {}", request_id);
	}
	
	public void response(String request_id, Message message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType,
		IllegalStateException
	{
		XResponseHandler handler = getHandler(request_id);
		if ( handler.onMessage(message) ) {
			removeHandler(request_id);
		}
	}

	private void throwNotFound(String request_id) throws IllegalStateException {
		throw new IllegalStateException("Handler not found: request_id=" + request_id);
	}
	
	private void link(String request_id, int msg_seq_num) throws IllegalStateException {
		synchronized ( monitor ) {
			if ( ! reqIdToHandler.containsKey(request_id) ) {
				throwNotFound(request_id);
			}
			msgSeqNumToReqID.put(msg_seq_num, request_id);
			reqIdToMsgSeqNum.put(request_id, msg_seq_num);
		}
	}
	
	private XResponseHandler getHandler(String request_id) throws IllegalStateException {
		synchronized ( monitor ) {
			XResponseHandler handler = reqIdToHandler.get(request_id);
			if ( handler == null ) {
				throwNotFound(request_id);
			}
			return handler;
		}
	}
	
	private String getRequestID(int msg_seq_num) throws IllegalStateException {
		synchronized ( monitor ) {
			String request_id = msgSeqNumToReqID.get(msg_seq_num);
			if ( request_id == null ) {
				throw new IllegalStateException("Request ID not found: msg_seq_num=" + msg_seq_num);
			}
			return request_id;
		}
	}
	
	private void removeHandler(String request_id) {
		XResponseHandler handler = null;
		synchronized ( monitor ) {
			handler = reqIdToHandler.remove(request_id);
			Integer msg_seq_num = reqIdToMsgSeqNum.remove(request_id);
			if ( msg_seq_num != null ) {
				msgSeqNumToReqID.remove(msg_seq_num);
			}
		}
		if ( handler != null ) {
			handler.close();
		}
	}
	
}
