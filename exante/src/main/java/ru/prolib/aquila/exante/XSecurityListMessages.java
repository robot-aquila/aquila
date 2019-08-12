package ru.prolib.aquila.exante;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.CFICode;
import quickfix.field.SecurityListRequestType;
import quickfix.field.SecurityReqID;
import quickfix.field.Symbol;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.SecurityList;
import quickfix.fix44.SecurityListRequest;

public class XSecurityListMessages {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(XSecurityListMessages.class);
	}
	
	private final XMessageDispatcher dispatcher;
	private final XRepo repo;
	
	XSecurityListMessages(XMessageDispatcher dispatcher, XRepo repo) {
		this.dispatcher = dispatcher;
		this.repo = repo;
	}
	
	public XSecurityListMessages(XMessageDispatcher dispatcher) {
		this(dispatcher, new XRepo());
	}
	
	public XSecurityListMessages(SessionID session_id) {
		this(new XMessageDispatcher(session_id));
	}
	
	public void list(XResponseHandler handler) throws SessionNotFound {
		SecurityListRequest request = newRequest(handler);
		request.set(new SecurityListRequestType(SecurityListRequestType.ALL_SECURITIES));
		dispatcher.send(request);
		logger.debug("Request sent");
	}
	
	public void list(CFICode code, XResponseHandler handler) throws SessionNotFound {
		SecurityListRequest request = newRequest(handler);
		request.set(new SecurityListRequestType(SecurityListRequestType.SECURITYTYPE_AND_OR_CFICODE));
		request.set(code);
		dispatcher.send(request);
		logger.debug("Request sent (CFI code={})", code.getValue());
	}
	
	public void list(Symbol symbol, XResponseHandler handler) throws SessionNotFound {
		SecurityListRequest request = newRequest(handler);
		request.set(new SecurityListRequestType(SecurityListRequestType.SYMBOL));
		request.set(symbol);
		dispatcher.send(request);
		logger.debug("Request sent (symbol={})", symbol.getValue());
	}
	
	public void approve(SecurityListRequest message) throws
		FieldNotFound,
		DoNotSend,
		IllegalStateException
	{
		repo.approve(message.getSecurityReqID().getValue(), message);
	}
	
	public void rejected(BusinessMessageReject message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType,
		IllegalStateException
	{
		repo.rejected(message);
	}
	
	public void response(SecurityList message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType,
		IllegalStateException
	{
		repo.response(message.getSecurityReqID().getValue(), message);
	}

	private SecurityListRequest newRequest(XResponseHandler handler) {
		String request_id = repo.newRequest(handler);
		SecurityListRequest request = new SecurityListRequest();
		request.set(new SecurityReqID(request_id));
		return request;
	}

}
