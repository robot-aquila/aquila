package ru.prolib.aquila.exante;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.Password;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.SecurityList;
import quickfix.fix44.SecurityListRequest;

public class XApplication implements Application {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(XApplication.class);
	}
	
	private final Map<SessionID, String> sessionPasswords;
	private final XSessionActions sessionActions;
	private final XSecurityListMessages securityListMessages;
	
	public XApplication(
			Map<SessionID, String> session_passwords,
			XSessionActions session_actions,
			XSecurityListMessages security_list_messages)
	{
		this.sessionPasswords = session_passwords;
		this.sessionActions = session_actions;
		this.securityListMessages = security_list_messages;
	}

	@Override
	public void onCreate(SessionID session_id) {
		//logger.debug("create {}", session_id);
	}

	@Override
	public void onLogon(SessionID session_id) {
		// when connection established
		logger.debug("logon {}", session_id);
		sessionActions.onLogon(session_id);
	}

	@Override
	public void onLogout(SessionID session_id) {
		// when connection closed
		logger.debug("logout {}", session_id);
		sessionActions.onLogout(session_id);
	}
	
	@Override
	public void fromAdmin(Message message, SessionID session_id)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon
	{
		MsgType msg_type = new MsgType();
		message.getHeader().getField(msg_type);
		logger.debug("fromAdmin: {}", msg_type);
		
	}
	
	@Override
	public void toAdmin(Message message, SessionID session_id) {
		try {
			MsgType msg_type = new MsgType();
			message.getHeader().getField(msg_type);
			logger.debug("toAdmin: {}", msg_type);
			switch ( msg_type.getValue() ) {
			case MsgType.LOGON:
				String password = sessionPasswords.get(session_id);
				if ( password != null ) {
					message.setField(new Password(password));
					logger.debug("pwd set: {}", session_id);
				} else {
					logger.warn("Logon w/o pwd: {}", session_id);
				}
				break;
			default:
				logger.debug("toAdmin: {}", msg_type.getValue());
				break;
			}
		} catch ( FieldNotFound e ) {
			logger.error("toAdmin error: ", e);
		}
	}

	@Override
	public void fromApp(Message message, SessionID session_id)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType
	{
		MsgType msg_type = new MsgType();
		MsgSeqNum msg_seq_num = new MsgSeqNum();
		message.getHeader().getField(msg_type);
		message.getHeader().getField(msg_seq_num);
		//logger.debug("incoming message: type={} seq_num={}", msg_type.getValue(), msg_seq_num.getValue());

		switch ( msg_type.getValue() ) {
		case MsgType.SECURITY_LIST:
			securityListMessages.response((SecurityList) message);
			break;
		case MsgType.BUSINESS_MESSAGE_REJECT:
			onBMR((BusinessMessageReject) message);
			break;
		}
	}

	@Override
	public void toApp(Message message, SessionID session_id) throws DoNotSend {
		MsgType msg_type = new MsgType();
		MsgSeqNum msg_seq_num = new MsgSeqNum();
		try {
			message.getHeader().getField(msg_type);
			message.getHeader().getField(msg_seq_num);
			logger.debug("outcoming message: type={} seq_num={}", msg_type.getValue(), msg_seq_num.getValue());
			switch ( msg_type.getValue() ) {
			case MsgType.SECURITY_LIST_REQUEST:
				securityListMessages.approve((SecurityListRequest) message);
				break;
			case MsgType.BUSINESS_MESSAGE_REJECT:
			case MsgType.REJECT:
				logger.debug("Rejected: {}", message);
				break;
			}
		} catch ( FieldNotFound e ) {
			logger.error("Malformed message: ", e);
		}
	}
	
	private void onBMR(BusinessMessageReject message) throws
		FieldNotFound,
		IllegalStateException,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		logger.error("BMR: {}", message);
		switch ( message.getRefMsgType().getValue() ) {
		case MsgType.SECURITY_LIST_REQUEST:
			securityListMessages.rejected(message);
			break;
		}
	}
	
}
