package ru.prolib.aquila.core.BusinessEntities;

import java.util.Hashtable;
import java.util.Map;

/**
 * Known symbol type constants.
 */
public class SymbolType {
	public static final SymbolType UNKNOWN = new SymbolType("U", "Unknown");
	public static final SymbolType STOCK = new SymbolType("S", "Stock");
	public static final SymbolType OPTION = new SymbolType("O", "Option");
	public static final SymbolType FUTURE = new SymbolType("F", "Future");
	public static final SymbolType BOND = new SymbolType("B", "Bond");
	public static final SymbolType CURRENCY = new SymbolType("C", "Currency");
	
	private static final Map<String, SymbolType> codeToType;
	private static final Map<String, Integer> codeToIndex;
	
	static {
		codeToType = new Hashtable<String, SymbolType>();
		codeToIndex = new Hashtable<String, Integer>();
		registerType(UNKNOWN);
		registerType(STOCK);
		registerType(OPTION);
		registerType(FUTURE);
		registerType(BOND);
		registerType(CURRENCY);
	}
	
	private static void registerType(SymbolType type) {
		codeToType.put(type.code, type);
		codeToIndex.put(type.code, codeToIndex.size());
	}
	
	private final String code;
	private final String name;
	
	private SymbolType(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return code;
	}
	
	/**
	 * Get type name.
	 * <p>
	 * @return type name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get type code.
	 * <p>
	 * @return type code
	 */
	public String getCode() {
		return code;
	}
	
	public int ordinal() {
		return codeToIndex.get(code);
	}
	
	public static SymbolType valueOf(String code) {
		return codeToType.get(code);
	}

}
