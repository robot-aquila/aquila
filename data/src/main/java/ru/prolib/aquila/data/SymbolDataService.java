package ru.prolib.aquila.data;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface SymbolDataService {
	SubscrHandler onSubscribe(Symbol symbol, MDLevel level);
	void onConnectionStatusChange(boolean connected);
}
