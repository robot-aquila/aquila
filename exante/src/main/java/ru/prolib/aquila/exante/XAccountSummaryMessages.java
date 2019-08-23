package ru.prolib.aquila.exante;

import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;

public class XAccountSummaryMessages {
	public static final String MSGTYPE_SUMMARY_REQUEST = "UASQ";
	public static final String MSGTYPE_SUMMARY_RESPONSE = "UASR";
	public static final String MSGTYPE_SUMMARY_REJECT = "UASJ";
	
	public static final int TAG_ACCOUNT_REQUEST_ID = 20020;
	public static final int TAG_REJECT_REASON = 20022;
	public static final int TAG_ACCOUNT_CURRENCY = 20023;
	public static final int TAG_USED_MARGIN = 20040;
	public static final int TAG_NUM_REPORTS = 20021;
	public static final int TAG_PROFIT_AND_LOSS = 20030;
	public static final int TAG_CONVERTED_PROFIT_AND_LOSS = 20031;
	public static final int TAG_VALUE = 20032;
	public static final int TAG_CONVERTED_VALUE = 20033;
	
	private final XMessageDispatcher dispatcher;
	private final XRepo repo;
	
	XAccountSummaryMessages(XMessageDispatcher dispatcher, XRepo repo) {
		this.dispatcher = dispatcher;
		this.repo = repo;
	}
	
	public XAccountSummaryMessages(XMessageDispatcher dispatcher) {
		this(dispatcher, new XRepo());
	}
	
	public XAccountSummaryMessages(SessionID session_id) {
		this(new XMessageDispatcher(session_id));
	}
	
	public void query(XResponseHandler handler) {
		Message request = newRequest(handler);
		dispatcher.send(request);
	}
	
	public void approve(Message message) throws
		FieldNotFound,
		DoNotSend,
		IllegalStateException
	{
		repo.approve(message.getString(TAG_ACCOUNT_REQUEST_ID), message);
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
	
	public void response(Message message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType,
		IllegalStateException
	{
		repo.response(message.getString(TAG_ACCOUNT_REQUEST_ID), message);
	}

	private Message newRequest(XResponseHandler handler) {
		String request_id = repo.newRequest(handler);
		Message request = new Message();
		request.getHeader().setField(new MsgType(MSGTYPE_SUMMARY_REQUEST));
		request.setString(TAG_ACCOUNT_REQUEST_ID, request_id);
		//request.setString(TAG_ACCOUNT_CURRENCY, "USD");
		return request;
	}
	
}
