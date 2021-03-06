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
import quickfix.fix44.NewOrderSingle;
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
	private final XAccountSummaryMessages accountSummaryMessages;
	private final XOrdersMessages ordersMessages;
	
	public XApplication(
			Map<SessionID, String> session_passwords,
			XSessionActions session_actions,
			XSecurityListMessages security_list_messages,
			XAccountSummaryMessages account_summary_messages,
			XOrdersMessages orders_messages)
	{
		this.sessionPasswords = session_passwords;
		this.sessionActions = session_actions;
		this.securityListMessages = security_list_messages;
		this.accountSummaryMessages = account_summary_messages;
		this.ordersMessages = orders_messages;
	}

	@Override
	public void onCreate(SessionID session_id) {
		//logger.debug("create {}", session_id);
	}

	@Override
	public void onLogon(SessionID session_id) {
		logger.debug("logon {}", session_id);
		try {
			sessionActions.onLogon(session_id);
		} catch ( Exception e ) {
			logger.error("Unexpected exception: ", e);
		}
	}

	@Override
	public void onLogout(SessionID session_id) {
		try {
			logger.debug("logout {}", session_id);
		} catch ( Exception e ) {
			logger.error("Unexpected exception: ", e);
		}
	}
	
	@Override
	public void fromAdmin(Message message, SessionID session_id)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon
	{
		MsgType msg_type = (MsgType) message.getHeader().getField(new MsgType());
		try {
			switch ( msg_type.getValue() ) {
			case MsgType.HEARTBEAT:
				break;
			default:
				logger.debug("fromAdmin (not processed): {}", message);
				break;
			}
		} catch ( Exception e ) {
			logger.error("Unexpected exception: ", e);
		}
	}
	
	@Override
	public void toAdmin(Message message, SessionID session_id) {
		MsgType msg_type = new MsgType();
		try {
			message.getHeader().getField(msg_type);
		} catch ( FieldNotFound e ) {
			logger.error("Malformed message (ours): ", e);
			return;
		}
		
		try {
			switch ( msg_type.getValue() ) {
			case MsgType.LOGON:
				String password = sessionPasswords.get(session_id);
				if ( password != null ) {
					message.setField(new Password(password));
					logger.debug("pwd set: {}", session_id);
				} else {
					logger.warn("Logon w/o pwd: {}", session_id);
				}
				message.setChar(10001, 'Y'); // CancelOnDisconnect
				break;
			case MsgType.HEARTBEAT:
				break;
			default:
				logger.debug("toAdmin (not processed): {}", message);
				break;
			}
		} catch ( Exception e ) {
			logger.error("Unexpected exception: ", e);
		}
	}

	@Override
	public void fromApp(Message message, SessionID session_id)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType
	{
		MsgType msg_type = (MsgType) message.getHeader().getField(new MsgType());
		try {
			switch ( msg_type.getValue() ) {
			case MsgType.SECURITY_LIST:
				//logger.debug("security list response");
				securityListMessages.response((SecurityList) message);
				break;
			case MsgType.EXECUTION_REPORT:
			case MsgType.ORDER_CANCEL_REJECT:
				logger.debug("execution report: {}", message);
				try {
					ordersMessages.response((quickfix.fix44.Message) message);
				} catch ( Throwable e ) {
					logger.error("Unexpected error: ", e);
					logger.debug("Cause by message: {}", message);
				}
				break;
			case XAccountSummaryMessages.MSGTYPE_SUMMARY_RESPONSE:
			case XAccountSummaryMessages.MSGTYPE_SUMMARY_REJECT:
				//logger.debug("account summary response: {}", message);
				accountSummaryMessages.response((quickfix.fix44.Message) message);
				break;
			case MsgType.BUSINESS_MESSAGE_REJECT:
				logger.debug("BMR: {}", message);
				onBMR((BusinessMessageReject) message);
				break;
			default:
				logger.debug("incoming message (ignored): {}", message);
			}
		} catch ( Exception e ) {
			logger.error("Unexpected exception: ", e);
		}
	}

	@Override
	public void toApp(Message message, SessionID session_id) throws DoNotSend {
		MsgType msg_type = new MsgType();
		MsgSeqNum msg_seq_num = new MsgSeqNum();
		try {
			message.getHeader().getField(msg_type);
			message.getHeader().getField(msg_seq_num);
			//logger.debug("outcoming message: type={} seq_num={}", msg_type.getValue(), msg_seq_num.getValue());
		} catch ( FieldNotFound e ) {
			logger.error("Malformed message (ours): ", e);
			return;
		}
		
		try {
			switch ( msg_type.getValue() ) {
			case MsgType.SECURITY_LIST_REQUEST:
				securityListMessages.approve((SecurityListRequest) message);
				break;
			case MsgType.ORDER_SINGLE:
			case MsgType.ORDER_CANCEL_REQUEST:
				ordersMessages.approve((NewOrderSingle) message);
				break;
			case XAccountSummaryMessages.MSGTYPE_SUMMARY_REQUEST:
				accountSummaryMessages.approve((quickfix.fix44.Message) message);
				break;
			case MsgType.BUSINESS_MESSAGE_REJECT:
			case MsgType.REJECT:
				logger.debug("Rejected: {}", message);
				break;
			}
		} catch ( Exception e ) {
			logger.error("Unexpected exception: ", e);
		}
	}
	
	private void onBMR(BusinessMessageReject message) throws
		FieldNotFound,
		IllegalStateException,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		switch ( message.getRefMsgType().getValue() ) {
		case MsgType.SECURITY_LIST_REQUEST:
			securityListMessages.rejected(message);
			break;
		case MsgType.ORDER_SINGLE:
			ordersMessages.rejected(message);
			break;
		case XAccountSummaryMessages.MSGTYPE_SUMMARY_REQUEST:
			accountSummaryMessages.rejected(message);
			break;
		}
	}
	
}
