package ru.prolib.aquila.data.replay;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleListener;
import ru.prolib.aquila.core.data.EditableTSeries;

public class CandleReplayToSeries implements CandleListener {
	private final EditableTSeries<Candle> target;
	
	public CandleReplayToSeries(EditableTSeries<Candle> target) {
		this.target = target;
	}
	
	public EditableTSeries<Candle> getTargetSeries() {
		return target;
	}

	@Override
	public void onCandle(Instant time, Symbol symbol, Candle candle) {
		target.set(candle.getStartTime(), candle);
	}

}
