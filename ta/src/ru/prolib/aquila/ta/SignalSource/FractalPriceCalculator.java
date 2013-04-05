package ru.prolib.aquila.ta.SignalSource;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ta.*;

/**
 * Рассчет цены сигнала на основе фрактала.
 * В качестве основы должен использоваться тот же бар, на котором сформировался
 * фрактал.
 */
public class FractalPriceCalculator extends OffsetPriceCalculator {
	private final int periods;
	private final int center;
	
	public FractalPriceCalculator(Value<Double> src, Asset asset, double mul,
								  int periods)
	{
		super(src, asset, mul);
		this.periods = periods;
		center = periods / 2 + 1;
	}
	
	/**
	 * Получить размер фрактала в периодах.
	 * @return
	 */
	public int getPeriods() {
		return periods;
	}

	@Override
	protected double getValue() throws ValueException {
		return src.get(-center + 1);
	}

}
