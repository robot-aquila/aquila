package ru.prolib.aquila.core.BusinessEntities.row;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.data.row.RowElement;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorStub;

/**
 * Конструктор стандартных модификаторов.
 * <p>
 * Создает стандартные модификаторы атрибутов объектов модели на основании ряда
 * типа {@link ru.prolib.aquila.core.data.row.Row Row}.
 * <p>
 * 2013-02-13<br>
 * $Id$
 */
public class Modifiers {
	private final SetterFactory sf = new SetterFactoryImpl();
	private final EditableTerminal terminal;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 */
	public Modifiers(EditableTerminal terminal) {
		super();
		this.terminal = terminal;
	}
	
	/**
	 * Создать стандартный модификатор анонимной сделки.
	 * <p>
	 * Создает модификатор анонимной сделки типа
	 * {@link ru.prolib.aquila.core.BusinessEntities.Trade Trade} на основании
	 * ряда типа {@link Row}. Ожидает следующие элементы ряда:
	 * {@link Spec#TRADE_DIR}, {@link Spec#TRADE_ID}, {@link Spec#TRADE_PRICE},
	 * {@link Spec#TRADE_QTY}, {@link Spec#TRADE_SECDESCR},
	 * {@link Spec#TRADE_TIME}, {@link Spec#TRADE_VOL}. Если какой-либо из
	 * указанных элементов ряда не определен, то модификация соответствующего
	 * атрибута сделки не выполняется. 
	 * <p>
	 * @return модификатор
	 */
	public S<Trade> createTradeModifier() {
		return new MListImpl<Trade>()
			.add(re(Spec.TRADE_DIR, OrderDirection.class),
					sf.tradeSetDirection())
			.add(re(Spec.TRADE_ID, Long.class), sf.tradeSetId())
			.add(re(Spec.TRADE_PRICE, Double.class), sf.tradeSetPrice())
			.add(re(Spec.TRADE_QTY, Long.class), sf.tradeSetQty())
			.add(re(Spec.TRADE_SECDESCR, SecurityDescriptor.class),
					sf.tradeSetSecurityDescr())
			.add(re(Spec.TRADE_TIME, Date.class), sf.tradeSetTime())
			.add(re(Spec.TRADE_VOL, Double.class), sf.tradeSetVolume());
	}
	
	/**
	 * Создать стандартный модификатор портфеля.
	 * <p>
	 * Создает модификатор портфеля типа {@link
	 * ru.prolib.aquila.core.BusinessEntities.EditablePortfolio
	 * EditablePortfolio} на основании ряда типа {@link Row}. Ожидает следующие
	 * элементы ряда: {@link Spec#PORT_BALANCE}, {@link Spec#PORT_CASH},
	 * {@link Spec#PORT_VMARGIN}. Если какой-либо из указанных элементов ряда
	 * не определен, то модификация соответствующего атрибута портфеля не
	 * выполняется. Помимо непосредственно изменения атрибутов портфеля,
	 * полученный модификатор содержит генератор стандартных событий потфеля.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePortfolio> createPortfolioModifier() {
		return createPortfolioModifier(new ValidatorStub(true));
	}
	
	/**
	 * Создать стандартный модификатор портфеля.
	 * <p>
	 * Работает аналогично {@link #createPortfolioModifier()}, только вместо
	 * валидатора доступности объекта по умолчанию использует указанный
	 * валидатор. Валидатор доступности позволяет определить условие, при
	 * котором объект портфеля должен рассматриваться как доступный для
	 * потребителей.
	 * <p>
	 * @param isAvailable валидатор доступности объекта
	 * @return модификатор
	 */
	public S<EditablePortfolio> createPortfolioModifier(Validator isAvailable) {
		return new MListImpl<EditablePortfolio>()
			.add(re(Spec.PORT_BALANCE, Double.class), sf.portfolioSetBalance())
			.add(re(Spec.PORT_CASH, Double.class), sf.portfolioSetCash())
			.add(re(Spec.PORT_VMARGIN, Double.class),sf.portfolioSetVarMargin())
			.add(new EditableEventGenerator<EditablePortfolio>(isAvailable,
					new FirePortfolioAvailable(terminal)));
	}
	
	/**
	 * Создать стандартный модификатор позиции.
	 * <p>
	 * Создает модификатор портфеля типа {@link
	 * ru.prolib.aquila.core.BusinessEntities.EditablePosition EditablePosition}
	 * на основании ряда типа {@link Row}. Ожидает следующие элементы ряда:
	 * {@link Spec#POS_BOOKVAL}, {@link Spec#POS_MARKETVAL},
	 * {@link Spec#POS_CURR}, {@link Spec#POS_LOCK}, {@link Spec#POS_OPEN},
	 * {@link Spec#POS_VMARGIN}. Если какой-либо из указанных элементов ряда
	 * не определен, то модификация соответствующего атрибута позиции не
	 * выполняется. Помимо непосредственно изменения атрибутов позиции,
	 * полученный модификатор содержит генератор стандартных событий позиции.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePosition> createPositionModifier() {
		return new MListImpl<EditablePosition>()
			.add(re(Spec.POS_BOOKVAL, Double.class), sf.positionSetBookValue())
			.add(re(Spec.POS_MARKETVAL, Double.class),
					sf.positionSetMarketValue())
			.add(re(Spec.POS_CURR, Long.class), sf.positionSetCurrQty())
			.add(re(Spec.POS_LOCK, Long.class), sf.positionSetLockQty())
			.add(re(Spec.POS_OPEN, Long.class), sf.positionSetOpenQty())
			.add(re(Spec.POS_VMARGIN, Double.class), sf.positionSetVarMargin())
			.add(new EditableEventGenerator<EditablePosition>(
					new FirePositionAvailableAuto(terminal)));
	}
	
	/**
	 * Создает общий для всех типов заявок набор модификаторов.
	 * <p> 
	 * @return набор модификаторов
	 */
	private MList<EditableOrder> createCommonOrderModifiers() {
		return new MListImpl<EditableOrder>()
			.add(re(Spec.ORD_ACCOUNT, Account.class), sf.orderSetAccount())
			.add(re(Spec.ORD_DIR, OrderDirection.class), sf.orderSetDirection())
			.add(re(Spec.ORD_ID, Long.class), sf.orderSetId())
			.add(re(Spec.ORD_PRICE, Double.class), sf.orderSetPrice())
			.add(re(Spec.ORD_QTY, Long.class), sf.orderSetQty())
			.add(re(Spec.ORD_SECDESCR, SecurityDescriptor.class),
					sf.orderSetSecurityDescriptor())
			.add(re(Spec.ORD_STATUS, OrderStatus.class), sf.orderSetStatus())
			.add(re(Spec.ORD_TRANSID, Long.class), sf.orderSetTransactionId())
			.add(re(Spec.ORD_TYPE, OrderType.class), sf.orderSetType())
			.add(re(Spec.ORD_TIME, Date.class), sf.orderSetTime())
			.add(re(Spec.ORD_CHNGTIME,Date.class), sf.orderSetLastChangeTime());
	}
	
	/**
	 * Создать стандартный модификатор заявки типа {@link
	 * ru.prolib.aquila.core.BusinessEntities.EditableOrder EditableOrder}
	 * на основании ряда типа {@link Row}.
	 * <p>
	 * Ожидает следующие элементы ряда:
	 * {@link Spec#ORD_ACCOUNT}, {@link Spec#ORD_DIR}, {@link Spec#ORD_ID},
	 * {@link Spec#ORD_PRICE}, {@link Spec#ORD_QTY}, {@link Spec#ORD_SECDESCR},
	 * {@link Spec#ORD_STATUS}, {@link Spec#ORD_TRANSID}, {@link Spec#ORD_TYPE},
	 * {@link Spec#ORD_QTYREST}, {@link Spec#ORD_EXECVOL},
	 * {@link Spec#ORD_TIME}, {@link Spec#ORD_CHNGTIME}.
	 * Если какой-либо из указанных элементов ряда не определен, то модификация
	 * соответствующего атрибута заявки не выполняется. Помимо непосредственно
	 * изменения атрибутов заявки. модификатор содержит генератор стандартных
	 * событий заявки.
	 * <p>
	 * @return модификатор
	 */
	public S<EditableOrder> createOrderModifier() {
		MList<EditableOrder> list = createCommonOrderModifiers();
		list.add(re(Spec.ORD_QTYREST, Long.class), sf.orderSetQtyRest());
		list.add(re(Spec.ORD_EXECVOL,Double.class),sf.orderSetExecutedVolume());
		list.add(new EditableEventGenerator<EditableOrder>(
				new FireOrderAvailable(terminal.getOrdersInstance())));
		return list;
	}
	
	/**
	 * Создать стандартный модификатор стоп-заявки типа {@link
	 * ru.prolib.aquila.core.BusinessEntities.EditableOrder EditableOrder}
	 * на основании ряда типа {@link Row}.
	 * <p>
	 * Ожидает следующие элементы ряда:
	 * {@link Spec#ORD_ACCOUNT}, {@link Spec#ORD_DIR}, {@link Spec#ORD_ID},
	 * {@link Spec#ORD_PRICE}, {@link Spec#ORD_QTY}, {@link Spec#ORD_SECDESCR},
	 * {@link Spec#ORD_STATUS}, {@link Spec#ORD_TRANSID}, {@link Spec#ORD_TYPE},
	 * {@link Spec#ORD_LINKID}, {@link Spec#ORD_OFFSET},
	 * {@link Spec#ORD_SPREAD}, {@link Spec#ORD_STOPLMT},
	 * {@link Spec#ORD_TAKEPFT}, {@link Spec#ORD_TIME},
	 * {@link Spec#ORD_CHNGTIME}.
	 * Если какой-либо из указанных элементов ряда не определен, то модификация
	 * соответствующего атрибута заявки не выполняется. Помимо непосредственно
	 * изменения атрибутов заявки, модификатор содержит генератор стандартных
	 * событий заявки.
	 * <p>
	 * @return модификатор
	 */
	public S<EditableOrder> createStopOrderModifier() {
		MList<EditableOrder> list = createCommonOrderModifiers();
		list.add(re(Spec.ORD_LINKID, Long.class), sf.orderSetLinkedOrderId());
		list.add(re(Spec.ORD_OFFSET, Price.class), sf.orderSetOffset());
		list.add(re(Spec.ORD_SPREAD, Price.class), sf.orderSetSpread());
		list.add(re(Spec.ORD_STOPLMT,Double.class),sf.orderSetStopLimitPrice());
		list.add(re(Spec.ORD_TAKEPFT,Double.class),sf.orderSetTakeProfitPrice());
		list.add(new EditableEventGenerator<EditableOrder>(
				new FireOrderAvailable(terminal.getStopOrdersInstance())));
		return list;
	}
	
	/**
	 * Создать стандартный модификатор инструмента типа {@link
	 * ru.prolib.aquila.core.BusinessEntities.EditableSecurity EditableSecurity}
	 * на основании ряда типа {@link Row}.
	 * <p>
	 * Ожидает следующие элементы ряда:
	 * {@link Spec#SEC_ASKPR}, {@link Spec#SEC_ASKSZ}, {@link Spec#SEC_BIDPR},
	 * {@link Spec#SEC_BIDSZ}, {@link Spec#SEC_CLOSE},
	 * {@link Spec#SEC_DISPNAME}, {@link Spec#SEC_HIGH}, {@link Spec#SEC_LAST},
	 * {@link Spec#SEC_LOTSZ}, {@link Spec#SEC_LOW}, {@link Spec#SEC_MAXPR},
	 * {@link Spec#SEC_MINPR}, {@link Spec#SEC_MINSTEPPR},
	 * {@link Spec#SEC_MINSTEPSZ}, {@link Spec#SEC_OPEN}, {@link Spec#SEC_PREC},
	 * {@link Spec#SEC_STATUS}.
	 * Если какой-либо из указанных элементов ряда неопределен, то модификация
	 * соответствующего атрибута инструмента не выполняется. Помимо
	 * непосредственно изменения атрибутов инструмента, модификатор содержит
	 * генератор стандартных событий инструмента.
	 * <p>
	 * @return модификатор
	 */
	public S<EditableSecurity> createSecurityModifier() {
		return new MListImpl<EditableSecurity>()
			.add(re(Spec.SEC_ASKPR, Double.class), sf.securitySetAskPrice())
			.add(re(Spec.SEC_ASKSZ, Long.class), sf.securitySetAskSize())
			.add(re(Spec.SEC_BIDPR, Double.class), sf.securitySetBidPrice())
			.add(re(Spec.SEC_BIDSZ, Long.class), sf.securitySetBidSize())
			.add(re(Spec.SEC_CLOSE, Double.class), sf.securitySetClosePrice())
			.add(re(Spec.SEC_DISPNAME,String.class),sf.securitySetDisplayName())
			.add(re(Spec.SEC_HIGH, Double.class), sf.securitySetHighPrice())
			.add(re(Spec.SEC_LAST, Double.class), sf.securitySetLastPrice())
			.add(re(Spec.SEC_LOTSZ, Integer.class), sf.securitySetLotSize())
			.add(re(Spec.SEC_LOW, Double.class), sf.securitySetLowPrice())
			.add(re(Spec.SEC_MAXPR, Double.class), sf.securitySetMaxPrice())
			.add(re(Spec.SEC_MINPR, Double.class), sf.securitySetMinPrice())
			.add(re(Spec.SEC_MINSTEPPR, Double.class),
					sf.securitySetMinStepPrice())
			.add(re(Spec.SEC_MINSTEPSZ, Double.class),
					sf.securitySetMinStepSize())
			.add(re(Spec.SEC_OPEN, Double.class), sf.securitySetOpenPrice())
			.add(re(Spec.SEC_PREC, Integer.class), sf.securitySetPrecision())
			.add(re(Spec.SEC_STATUS, SecurityStatus.class),
					sf.securitySetStatus())
			.add(new EditableEventGenerator<EditableSecurity>(
					new FireSecurityAvailable(terminal)));
	}
	
	/**
	 * Ярлык для создания типового геттера элемента ряда.
	 * <p>
	 * @param elementId идентификатор элемента ряда
	 * @param elementClass ожидаемый класс элемента
	 * @return геттер
	 */
	private final RowElement re(String elementId, Class<?> elementClass) {
		return new RowElement(elementId, elementClass);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == Modifiers.class ) {
			Modifiers o = (Modifiers) other;
			return new EqualsBuilder()
				.append(terminal, o.terminal)
				.isEquals();
		} else {
			return false;
		}
	}

}
