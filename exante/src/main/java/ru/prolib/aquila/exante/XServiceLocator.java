package ru.prolib.aquila.exante;

import quickfix.Initiator;
import quickfix.SessionID;
import ru.prolib.aquila.data.Sequence;

public class XServiceLocator {
	private final XParams params;
	private final XSymbolRepository symbolRepository;
	private final SessionID brokerSessionID;
	private final XSessionActions sessionActions;
	private final XSecurityListMessages securityListMessages;
	private final XAccountSummaryMessages accountSummaryMessages;
	private final XOrdersMessages ordersMessages;
	private final XAccountService accountService;
	private final Initiator brokerInitiator;
	
	public XServiceLocator(
			XParams params,
			XSymbolRepository symbol_repository,
			SessionID broker_session_id,
			XSessionActions session_actions,
			XSecurityListMessages security_list_messages,
			XAccountSummaryMessages account_summary_messages,
			XOrdersMessages orders_messages,
			XAccountService account_service,
			Initiator broker_initiator
		)
	{
		this.params = params;
		this.symbolRepository = symbol_repository;
		this.brokerSessionID = broker_session_id;
		this.sessionActions = session_actions;
		this.securityListMessages = security_list_messages;
		this.accountSummaryMessages = account_summary_messages;
		this.ordersMessages = orders_messages;
		this.accountService = account_service;
		this.brokerInitiator = broker_initiator;
	}
	
	public Sequence getOrderIDSequence() {
		return params.getOrderIDSequence();
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
	
	public XOrdersMessages getOrdersMessages() {
		return ordersMessages;
	}
	
	public XAccountService getAccountService() {
		return accountService;
	}
	
	public Initiator getBrokerInitiator() {
		return brokerInitiator;
	}

}
