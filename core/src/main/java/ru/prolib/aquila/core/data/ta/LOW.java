package ru.prolib.aquila.core.data.ta;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.*;

/**
 * Объектно-ориентированная обертка функции {@link TAMath#min(Series, int, int)}.
 */
public class LOW implements Series<CDecimal> {
	private final String id;
	private final Series<CDecimal> source;
	private final int period;
	private final TAMath math;

	public LOW(String id, Series<Candle> source, int period, TAMath math) {
		this.id = id;
		this.source = new CandleLowSeries(source);
		this.period = period;
		this.math = math;
	}

	public LOW(String id, Series<Candle> source, int period) {
		this(id, source, period, TAMath.getInstance());
	}

	public LOW(Series<Candle> source, int period) {
		this(DEFAULT_ID, source, period);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public CDecimal get() throws ValueException {
		return get(getLength() - 1);
	}

	@Override
	public CDecimal get(int index) throws ValueException {
		if(index< period){
			return null;
		}
		return math.min(source, index, period);
	}

	@Override
	public int getLength() {
		return source.getLength();
	}

	@Override
	public LID getLID() {
		return source.getLID();
	}

	@Override
	public void lock() {
		source.lock();
	}

	@Override
	public void unlock() {
		source.unlock();
	}

}
