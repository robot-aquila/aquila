package ru.prolib.aquila.core.data;

import org.joda.time.*;
import ru.prolib.aquila.core.BusinessEntities.TimeUnit;
import ru.prolib.aquila.core.data.timeframe.*;

/**
 * Интерфейс таймфрейма.
 */
public interface Timeframe {
	/**
	 * Минутный таймфрейм.
	 */
	public static final Timeframe M1 = new TFMinutes(1);
	/**
	 * Двухминутный таймфрейм.
	 */
	public static final Timeframe M2 = new TFMinutes(2);
	/**
	 * Трехминутный таймфрейм.
	 */
	public static final Timeframe M3 = new TFMinutes(3);
	/**
	 * Пятиминутный таймфрейм.
	 */
	public static final Timeframe M5 = new TFMinutes(5);
	/**
	 * Десятиминутный таймфрейм.
	 */
	public static final Timeframe M10 = new TFMinutes(10);
	/**
	 * Пятнадцатиминутный таймфрейм.
	 */
	public static final Timeframe M15 = new TFMinutes(15);
	/**
	 * Тридцатиминутный таймфрейм.
	 */
	public static final Timeframe M30 = new TFMinutes(30);
	/**
	 * Часовой таймфрейм.
	 */
	public static final Timeframe M60 = new TFMinutes(60);
	
	/**
	 * Получить интервал для временной метки.
	 * <p>
	 * @param time временная метка
	 * @return интервал
	 */
	public Interval getInterval(DateTime time);
	
	/**
	 * Внутридневной таймфрейм?
	 * <p>
	 * @return true если внутриндневной, иначе false
	 */
	public boolean isIntraday();
	
	/**
	 * Получить временную единицу.
	 * <p>
	 * @return временная единица
	 */
	public TimeUnit getUnit();
	
	/**
	 * Получить длину интервала.
	 * <p>
	 * @return длина интервала во временных единицах
	 */
	public int getLength();

}
