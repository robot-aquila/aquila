package ru.prolib.aquila.core.indicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;

/**
 * Абстрактная заготовка под индикатор типа MA.
 * <p>
 * 2013-03-12<br>
 * $Id: _MA.java 571 2013-03-12 00:53:34Z whirlwind $
 */
abstract public class _MA implements DataSeries {
	private static final Logger logger;
	protected final DataSeriesImpl series;
	protected final DataSeries source;
	protected final int period;
	
	static {
		logger = LoggerFactory.getLogger(_MA.class);
	}

	public _MA(String id, DataSeries source, int period, int storageLimit) {
		super();
		if ( period < 2 ) {
			throw new IllegalArgumentException("Period cannot be less than 2");
		}
		this.source = source;
		this.period = period;
		series = new DataSeriesImpl(makeId(id), storageLimit);
		init();
	}

	/**
	 * Получить лимит хранилища данных.
	 * <p>
	 * @return лимит
	 */
	public int getStorageLimit() {
		return series.getStorageLimit();
	}

	/**
	 * Получить исходный ряд.
	 * <p>
	 * @return ряд
	 */
	public DataSeries getSource() {
		return source;
	}

	/**
	 * Получить период скользящей средней.
	 * <p>
	 * @return период
	 */
	public int getPeriod() {
		return period;
	}

	@Override
	public String getId() {
		return series.getId();
	}

	@Override
	public synchronized Double get() throws ValueException {
		return series.get();
	}

	@Override
	public synchronized Double get(int index) throws ValueException {
		return series.get(index);
	}

	@Override
	public synchronized int getLength() {
		return series.getLength();
	}

	@Override
	public EventType OnAdd() {
		return series.OnAdd();
	}

	@Override
	public EventType OnUpd() {
		return series.OnUpd();
	}
	
	private void init() {
		source.OnAdd().addListener(new EventListener() {
			@SuppressWarnings("unchecked")
			@Override public void onEvent(Event event) {
				onSourceValueAdded((ValueEvent<Double>) event);
			}
		});
		source.OnUpd().addListener(new EventListener() {
			@SuppressWarnings("unchecked")
			@Override public void onEvent(Event event) {
				onSourceValueUpdated((ValueEvent<Double>) event);
			}
		});
		synchronized ( source ) {
			for ( int i = 0; i < source.getLength(); i ++ ) {
				try {
					series.add(calculate(i));
				} catch ( ValueException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		}
	}
	
	private synchronized void onSourceValueAdded(ValueEvent<Double> event) {
		try {
			series.add(calculate(event.getValueIndex()));
		} catch ( ValueException e ) {
			logger.error("Unexpected exception: ", e);
		}
	}
	
	private synchronized void onSourceValueUpdated(ValueEvent<Double> event) {
		synchronized ( series ) {
			int index = event.getValueIndex();
			int last = source.getLength() - 1;
			for ( ; index <= last; index ++ ) {
				try {
					series.set(calculate(index));
				} catch ( ValueException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		}
	}

	/**
	 * Сформировать идентификатор.
	 * <p>
	 * Данный метод формирует идентификатор если он не задан или модифицирует
	 * указанный идентификатор в зависимости от специфики ряда. 
	 * <p>
	 * @param id идентификатор переданный в конструктор (может быть null)
	 * @return идентификатор
	 */
	abstract protected String makeId(String id);
	
	/**
	 * Расчитать значение.
	 * <p>
	 * @param index индекс исходного элемента ряда (всегда гарантировано >=0)
	 * @return новое значение
	 * @throws ValueException 
	 */
	abstract protected Double calculate(int index) throws ValueException;
	
}
