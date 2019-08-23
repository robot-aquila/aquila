package ru.prolib.aquila.exante;

import quickfix.Initiator;
import quickfix.SessionID;

public class XServiceLocator {
	private final XSymbolRepository symbolRepository;
	private final SessionID brokerSessionID;
	private final XSessionActions sessionActions;
	private final XSecurityListMessages securityListMessages;
	private final XAccountSummaryMessages accountSummaryMessages;
	private final Initiator brokerInitiator;
	
	public XServiceLocator(
			XSymbolRepository symbol_repository,
			SessionID broker_session_id,
			XSessionActions session_actions,
			XSecurityListMessages security_list_messages,
			XAccountSummaryMessages account_summary_messages,
			Initiator broker_initiator
		)
	{
		this.symbolRepository = symbol_repository;
		this.brokerSessionID = broker_session_id;
		this.sessionActions = session_actions;
		this.securityListMessages = security_list_messages;
		this.accountSummaryMessages = account_summary_messages;
		this.brokerInitiator = broker_initiator;
	}
	
	public XSymbolRepository getSymbolRepository() {
		return symbolRepository;
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
	
	public XAccountSummaryMessages getAccountSummaryMessages() {
		return accountSummaryMessages;
	}
	
	public Initiator getBrokerInitiator() {
		return brokerInitiator;
	}

}
