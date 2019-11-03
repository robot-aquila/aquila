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
import ru.prolib.aquila.exante.rh.OrderCancelHandler;
import ru.prolib.aquila.exante.rh.OrderHandler;
import ru.prolib.aquila.exante.rh.SecurityListHandler;

public class XDataProvider implements DataProvider {
	private final XServiceLocator serviceLocator;
	private boolean firstTimeCall = true;
	
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
		if ( firstTimeCall ) {
			firstTimeCall = false;
			serviceLocator.getSessionActions().addLogonAction(broker_session_id, new XLogonAction() {
				@Override
				public boolean onLogon(SessionID session_id) {
					serviceLocator.getSecurityListMessages()
						.list(new SecurityListHandler(terminal, symbols) {
							@Override
							public void close() {
								super.close();
								serviceLocator.getAccountService().start(terminal);
							}
						});
					return false;
				}
			});
		}
		try {
			serviceLocator.getBrokerInitiator().start();
		} catch ( RuntimeError|ConfigError e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {
		serviceLocator.getAccountService().stop();
		serviceLocator.getBrokerInitiator().stop();
	}

	@Override
	public void registerNewOrder(EditableOrder order) throws OrderException {
		serviceLocator.getOrdersMessages()
			.newOrderSingle(order, new OrderHandler(order, serviceLocator.getAccountService()));
	}

	@Override
	public void cancelOrder(EditableOrder order) throws OrderException {
		serviceLocator.getOrdersMessages()
			.cancelOrder(order, new OrderCancelHandler(order));
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

	@Override
	public void close() {
		
	}

}
