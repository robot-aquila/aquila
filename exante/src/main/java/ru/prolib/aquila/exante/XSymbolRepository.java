package ru.prolib.aquila.exante;

import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class XSymbolRepository {
	private final Map<String, XSymbol> sec_id_to_desc;
	private final Map<Symbol, String> sym_to_sec_id;
	private final Map<String, Symbol> sec_id_to_sym;
	
	XSymbolRepository(
			Map<String, XSymbol> sec_id_to_desc,
			Map<Symbol, String> sym_to_sec_id,
			Map<String, Symbol> sec_id_to_sym
		)
	{
		this.sec_id_to_desc = sec_id_to_desc;
		this.sym_to_sec_id = sym_to_sec_id;
		this.sec_id_to_sym = sec_id_to_sym;
	}
	
	public XSymbolRepository() {
		this(new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	
	public synchronized void register(Symbol symbol, XSymbol desc) {
		String sec_id = desc.getSecurityID();
		sec_id_to_desc.put(sec_id, desc);
		sym_to_sec_id.put(symbol, sec_id);
		sec_id_to_sym.put(sec_id, symbol);
	}
	
	public synchronized Symbol getSymbol(String security_id) {
		Symbol symbol = sec_id_to_sym.get(security_id);
		if ( symbol == null ) {
			throw new IllegalArgumentException("Security ID not found: " + security_id);
		}
		return symbol;
	}
	
	public synchronized String getSecurityID(Symbol symbol) {
		String security_id = sym_to_sec_id.get(symbol);
		if ( security_id == null ) {
			throw new IllegalArgumentException("Symbol not found: " + symbol);
		}
		return security_id;
	}

	public synchronized XSymbol getBySecurityID(String security_id) {
		XSymbol desc = sec_id_to_desc.get(security_id);
		if ( desc == null ) {
			throw new IllegalArgumentException("Security ID not found: " + security_id);
		}
		return desc;
	}

	public synchronized XSymbol getBySymbol(Symbol symbol) {
		return getBySecurityID(getSecurityID(symbol));
	}
	
}
