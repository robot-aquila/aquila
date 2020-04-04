package ru.prolib.aquila.core.BusinessEntities;

import java.util.HashMap;
import java.util.Map;

/**
 * Order type.
 * <p>
 * 2012-05-30<br>
 * $Id: OrderType.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderType {
	private static final Map<String, OrderType> types = new HashMap<>();
	
	public synchronized static OrderType registerType(String code) {
		if ( types.containsKey(code) ) {
			throw new IllegalArgumentException("Type already exists: " + code);
		}
		OrderType type = new OrderType(code);
		types.put(code, type);
		return type;
	}
	
	public synchronized static OrderType registerType(OrderType type) {
		String code = type.getCode();
		if ( types.containsKey(code) ) {
			throw new IllegalArgumentException("Type already exists: " + code);
		}
		types.put(code, type);
		return type;
	}
	
	public synchronized static OrderType byCode(String code) {
		OrderType type = types.get(code);
		if ( type == null ) {
			throw new IllegalArgumentException("Type not exists: " + code);
		}
		return type;
	}
	
	/**
	 * Limit order.
	 */
	public static final OrderType LMT = registerType("LMT");
	/**
	 * Market order.
	 */
	public static final OrderType MKT = registerType("MKT");
	
	/**
	 * Immediate or cancel.
	 * <p>
	 * This is not an order type and will be removed in next releases.
	 */
	@Deprecated
	public static final OrderType IOC = registerType("IOC");
	
	/**
	 * Fill or kill.
	 * <p>
	 * This is not an order type and will be removed in next releases.
	 */
	@Deprecated
	public static final OrderType FOK = registerType("FOK");
	
	private final String code;
	
	/**
	 * Создать тип заявки.
	 * <p>
	 * @param code код типа
	 */
	protected OrderType(String code) {
		super();
		this.code = code;
	}
	
	@Override
	public String toString() {
		return code;
	}
	
	public String getCode() {
		return code;
	}

}
