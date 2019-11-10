package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter.Field;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepositoryImpl;

/**
 * Symbol subscriptions repository.
 */
public class SymbolSubscrRepository extends OSCRepositoryImpl<Symbol, SymbolSubscrCounter> {

	public SymbolSubscrRepository(OSCFactory<Symbol, SymbolSubscrCounter> factory, String repoID) {
		super(factory, repoID);
	}
	
	private SymbolSubscrCounter update(Symbol symbol, MDLevel level, int n) {
		lock();
		try {
			SymbolSubscrCounter counter = super.getOrCreate(symbol);
			int L2 = counter.getInteger(Field.NUM_L2, 0);
			int L1 = counter.getInteger(Field.NUM_L1, 0);
			int L1_BBO = counter.getInteger(Field.NUM_L1_BBO, 0);
			int L0 = counter.getInteger(Field.NUM_L0, 0);
			switch ( level ) {
			case L2:
				L0 += n; L1_BBO += n; L1 += n; L2 += n;
				break;
			case L1:
				L0 += n; L1_BBO += n; L1 += n;
				break;
			case L1_BBO:
				L0 += n; L1_BBO += n;
				break;
			case L0:
				L0 += n;
				break;
			default:
				throw new IllegalArgumentException("Unsupported MD level: " + level);
			}
			counter.consume(new DeltaUpdateBuilder()
					.withToken(Field.SYMBOL, symbol)
					.withToken(Field.NUM_L2, L2)
					.withToken(Field.NUM_L1, L1)
					.withToken(Field.NUM_L1_BBO, L1_BBO)
					.withToken(Field.NUM_L0, L0)
					.buildUpdate());
			return counter;
		} finally {
			unlock();
		}
	}
	
	/**
	 * Increment counters and return current state.
	 * <p>
	 * @param symbol - symbol of counter
	 * @param level - market data level
	 * @return counter instance
	 */
	public SymbolSubscrCounter subscribe(Symbol symbol, MDLevel level) {
		return update(symbol, level, 1);
	}
	
	/**
	 * Decrement counters and return current state.
	 * <p>
	 * @param symbol - symbol of counter
	 * @param level - market data level
	 * @return counter instance
	 */
	public SymbolSubscrCounter unsubscribe(Symbol symbol, MDLevel level) {
		return update(symbol, level, -1);
	}
	
	@Override
	public SymbolSubscrCounter getOrCreate(Symbol key) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean remove(Symbol key) {
		throw new UnsupportedOperationException();
	}

}
