package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueImpl;
import ru.prolib.aquila.ta.ValueUpdateException;

/**
 * Экспоненциальная скользящая средняя.
 * 
 * Рассчитывается по формуле:
 * 
 *   K = 2 / (N + 1)
 *   ЕМА = Р_тек * К + ЕМА_пред * (1 - К)
 *   
 * где 
 * ЕМА - экспоненциальное скользящее среднее;
 * N - период усреднения;
 * Р_тек - текущее значение источника (например цена);
 * ЕМА_пред - предыдущее значение ЕМА.
 * 
 * Если история отсутствует, то устанавливается EMA = P_тек  
 */
@Deprecated
public class Ema extends ValueImpl<Double> {
	private final ru.prolib.aquila.ta.indicator.Ema source;
	
	public Ema(Value<Double> iValue, int periods) {
		this(iValue, periods, ValueImpl.DEFAULT_ID);
	}
	
	public Ema(Value<Double> iValue, int periods, String id) {
		super(id);
		this.source = new ru.prolib.aquila.ta.indicator.Ema(iValue, periods);
	}
	
	public int getPeriods() {
		return source.getPeriod();
	}
	
	public Value<Double> getSourceValue() {
		return source.getSource();
	}

	@Override
	public synchronized void update() throws ValueUpdateException {
		try {
			add(source.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
