package ru.prolib.aquila.core.BusinessEntities;

import java.util.ArrayList;
import java.util.List;

public class L1UpdateConsumerStub implements L1UpdateConsumer {
	private List<L1Update> consumed = new ArrayList<>();

	@Override
	public void consume(L1Update update) {
		consumed.add(update);
	}
	
	public boolean isConsumed(L1Update update) {
		return consumed.contains(update);
	}

}
