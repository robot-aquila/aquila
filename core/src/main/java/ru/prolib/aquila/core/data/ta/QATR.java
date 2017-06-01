package ru.prolib.aquila.core.data.ta;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Объектно-ориентированная обертка функции {@link TAMath#qema(Series, int, int)}.
 */
public class QATR implements Series<Double> {
	private final String id;
	private final Series<Candle> source;
	private final int period;
	private final TAMath math;

	public QATR(String id, Series<Candle> source, int period, TAMath math) {
		this.id = id;
		this.source = source;
		this.period = period;
		this.math = math;
	}

	public QATR(String id, Series<Candle> source, int period) {
		this(id, source, period, TAMath.getInstance());
	}

	public QATR(Series<Candle> source, int period) {
		this(DEFAULT_ID, source, period);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Double get() throws ValueException {
		return math.qatr(source, getLength() - 1, period);
	}

	@Override
	public Double get(int index) throws ValueException {
		return math.qatr(source, index, period);
	}

	@Override
	public int getLength() {
		return source.getLength();
	}

}
