package ru.prolib.aquila.data.replay;

import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.data.TFSymbol;

public interface CandleReplayService {
	SubscrHandler subscribe(TFSymbol key, CandleListener listener);
}
