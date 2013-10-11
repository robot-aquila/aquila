package ru.prolib.aquila.core.data;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;

/**
 * Значение неопределенного типа.
 * <p>
 * Опционально поддерживает ограничение размера хранилища значений. 
 * <p>
 * @param <T> - тип значения
 * <p>
 * 2012-04-17<br>
 * $Id: SeriesImpl.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class SeriesImpl<T> implements EditableSeries<T> {
	/**
	 * Константа, отключающая реальное ограничение размера хранилища значений.
	 * Это означает, что история будет накапливаться до тех пор, пока не
	 * закончится память.  
	 */
	public static final int STORAGE_NOT_LIMITED = 0;
	
	private final String id;
	private final int limit;
	private final LinkedList<T> history = new LinkedList<T>();
	private final EventDispatcher dispatcher;
	private final EventType onAdd, onUpd;
	
	/**
	 * Реальный индекс первого элемента в хранилище.
	 */
	private int offset = 0;
	
	/**
	 * Создать ряд.
	 * <p>
	 * В качестве идентификатора значения используется {@link Series#DEFAULT_ID}.
	 * Ограничение размера хранилища используется.
	 */
	public SeriesImpl() {
		this(Series.DEFAULT_ID);
	}
	
	/**
	 * Создать ряд.
	 * <p>
	 * Ограничение размера хранилища используется.
	 * <p>
	 * @param valueId идентификатор ряда
	 */
	public SeriesImpl(String valueId) {
		this(valueId, STORAGE_NOT_LIMITED);
	}
	
	/**
	 * Создать ряд.
	 * <p>
	 * @param valueId идентификатор ряда
	 * @param storageLimit ограничение размера хранилища. Если меньше нуля, то
	 * используется {@link #STORAGE_NOT_LIMITED}
	 */
	public SeriesImpl(String valueId, int storageLimit) {
		super();
		if ( valueId == null ) {
			throw new NullPointerException("Id cannot be null");
		}
		if ( storageLimit < 0 ) {
			storageLimit = STORAGE_NOT_LIMITED;
		}
		id = valueId;
		limit = storageLimit;
		dispatcher = new EventDispatcherImpl(new SimpleEventQueue(), id);
		onAdd = dispatcher.createType("Add");
		onUpd = dispatcher.createType("Upd");
	}

	/**
	 * Получить лимит хранилища данных.
	 * <p>
	 * Лимит может быть определен при создании экземпляра. Лимит определяет
	 * реальный размер хранилища значений. Когда количество исторических
	 * значений превышает установленный лимит в два раза, то наиболее ранние
	 * значения удаляются. При этом, индексы исторических значений, используемые
	 * как аргумент {@link #get(int)} и длина истории, получаемая через
	 * {@link #getLength()}, не связаны с содержимым хранилища. Индексы
	 * исторических значений постоянны, а длина исторических данных всегда
	 * определяет суммарное количество исторических значений.
	 * <p>
	 * @return количество реально-доступных значений или
	 * {@link #STORAGE_NOT_LIMITED}, если лимит не используется. 
	 */
	public int getStorageLimit() {
		return limit;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public synchronized void add(T value) throws ValueException {
		Event event = null;
		synchronized ( this ) {
			event = new ValueEvent<T>(onAdd, value, history.size());
			history.add(value);
			if ( limit > 0 ) {
				if ( history.size() >= limit * 2 ) {
					history.subList(0, limit).clear();
					offset += limit;
				}
			}
		}
		dispatcher.dispatch(event);
	}

	@Override
	public synchronized T get() throws ValueException {
		try {
			return history.getLast();
		} catch ( NoSuchElementException e ) {
			throw new ValueNotExistsException();
		}
	}

	@Override
	public synchronized T get(int index) throws ValueException {
		try {
			return history.get(normalizeIndex(index));
		} catch ( IndexOutOfBoundsException e ) {
			throw new ValueOutOfRangeException(e);
		}
	}

	@Override
	public synchronized int getLength() {
		return history.size() + offset;
	}
	
	@Override
	public synchronized void set(T value) throws ValueException {
		int index = history.size() - 1;
		T oldValue = get(index);
		history.set(index, value);
		dispatcher.dispatch(new ValueEvent<T>(onUpd, oldValue, value, index));
	}

	@Override
	public synchronized void clear() {
		history.clear();
		offset = 0;
	}

	/**
	 * Нормализовать индекс элемента.
	 * <p>
	 * Метод преобразует указанный индекс в реальный индекс элемента внутри
	 * массива значений. 
	 * <p>
	 * @param index исходный индекс
	 * @return нормализованный индекс
	 * @throws ValueException
	 */
	protected int normalizeIndex(int index) throws ValueException {
		int length = history.size();
		if ( length == 0 ) {
			throw new ValueNotExistsException();
		}
		if ( index >= 0 ) {
			index -= offset;
		} else {
			index = length - 1 + index;
		}
		if ( index < 0 ) {
			throw new ValueOutOfDateException();
		}
		return index;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == SeriesImpl.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}
	
	/**
	 * Сравнить значения атрибутов ряда.
	 * <p>
	 * @param other объект для сравнения
	 * @return результат сравнения
	 */
	protected boolean fieldsEquals(Object other) {
		SeriesImpl<?> o = (SeriesImpl<?>) other;
		return new EqualsBuilder()
			.append(id, o.id)
			.append(limit, o.limit)
			.append(history, o.history)
			.append(offset, o.offset)
			.isEquals();		
	}

	@Override
	public EventType OnAdd() {
		return onAdd;
	}

	@Override
	public EventType OnUpd() {
		return onUpd;
	}

}
