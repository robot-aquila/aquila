package ru.prolib.aquila.ui.subman;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;

public class SymbolSubscrFactory implements OSCFactory<Integer, SymbolSubscr> {
	private final EventQueue queue;
	
	public SymbolSubscrFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public SymbolSubscr produce(OSCRepository<Integer, SymbolSubscr> owner, Integer key) {
		return new SymbolSubscr(new OSCParamsBuilder(queue)
				.withID("SymbolSubscr#" + key)
				.buildParams()
			);
	}

}
