package ru.prolib.aquila.ui.subman;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;

public class SSDescFactory implements OSCFactory<Integer, SSDesc> {
	private final EventQueue queue;
	
	public SSDescFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public SSDesc produce(OSCRepository<Integer, SSDesc> owner, Integer key) {
		return new SSDesc(new OSCParamsBuilder(queue)
				.withID("SSDesc#" + key)
				.buildParams()
			);
	}

}
