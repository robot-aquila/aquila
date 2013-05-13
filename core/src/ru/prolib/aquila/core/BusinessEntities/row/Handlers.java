package ru.prolib.aquila.core.BusinessEntities.row;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.Validator;

/**
 * Конструктор стандартных обработчиков ряда.
 * <p>
 * 2013-02-14<br>
 * $Id$
 */
public class Handlers {
	private final EventSystem es;
	private final EditableTerminal terminal;
	private final Modifiers modifiers;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param modifiers конструктор модификаторов
	 */
	public Handlers(EventSystem es, EditableTerminal terminal,
			Modifiers modifiers)
	{
		super();
		this.es = es;
		this.terminal = terminal;
		this.modifiers = modifiers;
	}
	
	/**
	 * Получить фасад событийной системы.
	 * <p>
	 * @return система событий
	 */
	public EventSystem getEventSystem() {
		return es;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить конструктор модификаторов.
	 * <p>
	 * @return конструктор модификаторов
	 */
	public Modifiers getModifiers() {
		return modifiers;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == Handlers.class ) {
			Handlers o = (Handlers) other;
			return new EqualsBuilder()
				.append(es, o.es)
				.append(terminal, o.terminal)
				.append(modifiers, o.modifiers)
				.isEquals();
		} else {
			return false;
		}
	}
	
	/**
	 * Создать обработчик для ряда сделки.
	 * <p>
	 * Требования к составу ряда см.
	 * {@link Modifiers#createTradeModifier()}.
	 * <p>
	 * @return обработчик ряда
	 */
	public RowHandler createTradeHandler() {
		return new TradeRowHandler(terminal,
				new TradeFactoryImpl(terminal), 
				modifiers.createTradeModifier());
	}
	
	/**
	 * Создать обработчик ряда для портфеля.
	 * <p>
	 * Требования к составу ряда см.
	 * {@link Modifiers#createPortfolioModifier()} + {@link Spec#PORT_ACCOUNT}.
	 * <p>
	 * @return обработчик ряда
	 */
	public RowHandler createPortfolioHandler() {
		return new PortfolioRowHandler(terminal,
				modifiers.createPortfolioModifier());
	}
	
	/**
	 * Создать обработчик ряда для портфеля.
	 * <p>
	 * Работает аналогично {@link #createPortfolioHandler()}, но позволяет
	 * указать специфический валидатор доступности портфеля.
	 * <p>
	 * @return обработчик ряда
	 */
	public RowHandler createPortfolioHandler(Validator isAvailable) {
		return new PortfolioRowHandler(terminal,
				modifiers.createPortfolioModifier(isAvailable));
	}
	
	/**
	 * Создать обработчик ряда для позиции.
	 * <p>
	 * Требования к составу ряда см. {@link Modifiers#createPositionModifier()}
	 * + {@link Spec#POS_ACCOUNT} и {@link Spec#POS_SECDESCR}.
	 * <p>
	 * @return обработчик ряда
	 */
	public RowHandler createPositionHandler() {
		return new PositionRowHandler(terminal,
				modifiers.createPositionModifier());
	}
	
	/**
	 * Создать обработчик ряда для заявки.
	 * <p>
	 * Требования к составу ряда см. {@link Modifiers#createOrderModifier()}.
	 * <p>
	 * @return обработчик ряда
	 */
	public RowHandler createOrderHandler() {
		return new OrderRowHandler(terminal,
			new OrderResolverStd(terminal.getOrdersInstance(),
				new OrderFactoryImpl(es, terminal)),
			modifiers.createOrderModifier());
	}

	/**
	 * Создать обработчик ряда для стоп-заявки.
	 * <p>
	 * Требования к составу ряда см.
	 * {@link Modifiers#createStopOrderModifier()}.
	 * <p>
	 * @return обработчик ряда
	 */
	public RowHandler createStopOrderHandler() {
		return new OrderRowHandler(terminal,
			new OrderResolverStd(terminal.getStopOrdersInstance(),
				new OrderFactoryImpl(es, terminal)),
			modifiers.createStopOrderModifier());
	}
	
	/**
	 * Создать обработчик ряда инструмента.
	 * <p>
	 * Требования к составу ряда см. {@link Modifiers#createSecurityModifier()}
	 * + {@link Spec#SEC_DESCR}.
	 * <p>
	 * @return обработчик ряда
	 */
	public RowHandler createSecurityHandler() {
		return new SecurityRowHandler(terminal,
				modifiers.createSecurityModifier());
	}

}
