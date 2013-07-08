package ru.prolib.aquila.core.BusinessEntities;

/**
 * Тип заявки.
 * <p>
 * 2012-05-30<br>
 * $Id: OrderType.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderType {
	/**
	 * Лимитная.
	 */
	public static final OrderType LIMIT = new OrderType("Limit");
	/**
	 * Рыночная.
	 */
	public static final OrderType MARKET = new OrderType("Market");
	/**
	 * Стоп-лимит.
	 * @deprecated Директива 20130706
	 */
	public static final OrderType STOP_LIMIT = new OrderType("StopLimit");
	/**
	 * Тэйк-профит.
	 * @deprecated Директива 20130706
	 */
	public static final OrderType TAKE_PROFIT = new OrderType("TakeProfit");
	/**
	 * Тэйк-профит и стоп-лимит.
	 * @deprecated Директива 20130706
	 */
	public static final OrderType TPSL = new OrderType("TakeProfit&StopLimit");
	/**
	 * Тэйк-профит и стоп-лимит.
	 * @deprecated Директива 20130706
	 */
	public static final OrderType TAKE_PROFIT_AND_STOP_LIMIT = TPSL;
	/**
	 * Заявка иного (явно не поддерживаемого) типа.
	 * @deprecated Директива 20130706
	 */
	public static final OrderType OTHER = new OrderType("Other");
	
	private final String code;
	
	/**
	 * Создать тип заявки.
	 * <p>
	 * @param code код типа
	 */
	private OrderType(String code) {
		super();
		this.code = code;
	}
	
	@Override
	public String toString() {
		return code;
	}

}
