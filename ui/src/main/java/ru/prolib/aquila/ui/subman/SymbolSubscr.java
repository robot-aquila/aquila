package ru.prolib.aquila.ui.subman;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;

public class SymbolSubscr extends ObservableStateContainerImpl {
	public static final int ID			= 11011;
	public static final int TERM_ID		= 11012;
	public static final int SYMBOL		= 11013;
	public static final int MD_LEVEL	= 11014;

	public SymbolSubscr(OSCParams params) {
		super(params);
	}
	
	public int getID() {
		return getInteger(ID);
	}
	
	public String getTerminalID() {
		return getString(TERM_ID);
	}
	
	public Symbol getSymbol() {
		return (Symbol) getObject(SYMBOL);
	}
	
	public MDLevel getLevel() {
		return (MDLevel) getObject(MD_LEVEL);
	}

}
