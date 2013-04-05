package ru.prolib.aquila.ta.ds;

import java.util.Date;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueList;
import ru.prolib.aquila.ta.indicator.BollingerBands;
import ru.prolib.aquila.ta.math.Alligator;
import ru.prolib.aquila.ta.math.Cross;
import ru.prolib.aquila.ta.math.Ema;
import ru.prolib.aquila.ta.math.Max;
import ru.prolib.aquila.ta.math.Median;
import ru.prolib.aquila.ta.math.Min;
import ru.prolib.aquila.ta.math.QuikSmma;
import ru.prolib.aquila.ta.math.Shift;
import ru.prolib.aquila.ta.math.Sma;
import ru.prolib.aquila.ta.math.Smma;
import ru.prolib.aquila.ta.math.Sub;
import ru.prolib.aquila.ta.math.WilliamsZones;

public interface MarketData extends ValueList {
	public static final String TIME  = "time";
	public static final String OPEN  = "open";
	public static final String CLOSE = "close";
	public static final String HIGH  = "high";
	public static final String LOW   = "low";
	public static final String VOL   = "volume";
	public static final String MEDIAN = "median";
	
	/**
	 * Получить последний бар.
	 * 
	 * @return
	 * @throws ValueException
	 */
	public Candle getBar() throws ValueException;
	
	/**
	 * Получить бар по индексу.
	 * 
	 * @param index Индекс бара в последовательности. 0 - самое первое значение.
	 * Отрицательные индексы используются для обращению к данным в прошлом
	 * относительно конца данных. -1 - предпоследнее значение, -2 - бар перед
	 * предпоследним и т.д. 
	 * @return
	 * @throws ValueException
	 */
	public Candle getBar(int index) throws ValueException; 
	
	/**
	 * Получить длину данных (количество баров)
	 * 
	 * @return
	 */
	public int getLength();
	
	/**
	 * Получить индекс последнего бара
	 * 
	 * @return
	 */
	public int getLastBarIndex();
	
	/**
	 * Получить уровень источника данных.
	 * 
	 * Уровень источника определяет текущую глубину иерархии источников данных.
	 * Нулевой уровень означает, что данный объект является непосредственным
	 * источником данных. Первый уровень указывает на то, что данный экземпляр
	 * ссылается на непосредственный источник данных.
	 * 
	 * Служебный метод, используемый для инициализации подсистем. 
	 * 
	 * @return
	 */
	public int getLevel();
	
	/**
	 * Получить используемый источник данных.
	 * 
	 * Служебный метод, используемый для инициализации подсистем. 
	 * 
	 * @return
	 * @throws MarketDataException 
	 */
	public MarketData getSource() throws MarketDataException;

	/**
	 * Подготовить значения
	 * @throws ValueException
	 */
	public void prepare() throws ValueException;
	
	/**
	 * Обновить значения
	 * @throws ValueException
	 */
	@Override
	public void update() throws ValueException;
	
	/**
	 * Получить значения цены открытия периода
	 * @return
	 * @throws ValueException
	 */
	public Value<Double> getOpen() throws ValueException;
	
	/**
	 * Получить значения цены закрытия периода
	 * @return
	 * @throws ValueException
	 */
	public Value<Double> getClose() throws ValueException;
	
	/**
	 * Получить значения максимальной цены периода
	 * @return
	 * @throws ValueException
	 */
	public Value<Double> getHigh() throws ValueException;
	
	/**
	 * Получить значения минимальной цены периода
	 * @return
	 * @throws ValueException
	 */
	public Value<Double> getLow() throws ValueException;
	
	/**
	 * Получить значения объема периода
	 * @return
	 * @throws ValueException
	 */
	public Value<Double> getVolume() throws ValueException;
	
	/**
	 * Получить значения времени периода
	 * @return
	 * @throws ValueException
	 */
	public Value<Date> getTime() throws ValueException;
	
	/**
	 * Получить значения средней цены (HIGH+LOW)/2
	 * @return
	 * @throws ValueException
	 */
	public Median getMedian() throws ValueException;

	
	public Cross addCross(String src1, String src2, String id)
		throws ValueException;
	
	public Ema addEma(String src, int period, String id)
		throws ValueException;
	
	public Sma addSma(String src, int period, String id)
		throws ValueException;
	
	public Smma addSmma(String src, int period, String id)
		throws ValueException;
	
	public QuikSmma addQuikSmma(String src, int period, String id)
		throws ValueException;
	
	public Median addMedian(String src1, String src2, String id)
		throws ValueException;
	
	public Shift<Double> addShift(String src, int period, String id)
		throws ValueException;
	
	public Sub addSub(String one, String two, String id)
		throws ValueException;
	
	public WilliamsZones addWilliamsZones(String ao, String ac, String id)
		throws ValueException;

	public Sub addAwesomeOscillator(String id) throws ValueException;

	public Sub addAccelerationOscillator(String ao, String id)
		throws ValueException;
	
	public Alligator addAlligator(String id) throws ValueException;
	
	/**
	 * Добавить индикаторо максимумов за период.
	 * @param src источник значений для расчета максимума
	 * @param periods количество периодов для расчета максимума
	 * @param maxId идентификатор значения максимума
	 * @return
	 * @throws ValueException
	 */
	public Max addMax(String src, int periods, String maxId)
		throws ValueException;
	
	/**
	 * Добавить индикатор минимумов за период.
	 * @param src источник значений для расчета минимума
	 * @param periods количество периодов для расчета минимума
	 * @param minId идентификатор значения минимума
	 * @return
	 * @throws ValueException
	 */
	public Min addMin(String src, int periods, String minId)
		throws ValueException;
	
	/**
	 * Добавить индикатор полосы Боллинджера.
	 * @param src идентификатор источника данных (напр. цена закрытия)
	 * @param period количество периодов для MA и STDEV
	 * @param factor коэффициент смещения
	 * @param id идентификатор создаваемого значения
	 * @return
	 * @throws ValueException
	 */
	public BollingerBands addBollingerBands(String src, int period,
			double factor, String id) throws ValueException;
	
}