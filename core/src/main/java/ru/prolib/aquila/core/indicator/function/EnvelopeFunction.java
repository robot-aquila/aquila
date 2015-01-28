package ru.prolib.aquila.core.indicator.function;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.indicator.SimpleFunction;

/**
 * Функция расчета границы конверта.
 * <p>
 */
public class EnvelopeFunction implements SimpleFunction<Double, Double> {
	private final boolean upper;
	private double k;
	
	public EnvelopeFunction(boolean upper, double k) {
		super();
		this.upper = upper;
		this.k = k;
	}
	
	public EnvelopeFunction(boolean upper) {
		this(upper, 2.0d);
	}
	
	/**
	 * Получить коэффициент сдвига.
	 * <p>
	 * @return коэффициент
	 */
	public synchronized double getOffset() {
		return k;
	}
	
	/**
	 * Установить коэффициент сдвига.
	 * <p>
	 * @param k коэффициент
	 */
	public synchronized void setOffset(double k) {
		this.k = k;
	}
	
	/**
	 * Это верхняя граница конверта?
	 * <p>
	 * @return true - верхняя граница, false - нижняя
	 */
	public boolean isUpper() {
		return upper;
	}
	
	/**
	 * Это нижняя граница конверта?
	 * <p>
	 * @return true - нижняя граница, false - верхняя 
	 */
	public boolean isLower() {
		return ! upper;
	}

	@Override
	public synchronized Double calculate(Series<Double> sourceSeries, int index)
			throws ValueException
	{
		Double value = sourceSeries.get(index);
		if ( value == null ) {
			return null;
		}
		double kk = k / 100;
		return value * (upper ? 1 + kk : 1 - kk);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != EnvelopeFunction.class ) {
			return false;
		}
		EnvelopeFunction o = (EnvelopeFunction) other;
		return new EqualsBuilder()
			.append(o.k, k)
			.append(o.upper, upper)
			.isEquals();
	}

	@Override
	public String getDefaultId() {
		return "Envelope" + (upper ? "Upper" : "Lower") + "(" + k + ")";
	}

}
