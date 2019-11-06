package ru.prolib.aquila.core.BusinessEntities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TerminalRegistry {
	private final Map<String, Terminal> map;
	private String defaultID;
	
	public TerminalRegistry(Map<String, Terminal> map) {
		this.map = map;
	}
	
	public TerminalRegistry() {
		this(new LinkedHashMap<>());
	}
	
	public synchronized void register(Terminal terminal) throws IllegalArgumentException {
		String term_id = terminal.getTerminalID();
		if ( map.containsKey(term_id) ) {
			throw new IllegalArgumentException("Terminal already registered: " + term_id);
		}
		map.put(term_id, terminal);
		if ( defaultID == null ) {
			defaultID = term_id;
		}
	}
	
	public synchronized Terminal get(String terminal_id) throws IllegalArgumentException {
		Terminal terminal = map.get(terminal_id);
		if ( terminal == null ) {
			throw new IllegalArgumentException("Terminal is not registered: " + terminal_id);
		}
		return terminal;
	}
	
	public synchronized List<String> getListIDs() {
		return new ArrayList<>(map.keySet());
	}
	
	public synchronized Terminal getDefault() {
		if ( defaultID == null ) {
			throw new IllegalStateException("No terminal available");
		}
		return get(defaultID);
	}
	
	public synchronized void setDefaultID(String terminal_id) {
		if ( ! map.containsKey(terminal_id) ) {
			throw new IllegalArgumentException("Terminal is not registered: " + terminal_id);
		}
		defaultID = terminal_id;
	}

}
