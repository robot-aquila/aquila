package ru.prolib.aquila.ib.getter;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.getter.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;

/**
 * Фабрика геттеров.
 * <p>
 * 2012-12-15<br>
 * $Id: IBGetterFactoryImpl.java 553 2013-03-01 13:37:31Z whirlwind $
 */
public class IBGetterFactoryImpl implements IBGetterFactory {
	private static final String DEFAULT_CURRENCY = "BASE";
	private static final String BALANCE = "NetLiquidationByCurrency";
	private static final String CASH = "TotalCashBalance";
	private static final Map<String, OrderDirection> mOrderDir;
	private static final Map<String, OrderType> mOrderType;
	private static final Map<String, OrderStatus> mOrderStatus;
	
	static {
		mOrderDir = new Hashtable<String, OrderDirection>();
		mOrderDir.put("BUY", OrderDirection.BUY);
		mOrderDir.put("SELL", OrderDirection.SELL);
		mOrderDir.put("SSHORT", OrderDirection.SELL);
		
		mOrderType = new Hashtable<String, OrderType>();
		mOrderType.put("LMT", OrderType.LIMIT);
		mOrderType.put("MKT", OrderType.MARKET);
		mOrderType.put("STP LMT", OrderType.STOP_LIMIT);
		
		mOrderStatus = new Hashtable<String, OrderStatus>();
		// Статусы PendingCancel, Inactive не обрабатываются
		mOrderStatus.put("PendingSubmit", OrderStatus.PENDING);
		mOrderStatus.put("PreSubmitted", OrderStatus.PENDING);
		mOrderStatus.put("Submitted", OrderStatus.ACTIVE);
		mOrderStatus.put("Cancelled", OrderStatus.CANCELLED);
		mOrderStatus.put("Filled", OrderStatus.FILLED);
	}
	
	private final IBServiceLocator locator;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param locator локатор сервисов
	 */
	public IBGetterFactoryImpl(IBServiceLocator locator) {
		super();
		this.locator = locator;
	}
	
	/**
	 * Получить локатор сервисов.
	 * <p>
	 * @return локатор
	 */
	public IBServiceLocator getServiceLocator() {
		return locator;
	}

	@Override
	public G<Account> openOrderAccount() {
		return new GChain<Account>(
				new IBGetOpenOrder(),
				new GAccount(new IBGetOrderAccount()));
	}

	@Override
	public G<SecurityDescriptor> openOrderSecDescr() {
		return new GChain<SecurityDescriptor>(
				new IBGetOpenOrderContract(),
				new IBGetSecurityDescriptor(locator.getContracts()));
	}

	@Override
	public G<OrderType> openOrderType() {
		return new GMapTR<OrderType>(
				new GChain<String>(
						new IBGetOpenOrder(),
						new IBGetOrderType()),
				mOrderType);
	}

	@Override
	public G<OrderDirection> openOrderDir() {
		return new GMapTR<OrderDirection>(
				new GChain<String>(
						new IBGetOpenOrder(),
						new IBGetOrderDir()),
				mOrderDir);
	}

	@Override
	public G<Long> openOrderQty() {
		return new GChain<Long>(
				new IBGetOpenOrder(),
				new IBGetOrderQty());
	}

	@Override
	public G<OrderStatus> openOrderStatus() {
		return new GMapTR<OrderStatus>(
				new GChain<String>(
						new IBGetOpenOrderState(),
						new IBGetOrderStateStatus()),
				mOrderStatus);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121217, 183657)
			.append(locator)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBGetterFactoryImpl.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBGetterFactoryImpl o = (IBGetterFactoryImpl) other;
		return new EqualsBuilder()
			.append(locator, o.locator)
			.isEquals();
	}

	@Override
	public G<OrderStatus> orderStatusStatus() {
		return new GMapTR<OrderStatus>(new IBGetOrderStatus(), mOrderStatus);
	}

	@Override
	public G<Long> orderStatusRemaining() {
		return new IBGetOrderRestQty();
	}

	@Override
	public G<Double> portCash() {
		return new IBGetAccountDouble(CASH, DEFAULT_CURRENCY);
	}

	@Override
	public G<Long> posCurrValue() {
		return new IBGetPositionCurrent();
	}

	@Override
	public G<Double> orderStatusExecutedVolume() {
		return new IBGetOrderExecVolume();
	}

	@Override
	public G<Double> portBalance() {
		return new IBGetAccountDouble(BALANCE, DEFAULT_CURRENCY);
	}

	@Override
	public G<Double> posMarketValue() {
		return new IBGetPositionMktValue();
	}

	@Override
	public G<Double> posBalanceCost() {
		return new IBGetPositionBalanceCost();
	}

	@Override
	public G<Double> posPL() {
		return new IBGetPositionVarMargin();
	}

	@Override
	public G<Double> orderStatusAvgExecutedPrice() {
		return new IBGetOrderAvgExecPrice();
	}

}
