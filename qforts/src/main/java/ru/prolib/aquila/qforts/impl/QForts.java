package ru.prolib.aquila.qforts.impl;

import java.util.concurrent.atomic.AtomicLong;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class QForts {
	private final QFObjectRegistry registry;
	private final QFTransactionService transactions;
	
	public QForts(QFObjectRegistry registry, QFTransactionService transactions) {
		this.registry = registry;
		this.transactions = transactions;
	}
	
	public QForts(QFObjectRegistry registry, AtomicLong seqExecutionID) {
		this(registry, new QFTransactionService(registry, seqExecutionID));
	}
	
	public void registerPortfolio(EditablePortfolio portfolio) {
		registry.register(portfolio);
	}
	
	public void registerSecurity(EditableSecurity security) {
		registry.register(security);
	}
	
	public void registerOrder(EditableOrder order) throws QFTransactionException {
		transactions.registerOrder(order);
	}
	
	public void cancelOrder(EditableOrder order) throws QFTransactionException {
		transactions.cancelOrder(order);
	}
	
	public void changeBalance(EditablePortfolio portfolio, FMoney value)
			throws QFTransactionException
	{
		transactions.changeBalance(portfolio, value);
	}
	
	public void handleOrders(Security security, long availableVolume, FDecimal price)
			throws QFTransactionException
	{
		for ( EditableOrder order : registry.getOrderList(security.getSymbol(), price) ) {
			long volume = Math.min(availableVolume, order.getCurrentVolume());
			if ( volume > 0L ) {
				try {
					transactions.executeOrder(order, volume, price);
				} catch ( QFValidationException e ) {
					transactions.rejectOrder(order, e.getMessage());
				}
			}
			availableVolume -= volume;
			if ( availableVolume <= 0L ) {
				break;
			}
		}
	}
	
	public void updateByMarket() throws QFTransactionException {
		for ( EditablePortfolio portfolio : registry.getPortfolioList() ) {
			transactions.updateByMarket(portfolio);
		}
	}
	
	public void updateMargin(Security security) throws QFTransactionException {
		final Symbol symbol = security.getSymbol();
		for ( EditablePortfolio portfolio : registry.getPortfolioList() ) {
			EditablePosition position = null;
			portfolio.lock();
			try {
				if ( portfolio.isPositionExists(symbol) ) {
					position = portfolio.getEditablePosition(symbol);
				}
			} finally {
				portfolio.unlock();
			}
			if ( position != null ) {
				transactions.updateMargin(position);
			}
		}
	}
	
	public void midClearing() throws QFTransactionException {
		for ( EditablePortfolio portfolio : registry.getPortfolioList() ) {
			transactions.midClearing(portfolio);
		}
	}
	
	public void clearing() throws QFTransactionException {
		for ( EditablePortfolio portfolio : registry.getPortfolioList() ) {
			transactions.clearing(portfolio);
		}
	}

}
