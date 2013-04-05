package ru.prolib.aquila.core.data;

import java.util.Date;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.ta.Deal;

/**
 * Данные японской свечи.
 * <p>
 * 2012-04-20<br>
 * $Id: Candle.java 218 2012-05-20 12:15:33Z whirlwind $
 */
public class Candle {
	private final int id;
	private final Date time;
	private final double open;
	private double close;
	private double high;
	private double low;
	private long volume;
	
	/**
	 * Базовый конструктор.
	 * 
	 * Создает свечу на основании указанных параметров.
	 * 
	 * @param id идентификатор (индекс)
	 * @param time время
	 * @param open цена открытия
	 * @param high максимальная цена
	 * @param low минимальная цена
	 * @param close цена закрытия
	 * @param volume объем
	 */
	public Candle(int id, Date time, double open, double high, double low,
			double close, long volume)
	{
		super();
		this.time	= time;
		this.open	= open;
		this.high	= high;
		this.low	= low;
		this.close	= close;
		this.volume	= volume;
		this.id = id;
	}

	/**
	 * Конструктор.
	 * 
	 * Создает свечу на основании указанных параметров.
	 * В качестве идентификатора устанавливается значение 0.
	 * 
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
		this(0, time, open, high, low, close, volume);
	}
	
	/**
	 * Конструктор.
	 * 
	 * Создает свечу на основании указанной свечи.
	 * В качестве идентификатора устанавливается значение 0.
	 * 
	 * @param candle свеча-основание
	 */
	public Candle(Candle candle) {
		this(0, candle);
	}
	
	/**
	 * Конструктор.
	 * 
	 * Создает свечу на основании указанной свечи и с указаннем идентификатора. 
	 * 
	 * @param id идентификатор новой свечи
	 * @param candle свеча-основание
	 */
	public Candle(int id, Candle candle) {
		this(id, candle.getTime(), candle.getOpen(),
				candle.getHigh(), candle.getLow(),
				candle.getClose(), candle.getVolume());		
	}
	
	/**
	 * Конструктор.
	 * 
	 * Создает свечу на основании информации о сделке.
	 * В качестве идентификатора устанавливается значение 0.
	 * 
	 * @param initialTime время свечи
	 * @param initialDeal сделка
	 */
	public Candle(Date initialTime, Deal initialDeal) {
		this(0, initialTime, initialDeal);
	}
	
	/**
	 * Конструктор.
	 * 
	 * Создает свечу на основании информации о сделке.
	 * 
	 * @param id идентификатор новой свечи
	 * @param initialTime время
	 * @param initialDeal сделка
	 */
	public Candle(int id, Date initialTime, Deal initialDeal) {
		this(id, initialTime, initialDeal.getPrice(), initialDeal.getPrice(),
				initialDeal.getPrice(), initialDeal.getPrice(),
				initialDeal.getQuantity());
	}
	
	/**
	 * Аггрегировать сделку на основе цены и объема
	 * @param price цена
	 * @param volume объем сделки
	 */
	public void addDeal(double price, long volume) {
		this.volume += volume;
		close = price;
		if ( price > high ) {
			high = price;
		}
		if ( price < low ) {
			low = price;
		}
	}

	/**
	 * Аггрегировать сделку.
	 * Время сделки не учитывается.
	 * @param deal сделка
	 */
	public void addDeal(Deal deal) {
		addDeal(deal.getPrice(), deal.getQuantity());
	}
	
	/**
	 * Аггрегировать свечу.
	 * Время свечи не учитывается.
	 * @param candle
	 */
	public void addCandle(Candle candle) {
		addDeal(candle.getOpen(), candle.getVolume());
		addDeal(candle.getHigh(), 0);
		addDeal(candle.getLow(), 0);
		addDeal(candle.getClose(), 0);
	}
	
	/**
	 * Получить высоту тела свечи
	 * 
	 * Фактически abs(open - close)
	 * 
	 * @return
	 */
	public double getCandleHeight() {
		return Math.abs(open - close);
	}
	
	/**
	 * Получить общую высоту свечи
	 * 
	 * Фактически high - low
	 * 
	 * @return
	 */
	public double getHeight() {
		return high - low;
	}
	
	/**
	 * Получить среднюю цену тела свечи или цену закрытия, в зависимости
	 * что выше.
	 * 
	 * Данный метод используется например для выставления заявок на продажу.
	 * Если свеча закрылась выше чем открылась, то это позволяет с высокой
	 * вероятностью продать по более выгодной цене. Если свеча закрылась ниже,
	 * то выставление заявки по средней увеличивает выгодность продажи.
	 *  
	 * @return
	 */
	public double getCandleCenterOrCloseIfHigher() {
		return close > open ? close : getCandleCenter();
	}
	
	/**
	 * Получить среднюю цену тела свечи или цену закрытия, в зависимости
	 * что ниже.
	 * 
	 * Данный метод используется для выставления заявок на покупку.
	 * Если свеча закрывается ниже чем открылась, то высока вероятность купить
	 * по более выгодной цене. Если свеча закрылась выше, то выставление
	 * заявки по средней увеличивает выгодность покупки.
	 * 
	 * @return
	 */
	public double getCandleCenterOrCloseIfLower() {
		return close < open ? close : getCandleCenter();
	}
	
	/**
	 * Получить цену середины тела свечи.
	 * 
	 * Фактически (open + close) / 2
	 * 
	 * @return
	 */
	public double getCandleCenter() {
		return (open + close) / 2;
	}
	
	/**
	 * Получить идентификатор (индекс).
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Получить время
	 * 
	 * @return
	 */
	public Date getTime() {
		return time;
	}
	
	/**
	 * Получить цену открытия
	 * @return цена открытия
	 */
	public double getOpen() {
		return open;
	}
	
	/**
	 * Получить цену закрытия
	 * @return цена закрытия
	 */
	public double getClose() {
		return close;
	}
	
	/**
	 * Получить максимальную цену
	 * @return максимальная цена
	 */
	public double getHigh() {
		return high;
	}
	
	/**
	 * Получить минимальную цену
	 * @return минимальная цена
	 */
	public double getLow() {
		return low;
	}
	
	/**
	 * Получить объем сделок
	 * TODO: поменять на double?
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
				.append(id, other.id)
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