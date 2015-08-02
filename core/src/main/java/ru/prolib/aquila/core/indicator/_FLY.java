package ru.prolib.aquila.core.indicator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;

/**
 * Абстрактная заготовка под индикатор типа TR.
 * <p>
 * 2013-03-12<br>
 * $Id: _FLY.java 571 2013-03-12 00:53:34Z whirlwind $
 */
abstract public class _FLY<T> implements Series<Double> {
	private static final Logger logger;
	protected final Series<T> source;
	private final String id;
	private final EventDispatcher dispatcher;
	private final EventTypeSI onAdd, onUpd;
	
	static {
		logger = LoggerFactory.getLogger(_FLY.class);
	}

	public _FLY(EventSystem es, String id, Series<T> source) {
		super();
		this.source = source;
		this.id = makeId(id);
		dispatcher = es.createEventDispatcher(id);
		onAdd = dispatcher.createSyncType("Add");
		onUpd = dispatcher.createSyncType("Upd");
		init();
	}

	/**
	 * Получить исходный ряд.
	 * <p>
	 * @return ряд
	 */
	public Series<T> getSource() {
		return source;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Double get() throws ValueException {
		return calculate(source.getLength() - 1);
	}

	@Override
	public Double get(int index) throws ValueException {
		return calculate(index);
	}

	@Override
	public int getLength() {
		return source.getLength();
	}

	@Override
	public EventType OnAdded() {
		return onAdd;
	}

	@Override
	public EventType OnUpdated() {
		return onUpd;
	}
	
	private void init() {
		source.OnAdded().addListener(new EventListener() {
			@SuppressWarnings("unchecked")
			@Override public void onEvent(Event event) {
				onSourceValueAdded((ValueEvent<Double>) event);
			}
		});
		source.OnUpdated().addListener(new EventListener() {
			@SuppressWarnings("unchecked")
			@Override public void onEvent(Event event) {
				onSourceValueUpdated((ValueEvent<Double>) event);
			}
		});
	}
	
	private synchronized void onSourceValueAdded(ValueEvent<Double> event) {
		try {
			dispatcher.dispatch(new ValueEvent<Double>(onAdd,
					calculate(event.getValueIndex()),
					event.getValueIndex()));
		} catch ( ValueException e ) {
			logger.error("Unexpected exception: ", e);
		}
	}
	
	private synchronized void onSourceValueUpdated(ValueEvent<Double> event) {
		try {
			dispatcher.dispatch(new ValueEvent<Double>(onUpd,
					calculate(event.getValueIndex()),
					event.getValueIndex()));
		} catch ( ValueException e ) {
			logger.error("Unexpected exception: ", e);
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
	 * @param index индекс исходного элемента ряда (всегда гарантировано &gt;=0)
	 * @return новое значение
	 * @throws ValueException - TODO:  
	 */
	abstract protected Double calculate(int index) throws ValueException;
	
	
	/**
	 * Сравнить базовые атрибуты.
	 * <p>
	 * @param other экземпляр для сравнения
	 * @return результат сравнения
	 */
	protected synchronized boolean fieldsEquals(_FLY<?> other) {
		return new EqualsBuilder()
			.append(other.id, id)
			.append(other.source, source)
			.isEquals();
	}

}
