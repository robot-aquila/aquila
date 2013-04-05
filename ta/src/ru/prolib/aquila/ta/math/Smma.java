package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.*;

/**
 * Сглаженная скользящая средняя.
 * 
 * До тех пор, пока история источника содержит значений в количестве менее или
 * равном количеству установленных периодов, текущее значение индикатора
 * рассчитывается как SMA.
 * 
 * Реализация по методу:
 * 
 * http://20minutetraders.com/learn/moving-averages/smoothed-moving-average-calculation
 * 
 * Копирую на всякий случай 
 *
 * The first value for a Smoothed Moving Average is calculated as a Simple
 * Moving Average (SMA):
 *
 * SUM1 = SUM(CLOSE, N)
 * SMMA1 = SUM1/N
 * 
 * The second and succeeding moving averages are calculated according to this
 * formula:
 * 
 * SMMA(i) = (SUM1-SMMA1+CLOSE(i))/N
 * 
 * Where:
 * SUM1 — is the total sum of closing prices for N periods;
 * SMMA1 — is the smoothed moving average of the first bar;
 * SMMA(i) — is the smoothed moving average of the current bar (except for the
 * first one);
 * CLOSE(i) — is the current closing price;
 * N — is the smoothing period.
 * 
 * For the following example we will set the PERIOD equal to 3. We will assume
 * the price of each day is the same as the day number for the price, thus,
 * price 1 = 1, price 2 = 2, and so on.
 * 
 * In this case, for the first data point, it will be the same as a Simple
 * Moving Price calculation. It is plotted on the chart at the third bar from
 * the first bar used in the calculation.
 * 
 * SMMA = (PRICE 1 + PRICE 2 + PRICE 3)/PERIOD
 * 
 * SMMA = (1 + 2 + 3) / 3
 * 
 * = 6 / 3
 * 
 * = 2
 * 
 * The next value would be plotted at the fourth bar from the first bar used in
 * the calculation.
 * 
 * SMMA = (PREVIOUS SUM – PREVIOUS AVG + PRICE 4) / PERIOD
 * 
 * For the second calculation of SMMA, PREVIOUS SUM is the sum of PRICE 1 +
 * PRICE 2 + PRICE 3; and PREVIOUS AVG is the initial value of SMMA.
 * 
 * SMMA = (6 – 2 + 4) / 3
 * 
 * = 8 / 3
 * 
 * = 2.67
 * 
 * The next value would be plotted at the fifth bar from the first bar used in
 * the calculation.
 * 
 * SMMA = (PREVIOUS SUM – PREVIOUS AVG + PRICE 5) / PERIOD
 * 
 * For the third and subsequent calculations of SMMA, values would be determined
 * by subtracting the PREVIOUS AVG from the PREVIOUS SUM, adding the next more
 * recent PRICE, then dividing by the PERIOD.
 * 
 * SMMA = (8 – 2.67 + 5) / 3
 * 
 * = 10.33 / 3
 * 
 * = 3.44
 *  
 * 
 * SMMA = (10.33 – 3.44 + 6) / 3
 * 
 * = 12.89 / 3
 * 
 * = 4.30
 * 
 *  
 * and so on…
 */
@Deprecated
public class Smma extends ValueImpl<Double> {
	private final ru.prolib.aquila.ta.indicator.Smma smma;
	
	public Smma(Value<Double> iValue, int periods) {
		this(iValue, periods, ValueImpl.DEFAULT_ID);
	}
	
	public Smma(Value<Double> iValue, int periods, String id) {
		super(id);
		smma = new ru.prolib.aquila.ta.indicator.Smma(iValue, periods);
	}
	
	public int getPeriods() {
		return smma.getPeriod();
	}
	
	public Value<Double> getSourceValue() {
		return smma.getSource();
	}

	@Override
	public synchronized void update() throws ValueUpdateException {
		try {
			add(smma.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
