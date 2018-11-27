package ru.prolib.aquila.probe.datasim.l1;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.L1Update;

public interface L1UpdateConsumerEx {

	public void consume(List<L1Update> updates, int sequenceID);
	
}
