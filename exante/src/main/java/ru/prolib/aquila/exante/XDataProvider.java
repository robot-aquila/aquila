package ru.prolib.aquila.exante;

import quickfix.ConfigError;
import quickfix.RuntimeError;
import quickfix.SessionID;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.exante.rh.AccountSummaryTestHandler;
import ru.prolib.aquila.exante.rh.SecurityListHandler;

public class XDataProvider implements DataProvider {
	private final XServiceLocator serviceLocator;
	
	public XDataProvider(XServiceLocator service_locator) {
		this.serviceLocator = service_locator;
	}
	
	public XServiceLocator getServiceLocator() {
		return serviceLocator;
	}

	@Override
	public long getNextOrderID() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void subscribeRemoteObjects(EditableTerminal terminal) {
		SessionID broker_session_id = serviceLocator.getBrokerSessionID();
		serviceLocator.getSessionActions().addLogonAction(broker_session_id, new XLogonAction() {
			@Override
			public boolean onLogon(SessionID session_id) {
				serviceLocator.getSecurityListMessages().list(new SecurityListHandler(terminal));
				serviceLocator.getAccountSummaryMessages().query(new AccountSummaryTestHandler());
				return true;
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
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancelOrder(EditableOrder order) throws OrderException {
		throw new UnsupportedOperationException();
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