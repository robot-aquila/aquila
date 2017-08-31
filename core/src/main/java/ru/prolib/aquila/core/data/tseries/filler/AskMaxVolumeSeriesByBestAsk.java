package ru.prolib.aquila.core.data.tseries.filler;

import java.time.Instant;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.EditableTSeries;

public class AskMaxVolumeSeriesByBestAsk extends FillBySecurityEvent<Long> {
	
	public AskMaxVolumeSeriesByBestAsk(EditableTSeries<Long> series,
			Terminal terminal, Symbol symbol)
	{
		super(series, terminal, symbol);
	}

	@Override
	protected void processEvent(Event event) {
		if ( event.isType(security.onBestAsk()) ) {
			Tick tick = ((SecurityTickEvent) event).getTick();
			Instant time = tick.getTime();
			long newVal = tick.getSize();
			Long curVal = series.get(time);
			if ( curVal == null || newVal > curVal ) {
				series.set(time, newVal);
			}
		}
	}

	@Override
	protected void stopListening(Security security) {
		security.onBestAsk().removeListener(this);
	}

	@Override
	protected void startListening(Security security) {
		security.onBestAsk().addListener(this);
	}

}
