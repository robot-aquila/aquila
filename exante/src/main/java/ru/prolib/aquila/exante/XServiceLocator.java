package ru.prolib.aquila.exante;

import quickfix.Initiator;
import quickfix.SessionID;

public class XServiceLocator {
	private final SessionID brokerSessionID;
	private final XSessionActions sessionActions;
	private final XSecurityListMessages securityListMessages;
	private final Initiator brokerInitiator;
	
	public XServiceLocator(
			SessionID broker_session_id,
			XSessionActions session_actions,
			XSecurityListMessages security_list_messages,
			Initiator broker_initiator
		)
	{
		this.brokerSessionID = broker_session_id;
		this.sessionActions = session_actions;
		this.securityListMessages = security_list_messages;
		this.brokerInitiator = broker_initiator;
	}

	public SessionID getBrokerSessionID() {
		return brokerSessionID;
	}
	
	public XSessionActions getSessionActions() {
		return sessionActions;
	}
	
	public XSecurityListMessages getSecurityListMessages() {
		return securityListMessages;
	}
	
	public Initiator getBrokerInitiator() {
		return brokerInitiator;
	}

}
