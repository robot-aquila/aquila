package ru.prolib.aquila.core.data.ta;

import ru.prolib.aquila.core.data.*;

/**
 * Объектно-ориентированная обертка функции {@link TAMath#qema(Series, int, int)}.
 */
public class HIGH implements Series<Double> {
	private final String id;
	private final Series<Double> source;
	private final int period;
	private final TAMath math;

	public HIGH(String id, Series<Candle> source, int period, TAMath math) {
		this.id = id;
		this.source = new CandleHighSeries(source);
		this.period = period;
		this.math = math;
	}

	public HIGH(String id, Series<Candle> source, int period) {
		this(id, source, period, TAMath.getInstance());
	}

	public HIGH(Series<Candle> source, int period) {
		this(DEFAULT_ID, source, period);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Double get() throws ValueException {
		return math.max(source, getLength() - 2, period);
	}

	@Override
	public Double get(int index) throws ValueException {
		return math.max(source, index-1, period);
	}

	@Override
	public int getLength() {
		return source.getLength();
	}

}
