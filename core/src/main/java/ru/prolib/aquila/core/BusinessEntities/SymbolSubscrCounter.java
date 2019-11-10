package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;

/**
 * Symbol subscription counter - an entity to count listeners
 * of different levels of market data for the symbol.
 */
public class SymbolSubscrCounter extends ObservableStateContainerImpl {
	
	public static class Field {
		public static final int SYMBOL		= 9001;
		public static final int NUM_L0		= 9002;
		public static final int NUM_L1_BBO	= 9003;
		public static final int NUM_L1		= 9004;
		public static final int NUM_L2		= 9005;
	}

	public SymbolSubscrCounter(OSCParams params) {
		super(params);
	}
	
	public Symbol getSymbol() {
		return (Symbol) getObject(Field.SYMBOL);
	}

	public int getNumL0() {
		return getInteger(Field.NUM_L0);
	}
	
	public int getNumL1_BBO() {
		return getInteger(Field.NUM_L1_BBO);
	}
	
	public int getNumL1() {
		return getInteger(Field.NUM_L1);
	}
	
	public int getNumL2() {
		return getInteger(Field.NUM_L2);
	}

}
