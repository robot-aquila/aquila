package ru.prolib.aquila.qforts.impl;

import java.util.concurrent.atomic.AtomicLong;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class QForts {
	public static final int LIQUIDITY_LIMITED = 0;
	public static final int LIQUIDITY_APPLY_TO_ORDER = 1;
	public static final int LIQUIDITY_UNLIMITED = 2;
	private static final CDecimal ZERO = CDecimalBD.ZERO;
	private static final CDecimal MAX_LIQUIDITY = CDecimalBD.of(Long.MAX_VALUE);
	private final IQFObjectRegistry registry;
	private final QFTransactionService transactions;
	private final int liquidityMode;
	
	public QForts(IQFObjectRegistry registry, QFTransactionService transactions, int liquidity_mode) {
		this.registry = registry;
		this.transactions = transactions;
		this.liquidityMode = liquidity_mode;
	}
	
	public QForts(IQFObjectRegistry registry, QFTransactionService transactions) {
		this(registry, transactions, LIQUIDITY_LIMITED);
	}
	
	public QForts(IQFObjectRegistry registry, AtomicLong seqExecutionID, int unlimited_liquidity) {
		this(registry, new QFTransactionService(registry, seqExecutionID), unlimited_liquidity);
	}
	
	public int getLiquidityMode() {
		return liquidityMode;
	}
	
	public void registerPortfolio(EditablePortfolio portfolio) {
		registry.register(portfolio);
	}
	
	@Deprecated
	public void registerSecurity(EditableSecurity security) {
		registry.register(security);
	}
	
	public void registerOrder(EditableOrder order) throws QFTransactionException {
		transactions.registerOrder(order);
	}
	
	public void cancelOrder(EditableOrder order) throws QFTransactionException {
		transactions.cancelOrder(order);
	}
	
	public void changeBalance(EditablePortfolio portfolio, CDecimal value)
			throws QFTransactionException
	{
		transactions.changeBalance(portfolio, value);
	}
	
	public void handleOrders(Security security, CDecimal availableVolume, CDecimal price)
			throws QFTransactionException
	{
		if ( liquidityMode == LIQUIDITY_UNLIMITED ) {
			availableVolume = MAX_LIQUIDITY;
		}
		for ( EditableOrder order : registry.getOrderList(security.getSymbol(), price) ) {
			CDecimal volume = availableVolume.min(order.getCurrentVolume());
			if ( volume.compareTo(ZERO) > 0 ) {
				try {
					transactions.executeOrder(order, volume, price);
				} catch ( QFValidationException e ) {
					transactions.rejectOrder(order, e.getMessage());
				}
			}
			if ( liquidityMode == 0 ) {
				availableVolume = availableVolume.subtract(volume);
				if ( availableVolume.compareTo(ZERO) <= 0 ) {
					break;
				}
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
