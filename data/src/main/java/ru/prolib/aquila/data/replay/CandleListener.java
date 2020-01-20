package ru.prolib.aquila.data.replay;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;

public interface CandleListener {
	void onCandle(Instant time, Symbol symbol, Candle candle);
}
