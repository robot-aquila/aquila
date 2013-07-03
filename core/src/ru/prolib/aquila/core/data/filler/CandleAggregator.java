package ru.prolib.aquila.core.data.filler;

import java.util.Date;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.*;

/**
 * Агрегатор свечей.
 * <p>
 * Объект позволяет формировать последовательность свечей, используя группировку 
 * разнородных данных по временным периодам. В свечу могут быть агрегированы
 * сделки, тики, другие свечи или временные метки. Свеча закрывается (помещается
 * в конец последовательности) при первом поступлении данных более позднего
 * периода. В основе механизма лежат два ключевых понятия: <b>Точка актуальности
 * (ТА)</b> и <b>Временная група (ВГ)</b>.
 * <p>
 * Точка актуальности последовательности - это временная метка, указывающая на
 * границу отчета. ТА смещается каждый раз с получением более поздних данных.
 * Никакие входящие данные, время которых меньше ТА не агрегируются. Так
 * же ТА может быть непосредственно смещена в будущее, что позволяет
 * ограничивать обработку устаревших данных не меняя механизма поставки этих
 * данных. Например, формирование свечей по котировкам из прошлых периодов
 * можно ограничить, предварительно сместив ТА на нужное время. 
 * <p> 
 * Характер влияния на точку актуальности зависит от типа агрегируемых данных.
 * При обработке тика, сделки или свечи ТА смещается, если время
 * соответствующего объекта больше ТА (объект младше, значит представляет новые
 * данные). Если время объекта равно ТА, то данные так же агрегируются, но на ТА
 * это никаки не влияет. Если время объекта меньше ТА, значит данные старше
 * (устарели) и следовательно должны быть отброшены.
 * <p>
 * Для понимания того, как агрегирование временной метки влияет на результат,
 * необходимо разобрать понятие временной группы. ВГ - это классификатор
 * данных на временной оси. Поскольку основной целью представления данных в виде
 * японских свечей является усреднение данных в равные промежутки времени, для
 * классификации группы достаточно знать только время начала периода. Таким
 * образом, ВГ - это временная метка, однозначно определяющая период группировки
 * данных. Фактически ВГ - это уникальный идентификатор свечи.
 * <p>
 * В процессе добавления данных, ТА постоянно увеличивается. Но в каждый момент
 * времени ТА относится к определенной временной группе. Переход ТА в следующую
 * временную группу является сигналом, свидетельствующим о том, что формирование
 * свечи завершено и она должна быть добавлена в итоговую последовательность.
 * При этом, новые данные относятся уже к следующей ВГ и для их агрегирования
 * необходимо открыть новую свечу.
 * <p>
 * Для случая агрегирования временной метки процесс аналогичен, с той разницей,
 * что поскольку усредняемых данных в этом процессе нет, то открытие новой свечи
 * не выполняется.
 */
public class CandleAggregator implements CandleSeries {
	private final EditableCandleSeries candles;
	private final AlignTime aligner;
	private Date ap,tg;
	private Tick lastTick;
	private Candle candle;
	
	/**
	 * Базовый конструктор.
	 * <p>
	 * @param candles набор свечей
	 * @param aligner алгоритм группировки по времени
	 */
	public CandleAggregator(EditableCandleSeries candles, AlignTime aligner) {
		super();
		this.candles = candles;
		this.aligner = aligner;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает новый агрегатор с собственным экземпляром набора свечей и
	 * группировкой данных по указанному количеству минут.
	 * <p>
	 * @param periodMinutes период группировки в минутах
	 */
	public CandleAggregator(int periodMinutes) {
		this(new SeriesFactoryImpl().createCandle(),
				new AlignMinute(periodMinutes));
	}
	
	/**
	 * Получить ТА.
	 * <p>
	 * @return точка актуальности
	 */
	public synchronized Date getActualityPoint() {
		return ap;
	}
	
	public CandleSeries getCandles() {
		return candles;
	}
	
	public AlignTime getTimeAligner() {
		return aligner;
	}
	
	@Override
	public TimeSeries getTime() {
		return candles.getTime();
	}
	
	@Override
	public DataSeries getOpen() {
		return candles.getOpen();
	}
	
	@Override
	public DataSeries getClose() {
		return candles.getClose();
	}
	
	@Override
	public DataSeries getHigh() {
		return candles.getHigh();
	}
	
	@Override
	public DataSeries getLow() {
		return candles.getLow();
	}
	
	@Override
	public DataSeries getVolume() {
		return candles.getVolume();
	}
	
	@Override
	public String getId() {
		return candles.getId();
	}

	@Override
	public Candle get() throws ValueException {
		return candles.get();
	}

	@Override
	public Candle get(int index) throws ValueException {
		return candles.get(index);
	}

	@Override
	public int getLength() {
		return candles.getLength();
	}

	@Override
	public EventType OnAdd() {
		return candles.OnAdd();
	}

	@Override
	public EventType OnUpd() {
		return candles.OnUpd();
	}
	
	/**
	 * Добавить тик данных.
	 * <p>
	 * @param tick тик данных
	 * @return true - формирование свечи завершено, false - свеча не добавлена 
	 */
	public synchronized boolean add(Tick tick) {
		if ( tick == null || tick.equals(lastTick) ) {
			return false;
		}
		lastTick = tick;

		boolean result = false;
		Date dAP = tick.getTime(); // ТА данных
		if ( ! slideAP(dAP) ) {
			// Устаревшие данные: пропускаем
			return false;
		}
		if ( slideTG(dAP) && candle != null ) {
			// Данные новой ВГ: закрываем предыдущую -> открываем новую
			closeCandle();
			result = true;				
		}
		aggregate(tick.getValue(), tick.getVolume());
		return result;
	}
	
	/**
	 * Добавить временную метку.
	 * <p>
	 * Фактически выполняет попытку смещения ТА. В случае успеха, незакрытая
	 * свеча добавляется в последовательность. 
	 * <p>
	 * @param time временная метка
	 * @return true - формирование свечи завершено, false - свеча не добавлена
	 */
	public synchronized boolean add(Date time) {
		boolean result = false;
		if ( ! slideAP(time) ) {
			// Время за границами ТА: пропускаем
			return false;
		}
		if ( slideTG(time) && candle != null ) {
			// Время в новой ВГ: закрываем свечу
			closeCandle();
			result = true;
		}
		return result;
	}
	
	/**
	 * Добавить сделку.
	 * <p>
	 * Работает через {@link #add(Tick)}, формируя тик на основании сделки.
	 * <p>
	 * @param trd сделка
	 * @return true - формирование свечи завершено, false - свеча не добавлена
	 */
	public synchronized boolean add(Trade trd) {
		Double vol = trd.getQty() == null ? 0d : trd.getQty().doubleValue(); 
		return add(new Tick(trd.getTime(), trd.getPrice(), vol));
	}
	
	/**
	 * Добавить свечу.
	 * <p>
	 * <b>Прим.</b> не реализовано в данной версии.
	 * <p>
	 * @param candle свеча
	 * @return true - формирование свечи завершено, false - свеча не добавлена 
	 */
	public synchronized boolean add(Candle candle) {
		throw new UnsupportedOperationException("TODO: not implemented");
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CandleAggregator.class ) {
			return false;
		}
		CandleAggregator o = (CandleAggregator) other;
		return new EqualsBuilder()
			.append(o.aligner, aligner)
			.append(o.candles, candles)
			.isEquals();
	}
	
	/**
	 * Агрегировать данные.
	 * <p>
	 * ВГ должна быть определена, так как если свеча не открыта,
	 * то создается новая по временем ВГ.
	 * <p>
	 * @param price цена
	 * @param qty количество
	 */
	private void aggregate(Double price, Long qty) {
		if ( price == null ) {
			return;
		}
		if ( qty == null ) {
			qty = 0L;
		}
		if ( candle == null ) {
			candle = new Candle(tg, price, qty);
		} else {
			candle = candle.addDeal(price, qty);
		}
	}
	
	/**
	 * Шоткат к {@link #aggregate(Double, Long)} с кастом.
	 * <p>
	 * @param price цена
	 * @param qty количество
	 */
	private void aggregate(Double price, Double qty) {
		if ( qty == null ) {
			qty = 0d;
		}
		aggregate(price, qty.longValue());
	}
	
	/**
	 * Закрыть свечу.
	 * <p>
	 * Закрывает незавершенную свечу, если она открыта.
	 */
	private void closeCandle() {
		if ( candle != null ) {
			candles.add(candle);
			candle = null;
		}
	}
	
	/**
	 * Сместить точку актуальности.
	 * <p>
	 * @param time временная метка
	 * @return true - нове значение ТА применено, false - ТА не изменилось
	 */
	private boolean slideAP(Date time) {
		if ( ap == null ) {
			// Вызвано впервые: устанавливаем ТА по указанному
			ap = time;
			return true;
		} else if ( time.before(ap) ) {
			// Время за границей ТА: отбрасываем
			return false;
		} else {
			// Время в пределах ТА: применяем
			ap = time;
			return true;
		}
	}
	
	/**
	 * Расчитать временную группу.
	 * <p>
	 * @param time временная метка
	 * @return true - определена новая ВГ, false - ВГ не изменилось 
	 */
	private boolean slideTG(Date time) {
		Date nTG = aligner.align(time);
		if ( tg == null ) {
			// Вызвано впервые: устанавливаем ВГ по указанному
			tg = nTG;
			return false;
		} else if ( tg.before(nTG) ) {
			// Текущая ВГ в прошлом: используем новую
			tg = nTG;
			return true;
		} else {
			// Время рамках текущей ВГ или раньше: ВГ не меняется
			return false;
		}
	}
	
}
