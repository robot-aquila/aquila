package ru.prolib.aquila.core.data;

/**
 * Интерфейс фабрики рядов.
 * <p>
 * Фабрика скрывает специфику реализации рядов значений от потребителей.
 * Например, типовая реализация ряда требует передачи в конструктор очереди
 * событий. Кроме того, базовая реализация предусматривает возможность
 * определения размера хранилища для хранения значений при рассмотрении ряда
 * как истории изменений какого-либо параметра. Использование фабрики для
 * инстанцирования рядов позволяет скрыть от потребителя эти параметры,
 * затребовав их один раз в конструкторе фабрики.
 * <p>
 * Фабрика не ограничивает систему значениями перечисленных в интерфейсе типов.
 * Она лишь определяет набор типов, используемых в базовых компонентах системы.
 * Но поскольку алгоритмы расчета значений обособлены от хранилища,
 * представленных фабрикой типов вполне достаточно для реализации большинства
 * программ. 
 * <p>
 * 2012-04-09<br>
 * $Id: SeriesFactory.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public interface SeriesFactory {
	
	/**
	 * Создать ряд типа {@link java.lang.Boolean}.
	 * <p>
	 * @param id идентификатор ряда
	 * @return ряд
	 */
	public EditableSeries<Boolean> createBoolean(String id);
	
	/**
	 * Создать ряд типа {@link java.lang.Boolean}.
	 * <p>
	 * @return ряд
	 */
	public EditableSeries<Boolean> createBoolean();
	
	/**
	 * Создать ряд типа {@link Candle}.
	 * <p>
	 * @param id идентификатор ряда
	 * @return ряд
	 */
	public EditableCandleSeries createCandle(String id);
	
	/**
	 * Создать ряд типа {@link Candle}.
	 * <p>
	 * @return ряд
	 */
	public EditableCandleSeries createCandle();
	
	/**
	 * Создать ряд типа {@link java.util.Date}.
	 * <p>
	 * @param id идентификатор ряда
	 * @return ряд
	 */
	public EditableTimeSeries createTime(String id);
	
	/**
	 * Создать ряд типа {@link java.util.Date}.
	 * <p>
	 * @return ряд
	 */
	public EditableTimeSeries createTime();
	
	/**
	 * Создать ряд типа {@link java.lang.Double}.
	 * <p>
	 * @param id идентификатор ряда
	 * @return ряд
	 */
	public EditableDataSeries createDouble(String id);
	
	/**
	 * Создать ряд типа {@link java.lang.Double}.
	 * <p>
	 * @return ряд
	 */
	public EditableDataSeries createDouble();
	
	/**
	 * Создать ряд типа {@link java.lang.Integer}.
	 * <p>
	 * @param id идентификатор ряда
	 * @return ряд
	 */
	public EditableSeries<Integer> createInteger(String id);
	
	/**
	 * Создать ряд типа {@link java.lang.Integer}.
	 * <p>
	 * @return ряд
	 */
	public EditableSeries<Integer> createInteger();

	/**
	 * Создать ряд типа {@link java.lang.Long}.
	 * <p>
	 * @param id идентификатор ряда
	 * @return ряд
	 */
	public EditableSeries<Long> createLong(String id);
	
	/**
	 * Создать ряд типа {@link java.lang.Long}.
	 * <p>
	 * @return ряд
	 */
	public EditableSeries<Long> createLong();
	
	/**
	 * Создать ряд типа {@link java.lang.String}.
	 * <p>
	 * @param id идентификатор ряда
	 * @return ряд
	 */
	public EditableSeries<String> createString(String id);
	
	/**
	 * Создать ряд типа {@link java.lang.String}.
	 * <p>
	 * @return ряд
	 */
	public EditableSeries<String> createString();

}
