package ru.prolib.aquila.core.data;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface CandleListener {
	void onCandle(Instant time, Symbol symbol, Candle candle);
}
