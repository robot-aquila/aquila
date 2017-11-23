package ru.prolib.aquila.core.data.ta;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Объектно-ориентированная обертка функции {@link TAMath#qema(Series, int, int)}.
 */
public class QEMA implements Series<CDecimal> {
	private final String id;
	private final Series<CDecimal> source;
	private final int period;
	private final TAMath math;
	
	public QEMA(String id, Series<CDecimal> source, int period, TAMath math) {
		this.id = id;
		this.source = source;
		this.period = period;
		this.math = math;
	}
	
	public QEMA(String id, Series<CDecimal> source, int period) {
		this(id, source, period, TAMath.getInstance());
	}
	
	public QEMA(Series<CDecimal> source, int period) {
		this(DEFAULT_ID, source, period);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public CDecimal get() throws ValueException {
		return math.qema(source, getLength() - 1, period);
	}

	@Override
	public CDecimal get(int index) throws ValueException {
		return math.qema(source, index, period);
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
