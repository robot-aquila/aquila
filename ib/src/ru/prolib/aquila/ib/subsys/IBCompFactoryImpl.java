package ru.prolib.aquila.ib.subsys;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ib.client.EClientSocket;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.ib.subsys.account.IBIsPortfolioAvailable;
import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.api.IBClientImpl;
import ru.prolib.aquila.ib.subsys.api.IBRequestFactory;
import ru.prolib.aquila.ib.subsys.api.IBRequestFactoryImpl;
import ru.prolib.aquila.ib.subsys.api.IBWrapper;
import ru.prolib.aquila.ib.subsys.contract.IBContractUtilsImpl;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;
import ru.prolib.aquila.ib.subsys.contract.IBContractsImpl;
import ru.prolib.aquila.ib.subsys.contract.IBContractsStorageImpl;
import ru.prolib.aquila.ib.subsys.order.IBIsEventOpenOrder;

/**
 * Фабрика компонентов.
 * <p>
 * 2012-11-19<br>
 * $Id: IBCompFactoryImpl.java 553 2013-03-01 13:37:31Z whirlwind $
 */
public class IBCompFactoryImpl implements IBCompFactory {
	private final IBServiceLocator locator;
	private final BMFactory bfactory;
	private final IBModifierFactory mfactory;
	
	/**
	 * Конструктор (для теста).
	 * <p>
	 * @param locator локатор сервисов
	 * @param bfactory фабрика бизнес-модели
	 * @param mfactory фабрика модификаторов
	 */
	public IBCompFactoryImpl(IBServiceLocator locator, BMFactory bfactory,
			IBModifierFactory mfactory)
	{
		super();
		this.locator = locator;
		this.bfactory = bfactory;
		this.mfactory = mfactory;
	}

	/**
	 * Конструктор.
	 * <p>
	 * @param locator локатор сервисов
	 */
	public IBCompFactoryImpl(IBServiceLocator locator) {
		this(locator,
			new BMFactoryImpl(locator.getEventSystem(), locator.getTerminal()),
			new IBModifierFactoryImpl(locator));
	}
	
	/**
	 * Получить локатор сервисов.
	 * <p>
	 * @return локатор
	 */
	public IBServiceLocator getServiceLocator() {
		return locator;
	}
	
	/**
	 * Получить фабрику бизнес модели.
	 * <p>
	 * @return фабрика
	 */
	public BMFactory getBusinessModelFactory() {
		return bfactory;
	}
	
	/**
	 * Получить фабрику модификаторов.
	 * <p>
	 * @return фабрика модификаторов
	 */
	public IBModifierFactory getModifierFactory() {
		return mfactory;
	}
	
	@Override
	public OrderFactory createOrderFactory() {
		return bfactory.createOrderFactory();
	}

	@Override
	public EditableOrders createOrders() {
		return bfactory.createOrders();
	}

	@Override
	public PortfolioFactory createPortfolioFactory() {
		return bfactory.createPortfolioFactory();
	}

	@Override
	public EditablePortfolios createPortfolios() {
		return bfactory.createPortfolios();
	}

	@Override
	public PositionFactory createPositionFactory(Account account) {
		return bfactory.createPositionFactory(account);
	}

	@Override
	public EditableSecurities createSecurities(String defaultCurrency,
			SecurityType defaultType)
	{
		return bfactory.createSecurities(defaultCurrency, defaultType);
	}

	@Override
	public SecurityFactory createSecurityFactory() {
		return bfactory.createSecurityFactory();
	}

	@Override
	public TradeFactory createTradeFactory() {
		return bfactory.createTradeFactory();
	}

	@Override
	public IBClient createClient() {
		EventSystem eSys = locator.getApiEventSystem();
		EventDispatcher dispatcher = eSys.createEventDispatcher();
		IBWrapper wrapper = new IBWrapper(dispatcher,
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher)); 
		return new IBClientImpl(new EClientSocket(wrapper), wrapper,
				dispatcher,
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher));
	}

	@Override
	public IBRequestFactory createRequestFactory() {
		return new IBRequestFactoryImpl(locator.getEventSystem(), locator);
	}

	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == IBCompFactoryImpl.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}
	
	protected boolean fieldsEquals(Object other) {
		IBCompFactoryImpl o = (IBCompFactoryImpl) other;
		return new EqualsBuilder()
			.append(locator, o.locator)
			.append(bfactory, o.bfactory)
			.append(mfactory, o.mfactory)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121207, 55233)
			.append(locator)
			.append(bfactory)
			.append(mfactory)
			.toHashCode();
	}

	@Override
	public OrderBuilder createOrderBuilder() {
		return bfactory.createOrderBuilder();
	}

	@Override
	public OrderBuilder createOrderBuilder(Counter transId) {
		return bfactory.createOrderBuilder(transId);
	}

	@Override
	public OrderBuilder
			createOrderBuilder(Counter transId, OrderFactory factory)
	{
		return bfactory.createOrderBuilder(transId, factory);
	}

	private S<EditableOrder> mOpenOrder() {
		return new MListImpl<EditableOrder>()
			.add(mfactory.orderOoAccount())
			.add(mfactory.orderOoDir())
			.add(mfactory.orderOoQty())
			.add(mfactory.orderOoSecurityDescr())
			.add(mfactory.orderOoStatus())
			.add(mfactory.orderOoType())
			.add(bfactory.createOrderEG());
	}

	private S<EditableOrder> mOrderStatus() {
		return new MListImpl<EditableOrder>()
			.add(mfactory.orderOsStatus())
			.add(mfactory.orderOsQtyRest())
			.add(mfactory.orderOsExecutedVolume())
			.add(mfactory.orderOsAvgExecutedPrice())
			.add(bfactory.createOrderEG());
	}
	
	@Override
	public S<EditableOrder> mOrder() {
		return new SSwitch<EditableOrder>(
				new IBIsEventOpenOrder(), mOpenOrder(), mOrderStatus());
	}
	
	@Override
	public S<EditableSecurity> mSecurity() {
		return new MListImpl<EditableSecurity>()
			.add(mfactory.securityContract())
			.add(mfactory.securityTick())
			.add(bfactory.createSecurityEG());
	}

	@Override
	public S<EditablePortfolio> mPortfolio() {
		return new MListImpl<EditablePortfolio>()
			.add(mfactory.portCash())
			.add(mfactory.portBalance())
			.add(bfactory.createPortfolioEG(new IBIsPortfolioAvailable()));
	}

	@Override
	public S<EditablePosition> mPosition() {
		return new MListImpl<EditablePosition>()
			.add(mfactory.posCurrQty())
			.add(mfactory.posMarketValue())
			.add(mfactory.posBookValue())
			.add(mfactory.posVarMargin())
			.add(bfactory.createPositionEG());
	}

	@Override
	public IBContracts createContracts() {
		return new IBContractsImpl(new IBContractsStorageImpl(locator,
				locator.getEventSystem().createEventDispatcher()),
				new IBContractUtilsImpl());
	}

	@Override
	public S<EditableOrder> createOrderEG() {
		return bfactory.createOrderEG();
	}

	@Override
	public S<EditableOrder> createOrderEG(Validator arg0) {
		return bfactory.createOrderEG(arg0);
	}

	@Override
	public S<EditablePortfolio> createPortfolioEG() {
		return bfactory.createPortfolioEG();
	}

	@Override
	public S<EditablePortfolio> createPortfolioEG(Validator arg0) {
		return bfactory.createPortfolioEG(arg0);
	}

	@Override
	public S<EditablePosition> createPositionEG() {
		return bfactory.createPositionEG();
	}

	@Override
	public S<EditablePosition> createPositionEG(Validator arg0) {
		return bfactory.createPositionEG(arg0);
	}

	@Override
	public S<EditableSecurity> createSecurityEG() {
		return bfactory.createSecurityEG();
	}

	@Override
	public S<EditableSecurity> createSecurityEG(Validator arg0) {
		return bfactory.createSecurityEG(arg0);
	}

	@Override
	public S<EditableOrder> createStopOrderEG() {
		return bfactory.createStopOrderEG();
	}

	@Override
	public S<EditableOrder> createStopOrderEG(Validator arg0) {
		return bfactory.createStopOrderEG(arg0);
	}
	
}
