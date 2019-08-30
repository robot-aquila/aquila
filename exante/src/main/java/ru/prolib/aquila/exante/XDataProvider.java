package ru.prolib.aquila.exante;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.ConfigError;
import quickfix.RuntimeError;
import quickfix.SessionID;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.exante.rh.AccountSummaryHandler;
import ru.prolib.aquila.exante.rh.OrderHandler;
import ru.prolib.aquila.exante.rh.SecurityListHandler;

public class XDataProvider implements DataProvider {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(XDataProvider.class);
	}
	
	private final XServiceLocator serviceLocator;
	
	public XDataProvider(XServiceLocator service_locator) {
		this.serviceLocator = service_locator;
	}
	
	public XServiceLocator getServiceLocator() {
		return serviceLocator;
	}

	@Override
	public long getNextOrderID() {
		return serviceLocator.getOrderIDSequence().next();
	}

	@Override
	public void subscribeRemoteObjects(EditableTerminal terminal) {
		SessionID broker_session_id = serviceLocator.getBrokerSessionID();
		XSymbolRepository symbols = serviceLocator.getSymbolRepository();
		serviceLocator.getSessionActions().addLogonAction(broker_session_id, new XLogonAction() {
			@Override
			public boolean onLogon(SessionID session_id) {
				serviceLocator.getSecurityListMessages()
					.list(new SecurityListHandler(terminal, symbols) {
						@Override
						public void close() {
							super.close();
							serviceLocator.getAccountSummaryMessages()
								.query(new AccountSummaryHandler(terminal, symbols));
						}
					});
				return false;
			}
		});
		try {
			serviceLocator.getBrokerInitiator().start();
		} catch ( RuntimeError|ConfigError e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {
		serviceLocator.getBrokerInitiator().stop();
	}

	@Override
	public void registerNewOrder(EditableOrder order) throws OrderException {
		serviceLocator.getOrdersMessages().newOrderSingle(order, new OrderHandler(order));
	}

	@Override
	public void cancelOrder(EditableOrder order) throws OrderException {
		logger.debug("cancelOrder not implemented yet");
	}

	@Override
	public void subscribe(Symbol symbol, EditableTerminal terminal) {
		
	}

	@Override
	public void unsubscribe(Symbol symbol, EditableTerminal terminal) {
		
	}

	@Override
	public void subscribe(Account account, EditableTerminal terminal) {
		
	}

	@Override
	public void unsubscribe(Account account, EditableTerminal terminal) {
		
	}

}
