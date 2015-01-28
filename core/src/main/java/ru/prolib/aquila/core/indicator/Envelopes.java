package ru.prolib.aquila.core.indicator;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.indicator.function.*;

/**
 * Индикатор: конверты.
 */
public class Envelopes implements Starter {
	private final MA ma;
	private final EnvelopeBand upper, lower;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param movingAverage индикатор скользящейсредней
	 * @param upper индикатор верхней границы
	 * @param lower индикатор нижней границы
	 */
	public Envelopes(MA movingAverage, EnvelopeBand upper, EnvelopeBand lower) {
		super();
		this.ma = movingAverage;
		this.upper = upper;
		this.lower = lower;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param id идентификатор (может быть null)
	 * @param sourceSeries исходный ряд данных
	 * @param function функция расчета скользящей средней
	 * @param k коэффициент сдвига
	 */
	public Envelopes(String id, DataSeries sourceSeries, MAFunction function,
			double k)
	{
		ma = new MA(id, function, sourceSeries);
		upper = new EnvelopeBand(ma, true, k);
		lower = new EnvelopeBand(ma, false, k);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param sourceSeries исходный ряд данных
	 * @param function функция расчета скользящей средней
	 * @param k коэффециент сдвига
	 */
	public Envelopes(DataSeries sourceSeries, MAFunction function,  double k) {
		this(null, sourceSeries, function, k);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает конверты на базе скользящей средней по формуле
	 * {@link QuikEMAFunction}.
	 * <p>
	 * @param id идентификатор (может быть null)
	 * @param sourceSeries исходный ряд данных
	 * @param period период скользящей средней
	 * @param k коэффициент сдвига
	 */
	public Envelopes(String id, DataSeries sourceSeries, int period, double k) {
		this(id, sourceSeries, new QuikEMAFunction(period), k);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает конверты на базе скользящей средней по формуле
	 * {@link QuikEMAFunction}.
	 * <p>
	 * @param sourceSeries исходный ряд данных
	 * @param period период скользящей средней
	 * @param k коэффициент сдвига
	 */
	public Envelopes(DataSeries sourceSeries, int period, double k) {
		this(null, sourceSeries, period, k);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает конверты на базе скользящей средней по формуле
	 * {@link QuikEMAFunction} и коэффициентом сдвига 2.
	 * <p>
	 * @param sourceSeries исходный ряд данных
	 * @param period период скользящей средней
	 */
	public Envelopes(DataSeries sourceSeries, int period) {
		this(sourceSeries, period, 2.0d);
	}
	
	/**
	 * Получить индикатор скользящей средней.
	 * <p>
	 * @return индикатор
	 */
	public MA getMovingAverageSeries() {
		return ma;
	}
	
	/**
	 * Получить индикатор верхней границы конвертов.
	 * <p>
	 * @return индикатор
	 */
	public EnvelopeBand getUpperSeries() {
		return upper;
	}
	
	/**
	 * Получить индикатор нижней границы конвертов.
	 * <p>
	 * @return индикатор
	 */
	public EnvelopeBand getLowerSeries() {
		return lower;
	}
	
	/**
	 * Получить текущее значение скользящей средней.
	 * <p>
	 * @return значение индикатора
	 * @throws ValueException
	 */
	public Double getMovingAverage() throws ValueException {
		return ma.get();
	}
	
	/**
	 * Получить значение скользящей средней.
	 * <p>
	 * @param index индекс элемента ряда
	 * @return значение индикатора
	 * @throws ValueException
	 */
	public Double getMovingAverage(int index) throws ValueException {
		return ma.get(index);
	}
	
	/**
	 * Получить текущее значение верхней границы конвертов.
	 * <p>
	 * @return значение индикатора
	 * @throws ValueException
	 */
	public Double getUpper() throws ValueException {
		return upper.get();
	}
	
	/**
	 * Получить значение верхней границы конвертов.
	 * <p>
	 * @param index индекс элемента ряда
	 * @return значение индикатора
	 * @throws ValueException
	 */
	public Double getUpper(int index) throws ValueException {
		return upper.get(index);
	}
	
	/**
	 * Получить текущее значение нижней границы конвертов.
	 * <p>
	 * @return значение индикатора
	 * @throws ValueException
	 */
	public Double getLower() throws ValueException {
		return lower.get();
	}
	
	/**
	 * Получить значение нижней границы конвертов.
	 * <p>
	 * @param index индекс элемента ряда
	 * @return значение индикатора
	 * @throws ValueException
	 */
	public Double getLower(int index) throws ValueException {
		return lower.get(index);
	}

	/**
	 * Получить длину ряда.
	 * <p>
	 * @return количество элементов ряда
	 */
	public int getLength() {
		return ma.getLength();
	}

	@Override
	public void start() throws StarterException {
		upper.start();
		lower.start();
		ma.start();
	}

	@Override
	public void stop() throws StarterException {
		ma.stop();
		upper.stop();
		lower.stop();
	}

	/**
	 * Индикатор в работе?
	 * <p>
	 * @return true индикатор работает, false не работает
	 */
	public boolean started() {
		return ma.started();
	}
	
	/**
	 * Установить период скользящей средней.
	 * <p>
	 * @param period период
	 * @throws IllegalStateException индикатор в работе
	 */
	public void setPeriod(int period) {
		ma.setPeriod(period);
	}
	
	/**
	 * Получить период скользящей средней.
	 * <p>
	 * @return период
	 */
	public int getPeriod() {
		return ma.getPeriod();
	}
	
	/**
	 * Установить коэффициент смещения границ конверта.
	 * <p>
	 * @param k коэффициент
	 */
	public void setOffset(double k) {
		upper.setOffset(k);
		lower.setOffset(k);
	}
	
	/**
	 * Получить коэффициент смещения границ конверта.
	 * <p>
	 * @return коэффициент
	 */
	public double getOffset() {
		return upper.getOffset();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Envelopes.class ) {
			return false;
		}
		Envelopes o = (Envelopes) other;
		return new EqualsBuilder()
			.append(o.ma, ma)
			.append(o.upper, upper)
			.append(o.lower, lower)
			.isEquals();
	}

}
