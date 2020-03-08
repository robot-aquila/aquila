package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;

public interface CandleProvider {
	SubscrHandler subscribe(TFSymbol key, CandleListener listener);
}
