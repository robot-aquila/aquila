package ru.prolib.aquila.quik.subsys;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.row.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.dde.utils.table.*;
import ru.prolib.aquila.quik.QUIKConfig;
import ru.prolib.aquila.quik.subsys.row.*;

/**
 * Реализация фабрики обозревателей таблиц QUIK.
 * <p>
 * TODO: выпилить, после полного перехода на DDE-кэш
 * <p>
 * 2012-09-08<br>
 * $Id$
 */
@Deprecated
public class QUIKListenerFactoryImpl implements QUIKListenerFactory {
	public static final int VERSION = 0x04;
	private final QUIKServiceLocator locator;
	private final Handlers rowHandlers;
	private final RowSetBuilderFactory rowSetBuilders;
	
	public QUIKListenerFactoryImpl(QUIKServiceLocator locator,
			Handlers rowHandlers, RowSetBuilderFactory rowSetBuilders)
	{
		super();
		this.locator = locator;
		this.rowHandlers = rowHandlers;
		this.rowSetBuilders = rowSetBuilders;
	}
	
	public QUIKListenerFactoryImpl(QUIKServiceLocator locator) {
		this(locator, new Handlers(locator.getEventSystem(),
				locator.getTerminal(), new Modifiers(locator.getTerminal())),
			new RowSetBuilderFactory(locator, new RowAdapters(locator)));
	}
	
	public QUIKServiceLocator getServiceLocator() {
		return locator;
	}
	
	public Handlers getRowHandlers() {
		return rowHandlers;
	}
	
	public RowSetBuilderFactory getRowSetBuilders() {
		return rowSetBuilders;
	}
	
	@Override
	public DDETableListener listenAllDeals() {
		return new DDETableListener(locator.getConfig().getAllDeals(),
			new DDETableHandlerImpl(
				rowSetBuilders.createAllDealsRowSetBuilder(),
				rowHandlers.createTradeHandler()));
	}

	@Override
	public DDETableListener listenSecurities() {
		return new DDETableListener(locator.getConfig().getSecurities(),
			new DDETableHandlerImpl(
				rowSetBuilders.createSecurityRowSetBuilder(),
				rowHandlers.createSecurityHandler()));
	}

	@Override
	public DDETableListener listenPortfoliosSTK() {
		return new DDETableListener(locator.getConfig().getPortfoliosSTK(),
			new DDETableHandlerImpl(
				rowSetBuilders.createPortfolioStkRowSetBuilder(),
				rowHandlers.createPortfolioHandler()));
	}

	@Override
	public DDETableListener listenPortfoliosFUT() {
		return new DDETableListener(locator.getConfig().getPortfoliosFUT(),
			new DDETableHandlerImpl(
				rowSetBuilders.createPortfolioFutRowSetBuilder(),
				rowHandlers.createPortfolioHandler()));
	}

	@Override
	public DDETableListener listenPositionsFUT() {
		return new DDETableListener(locator.getConfig().getPositionsFUT(),
			new DDETableHandlerImpl(
				rowSetBuilders.createPositionFutRowSetBuilder(),
				rowHandlers.createPositionHandler()));
	}

	@Override
	public DDETableListener listenPositionsSTK() {
		return new DDETableListener(locator.getConfig().getPositionsSTK(),
			new DDETableHandlerImpl(
				rowSetBuilders.createPositionStkRowSetBuilder(),
				rowHandlers.createPositionHandler()));
	}

	@Override
	public DDETableListener listenOrders() {
		return new DDETableListener(locator.getConfig().getOrders(),
			new DDETableHandlerImpl(
				rowSetBuilders.createOrderRowSetBuilder(),
				rowHandlers.createOrderHandler()));
	}

	@Override
	public DDETableListener listenStopOrders() {
		return new DDETableListener(locator.getConfig().getStopOrders(),
			new DDETableHandlerImpl(
				rowSetBuilders.createStopOrderRowSetBuilder(),
				rowHandlers.createStopOrderHandler()));
	}

	@Override
	public Dependencies<String> createDependencies() {
		QUIKConfig config = locator.getConfig();
		if ( config.getAllDeals() == null || config.getOrders() == null
				|| config.getPortfoliosFUT() == null
				|| config.getPortfoliosSTK() == null
				|| config.getPositionsFUT() == null
				|| config.getPositionsSTK() == null
				|| config.getSecurities() == null
				|| config.getStopOrders() == null )
		{
			throw new NullPointerException();
		}
		return new Deps<String>()
			.setDependency(config.getAllDeals(), config.getSecurities())
			.setDependency(config.getPositionsFUT(), config.getSecurities())
			//.setDependency(config.getPositionsSTK(), config.getSecurities())
			.setDependency(config.getPositionsFUT(), config.getPortfoliosFUT())
			//.setDependency(config.getPositionsSTK(), config.getPortfoliosSTK())
			.setDependency(config.getOrders(), config.getSecurities())
			.setDependency(config.getOrders(), config.getPositionsFUT())
			//.setDependency(config.getOrders(), config.getPositionsSTK())
			.setDependency(config.getStopOrders(), config.getSecurities())
			.setDependency(config.getStopOrders(), config.getPositionsFUT())
			//.setDependency(config.getStopOrders(), config.getPositionsSTK())
			;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null
		  && other.getClass() == QUIKListenerFactoryImpl.class )
		{
			QUIKListenerFactoryImpl o = (QUIKListenerFactoryImpl) other;
			return new EqualsBuilder()
				.append(locator, o.locator)
				.append(rowHandlers, o.rowHandlers)
				.append(rowSetBuilders, o.rowSetBuilders)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121111, 110347)
			.append(locator)
			.append(rowHandlers)
			.append(rowSetBuilders)
			.toHashCode();
	}

}
