package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;

public class SymbolSubscrCounterFactory implements OSCFactory<Symbol, SymbolSubscrCounter> {
	private final EventQueue queue;
	
	public SymbolSubscrCounterFactory(EventQueue queue) {
		this.queue = queue;
	}
	
	public EventQueue getEventQueue() {
		return queue;
	}

	@Override
	public SymbolSubscrCounter produce(OSCRepository<Symbol, SymbolSubscrCounter> owner, Symbol key) {
		return new SymbolSubscrCounter(new OSCParamsBuilder(queue)
				.withID("SymbolSubscrCounter#" + key)
				.buildParams()
			);
	}

}
