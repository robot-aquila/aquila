package ru.prolib.aquila.core.data;

import java.util.Date;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Данные японской свечи.
 * <p>
 * 2012-04-20<br>
 * $Id: Candle.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class Candle {
	/**
	 * Экземпляр свечи, использующийся для индикации конца последовательности.
	 * Содержимое этой свечи не должно использоваться в качестве данных.
	 */
	public static final Candle END = new Candle(null,0,0,0,0,0);
	private final Date time;
	private final double open;
	private final double close;
	private final double high;
	private final double low;
	private long volume;
	
	/**
	 * Создать свечу на основании указанных параметров.
	 * <p> 
	 * @param time время
	 * @param open цена открытия
	 * @param high максимальная цена
	 * @param low минимальная цена
	 * @param close цена закрытия
	 * @param volume объем
	 */
	public Candle(Date time, double open, double high, double low,
			double close, long volume)
	{
		super();
		this.time	= time;
		this.open	= open;
		this.high	= high;
		this.low	= low;
		this.close	= close;
		this.volume	= volume;
	}
	
	/**
	 * Создать копию свечи.
	 * <p>
	 * @param candle свеча-основание
	 */
	public Candle(Candle candle) {
		this(candle.getTime(), candle.getOpen(),
				candle.getHigh(), candle.getLow(),
				candle.getClose(), candle.getVolume());		
	}
	
	/**
	 * Создать свечу на основе сделки.
	 * <p>
	 * @param time время
	 * @param price цена (используется для всех компонентов цены свечи)
	 * @param volume объем
	 */
	public Candle(Date time, double price, long volume) {
		this(time, price, price, price, price, volume);
	}
	
	/**
	 * Создать новую свечу путем добавления сделки к текущей.
	 * <p>
	 * В качестве времени и цены открытия используются соответствующие значения
	 * текущей свечи. Цена учитывается при расчете максимума и минимума новой
	 * свечи. А так же цена используется в качестве цены закрытия новой свечи.
	 * Указанный объем суммируется с объемом текущей свечи.
	 * <p>
	 * @param price цена
	 * @param volume объем сделки
	 * @return новая свеча
	 */
	public Candle addDeal(double price, long volume) {
		return new Candle(time, open,
				price > high ? price : high,
				price < low ? price : low,
				price,
				this.volume + volume);
	}
	
	/**
	 * Создать новую свечу путем объединения с указанной.
	 * <p>
	 * В качестве времени и цены открытия используются соответствующие значения
	 * текущей свечи. Цены максимума и минимума расчитываются на основе обеих
	 * свечей. В качестве цены закрытия используется цена закрытия указанной
	 * свечи. Объем обеих свечей суммируется.
	 * <p>
	 * @param candle свеча
	 * @return новая свеча
	 */
	public Candle addCandle(Candle candle) {
		return new Candle(time, open,
				candle.high > high ? candle.high : high,
				candle.low < low ? candle.low : low,
				candle.close,
				volume + candle.volume);
	}
	
	/**
	 * Получить высоту тела свечи.
	 * <p>
	 * Фактически abs(open - close)
	 * <p>
	 * @return высота свечи
	 */
	public double getCandleHeight() {
		return Math.abs(open - close);
	}
	
	/**
	 * Получить общую высоту свечи.
	 * <p>
	 * Фактически high - low
	 * <p>
	 * @return высота свечи
	 */
	public double getHeight() {
		return high - low;
	}
	
	/**
	 * Получить среднюю цену тела свечи или цену закрытия, в зависимости
	 * что выше.
	 * <p>
	 * Данный метод используется например для выставления заявок на продажу.
	 * Если свеча закрылась выше чем открылась, то это позволяет с высокой
	 * вероятностью продать по более выгодной цене. Если свеча закрылась ниже,
	 * то выставление заявки по средней увеличивает выгодность продажи.
	 * <p>
	 * @return цена
	 */
	public double getCandleCenterOrCloseIfHigher() {
		return close > open ? close : getCandleCenter();
	}
	
	/**
	 * Получить среднюю цену тела свечи или цену закрытия, в зависимости
	 * что ниже.
	 * <p>
	 * Данный метод используется для выставления заявок на покупку.
	 * Если свеча закрывается ниже чем открылась, то высока вероятность купить
	 * по более выгодной цене. Если свеча закрылась выше, то выставление
	 * заявки по средней увеличивает выгодность покупки.
	 * <p>
	 * @return цена
	 */
	public double getCandleCenterOrCloseIfLower() {
		return close < open ? close : getCandleCenter();
	}
	
	/**
	 * Получить цену середины тела свечи.
	 * <p>
	 * Фактически (open + close) / 2
	 * <p>
	 * @return цена
	 */
	public double getCandleCenter() {
		return (open + close) / 2;
	}
	
	/**
	 * Получить время.
	 * <p>
	 * @return время свечи
	 */
	public Date getTime() {
		return time;
	}
	
	/**
	 * Получить цену открытия.
	 * <p>
	 * @return цена открытия
	 */
	public double getOpen() {
		return open;
	}
	
	/**
	 * Получить цену закрытия.
	 * <p>
	 * @return цена закрытия
	 */
	public double getClose() {
		return close;
	}
	
	/**
	 * Получить максимальную цену.
	 * <p>
	 * @return максимальная цена
	 */
	public double getHigh() {
		return high;
	}
	
	/**
	 * Получить минимальную цену.
	 * <p>
	 * @return минимальная цена
	 */
	public double getLow() {
		return low;
	}
	
	/**
	 * Получить объем сделок.
	 * <p>
	 * @return объем сделок
	 */
	public long getVolume() {
		return volume;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o == this ) {
			return true;
		}
		if ( o instanceof Candle ) {
			Candle other = (Candle)o;
			return new EqualsBuilder()
				.append(open, other.open)
				.append(high, other.high)
				.append(low, other.low)
				.append(close, other.close)
				.append(volume, other.volume)
				.append(time, other.time)
				.isEquals();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return time.toString() +
			" O:" + open +
			" H:" + high +
			" L:" + low +
			" C:" + close +
			" V:" + volume;
	}
	
}