package ru.prolib.aquila.core.data;

import java.time.Instant;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Tick;

/**
 * Представление динамики цены за период.
 * <p>
 * 2012-04-20<br>
 * $Id: Candle.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class Candle {
	
	/**
	 * Экземпляр свечи, использующийся для индикации конца последовательности.
	 * Содержимое этой свечи не должно использоваться в качестве данных.
	 */
	public static final Candle END = new Candle(null, null, null, null, null, null);
	private final Interval interval;
	private final CDecimal open, close, high, low, volume;
	
	/**
	 * Создать свечу на основании указанных параметров.
	 * <p> 
	 * @param interval временной интервал свечи
	 * @param open цена открытия
	 * @param high максимальная цена
	 * @param low минимальная цена
	 * @param close цена закрытия
	 * @param volume объем
	 */
	public Candle(Interval interval, CDecimal open, CDecimal high, CDecimal low,
			CDecimal close, CDecimal volume)
	{
		super();
		this.interval = interval;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume	= volume;
	}
	
	/**
	 * Создать копию свечи.
	 * <p>
	 * @param candle свеча-основание
	 */
	public Candle(Candle candle) {
		this(candle.interval, candle.open, candle.high, candle.low,
				candle.close, candle.volume);
	}
	
	/**
	 * Создать свечу на основе сделки.
	 * <p>
	 * @param interval интервал свечи
	 * @param price цена (используется для всех компонентов цены свечи)
	 * @param volume объем
	 */
	public Candle(Interval interval, CDecimal price, CDecimal volume) {
		this(interval, price, price, price, price, volume);
	}
	
	/**
	 * Создать новую свечу путем добавления сделки к текущей.
	 * <p>
	 * В качестве интервала и цены открытия используются соответствующие
	 * значения текущей свечи. Цена учитывается при расчете максимума и минимума
	 * новой свечи. А так же цена используется в качестве цены закрытия новой
	 * свечи. Указанный объем суммируется с объемом текущей свечи.
	 * <p>
	 * @param price цена
	 * @param volume объем сделки
	 * @return новая свеча
	 */
	public Candle addDeal(CDecimal price, CDecimal volume) {
		return new Candle(interval, open,
				price.max(high),
				price.min(low),
				price,
				this.volume.add(volume));
	}
	
	/**
	 * Создать новую свечу путем объединения с указанной.
	 * <p>
	 * Интервал добавляемой свечи должен принадлежать интервалу текущей.
	 * В качестве интервала и цены открытия результирующей свечи используются
	 * соответствующие значения текущей свечи. Цены максимума и минимума
	 * расчитываются на основе обеих свечей. В качестве цены закрытия
	 * используется цена закрытия указанной свечи. Объем обеих свечей
	 * суммируется.
	 * <p>
	 * @param candle свеча
	 * @return новая свеча
	 * @throws OutOfIntervalException интервал аргумента выходит за
	 * границы интервала данной свечи 
	 */
	public Candle addCandle(Candle candle)
		throws OutOfIntervalException
	{
		if ( ! interval.encloses(candle.interval) ) {
			throw new OutOfIntervalException(interval, candle);
		}
		return new Candle(interval, open,
				high.max(candle.high),
				low.min(candle.low),
				candle.close,
				volume.add(candle.volume));
	}
	
	/**
	 * Создать свечу путем добавления тика к текущей свече.
	 * <p>
	 * @param tick тик данных
	 * @return новая свеча
	 * @throws OutOfIntervalException время тика за границей интервала свечи
	 */
	public Candle addTick(Tick tick) throws OutOfIntervalException {
		if ( ! interval.contains(tick.getTime()) ) {
			throw new OutOfIntervalException(interval, tick);
		}
		return addDeal(tick.getPrice(), tick.getSize());
	}
	
	/**
	 * Получить время начала интервала свечи.
	 * <p>
	 * @return время начала интервала
	 */
	public Instant getStartTime() {
		return interval.getStart();
	}
	
	/**
	 * Получить время окончания интервала свечи.
	 * <p>
	 * Подразумевается время окончания периода в соответствии с соглашениями,
	 * принятыми библиотекой joda, т.е. время окончания интервала не принадлежит
	 * интервалу, а указывает на следующую миллисекунду после интервала.
	 * <p>
	 * @return время окончания интервала
	 */
	public Instant getEndTime() {
		return interval.getEnd();
	}
	
	/**
	 * Получить высоту тела свечи.
	 * <p>
	 * Фактически abs(open - close)
	 * <p>
	 * @return высота свечи
	 */
	public CDecimal getBody() {
		return open.subtract(close).abs();
	}
	
	/**
	 * Получить общую высоту свечи.
	 * <p>
	 * Фактически high - low
	 * <p>
	 * @return высота свечи
	 */
	public CDecimal getHeight() {
		return high.subtract(low);
	}
	
	/**
	 * Бычья свечка?
	 * <p>
	 * @return true если свечка бычья (рост цены)
	 */
	public boolean isBullish() {
		return close.compareTo(open) > 0;
	}
	
	/**
	 * Медвежья свечка?
	 * <p>
	 * @return true если свечка медвежья (снижение цены)
	 */
	public boolean isBearish() {
		return close.compareTo(open) < 0;
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
	public CDecimal getBodyMiddleOrCloseIfBullish() {
		return isBullish() ? close : getBodyMiddle();
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
	public CDecimal getBodyMiddleOrCloseIfBearish() {
		return isBearish() ? close : getBodyMiddle();
	}
	
	/**
	 * Получить цену середины тела свечи.
	 * <p>
	 * Фактически (open + close) / 2
	 * <p>
	 * @return цена
	 */
	public CDecimal getBodyMiddle() {
		return open.add(close).divide(2L);
	}
	
	/**
	 * Получить интервал.
	 * <p>
	 * @return интервал свечи
	 */
	public Interval getInterval() {
		return interval;
	}
	
	/**
	 * Получить цену открытия.
	 * <p>
	 * @return цена открытия
	 */
	public CDecimal getOpen() {
		return open;
	}
	
	/**
	 * Получить цену закрытия.
	 * <p>
	 * @return цена закрытия
	 */
	public CDecimal getClose() {
		return close;
	}
	
	/**
	 * Получить максимальную цену.
	 * <p>
	 * @return максимальная цена
	 */
	public CDecimal getHigh() {
		return high;
	}
	
	/**
	 * Получить минимальную цену.
	 * <p>
	 * @return минимальная цена
	 */
	public CDecimal getLow() {
		return low;
	}
	
	/**
	 * Получить объем сделок.
	 * <p>
	 * @return объем сделок
	 */
	public CDecimal getVolume() {
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
				.append(interval, other.interval)
				.isEquals();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() +
			"[T=" + interval.getStart() + " " + interval.toDuration() + "," +
			" O=" + open + "," +
			" H=" + high + "," +
			" L=" + low + "," +
			" C=" + close + "," +
			" V=" + volume + "]";
	}
	
	/**
	 * Получить высоту верхней тени.
	 * <p>
	 * @return высота тени
	 */
	public CDecimal getTopShadow() {
		return high.subtract(isBullish() ? close : open);
	}
	
	/**
	 * Получить высоту нижней тени.
	 * <p>
	 * @return высота тени
	 */
	public CDecimal getBottomShadow() {
		return (isBearish() ? close : open).subtract(low);
	}
	
}