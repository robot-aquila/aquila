package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.*;

/**
 * Стандартная реализация фабрики элементов бизнес модели.
 * <p>
 * 2012-08-17<br>
 * $Id: BMFactoryImpl.java 522 2013-02-12 12:07:35Z whirlwind $
 */
public class BMFactoryImpl implements BMFactory {
	@SuppressWarnings("unused")
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(BMFactoryImpl.class);
	}
	
	private final EventSystem eventSystem;
	private final EditableTerminal terminal;
	
	/**
	 * Создать фабрику.
	 * <p>
	 * @param eventSystem
	 * @param terminal
	 */
	public BMFactoryImpl(EventSystem eventSystem, EditableTerminal terminal) {
		super();
		if ( eventSystem == null ) {
			throw new NullPointerException("Event system cannot be null");
		}
		this.eventSystem = eventSystem;
		if ( terminal == null ) {
			throw new NullPointerException("Terminal cannot be null");
		}
		this.terminal = terminal;
	}
	
	/**
	 * Получить используемый фасад событийной системы.
	 * <p>
	 * @return фасад системы событий
	 */
	public EventSystem getEventSystem() {
		return eventSystem;
	}
	
	/**
	 * Получить используемый терминал.
	 * <p>
	 * @return терминал
	 */
	public FirePanicEvent getTerminal() {
		return terminal;
	}

	@Override
	public EditableSecurities createSecurities() {
		EventDispatcher dispatcher =
			eventSystem.createEventDispatcher("Securities");
		return new SecuritiesImpl(dispatcher,
				eventSystem.createGenericType(dispatcher, "OnAvailable"),
				eventSystem.createGenericType(dispatcher, "OnChanged"),
				eventSystem.createGenericType(dispatcher, "OnTrade"));
	}

	@Override
	public EditablePortfolios createPortfolios() {
		EventDispatcher dispatcher =
			eventSystem.createEventDispatcher("Portfolios");
		return new PortfoliosImpl(dispatcher,
			eventSystem.createGenericType(dispatcher, "OnAvailable"),
			eventSystem.createGenericType(dispatcher, "OnChanged"),
			eventSystem.createGenericType(dispatcher, "OnPositionAvailable"),
			eventSystem.createGenericType(dispatcher, "OnPositionChanged"));
	}

	@Override
	public EditableOrders createOrders() {
		EventDispatcher dispatcher =
			eventSystem.createEventDispatcher("Orders");
		return new OrdersImpl(dispatcher,
			eventSystem.createGenericType(dispatcher, "OnAvailable"),
			eventSystem.createGenericType(dispatcher, "OnCancelFailed"),
			eventSystem.createGenericType(dispatcher, "OnCancelled"),
			eventSystem.createGenericType(dispatcher, "OnChanged"),
			eventSystem.createGenericType(dispatcher, "OnDone"),
			eventSystem.createGenericType(dispatcher, "OnFailed"),
			eventSystem.createGenericType(dispatcher, "OnFilled"),
			eventSystem.createGenericType(dispatcher, "OnPartiallyFilled"),
			eventSystem.createGenericType(dispatcher, "OnRegistered"),
			eventSystem.createGenericType(dispatcher, "OnRegisterFailed"));
	}

	@Override
	public OrderFactory createOrderFactory() {
		return new OrderFactoryImpl(eventSystem, terminal);
	}

	@Override
	public TradeFactory createTradeFactory() {
		return new TradeFactoryImpl(terminal);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121109, 161555)
			.append(eventSystem)
			.append(terminal)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof BMFactoryImpl ) {
			BMFactoryImpl o = (BMFactoryImpl) other;
			return new EqualsBuilder()
				.append(eventSystem, o.eventSystem)
				.append(terminal, o.terminal)
				.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public S<EditableOrder> createOrderEG() {
		return new EditableEventGenerator<EditableOrder>(
			new FireOrderAvailable(terminal.getOrdersInstance()));
	}

	@Override
	public S<EditableOrder> createOrderEG(Validator isAvailable) {
		return new EditableEventGenerator<EditableOrder>(isAvailable,
				new FireOrderAvailable(terminal.getOrdersInstance()));
	}

	@Override
	public S<EditableOrder> createStopOrderEG() {
		return new EditableEventGenerator<EditableOrder>(
				new FireOrderAvailable(terminal.getStopOrdersInstance()));
	}

	@Override
	public S<EditableOrder> createStopOrderEG(Validator isAvailable) {
		return new EditableEventGenerator<EditableOrder>(isAvailable,
				new FireOrderAvailable(terminal.getStopOrdersInstance()));
	}

	@Override
	public S<EditablePortfolio> createPortfolioEG() {
		return new EditableEventGenerator<EditablePortfolio>(
			new FirePortfolioAvailable(terminal.getPortfoliosInstance()));
	}

	@Override
	public S<EditablePortfolio> createPortfolioEG(Validator isAvailable) {
		return new EditableEventGenerator<EditablePortfolio>(isAvailable,
			new FirePortfolioAvailable(terminal.getPortfoliosInstance()));
	}

	@Override
	public S<EditablePosition> createPositionEG() {
		return new EditableEventGenerator<EditablePosition>(
			new FirePositionAvailableAuto(
				terminal.getPortfoliosInstance()));
	}

	@Override
	public S<EditablePosition> createPositionEG(Validator isAvailable) {
		return new EditableEventGenerator<EditablePosition>(isAvailable,
				new FirePositionAvailableAuto(
					terminal.getPortfoliosInstance()));
	}

	@Override
	public S<EditableSecurity> createSecurityEG() {
		return new EditableEventGenerator<EditableSecurity>(
			new FireSecurityAvailable(terminal.getSecuritiesInstance()));
	}

	@Override
	public S<EditableSecurity> createSecurityEG(Validator isAvailable) {
		return new EditableEventGenerator<EditableSecurity>(isAvailable,
			new FireSecurityAvailable(terminal.getSecuritiesInstance()));
	}

}
