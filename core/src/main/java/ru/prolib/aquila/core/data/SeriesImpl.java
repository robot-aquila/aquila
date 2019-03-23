package ru.prolib.aquila.core.data;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.core.concurrency.LID;

/**
 * Ряд значений неопределенного типа.
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
	private final ArrayList<T> history = new ArrayList<T>();
	private final LID lid;
	private final Lock lock = new ReentrantLock();
	
	/**
	 * Реальный индекс первого элемента в хранилище.
	 */
	private int offset = 0;
	
	/**
	 * Создать ряд.
	 * <p>
	 * В качестве идентификатора значения используется {@link Series#DEFAULT_ID}.
	 * Ограничение размера хранилища не используется.
	 */
	public SeriesImpl() {
		this(Series.DEFAULT_ID);
	}
	
	/**
	 * Создать ряд.
	 * <p>
	 * Ограничение размера хранилища не используется.
	 * <p>
	 * @param valueId строковый идентификатор ряда
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
		lid = LID.createInstance();
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
	public void add(T value) throws ValueException {
		lock();
		try {
			history.add(value);
			if ( limit > 0 ) {
				if ( history.size() >= limit * 2 ) {
					history.subList(0, limit).clear();
					offset += limit;
				}
			}
		} finally {
			unlock();
		}
	}

	@Override
	public T get() throws ValueException {
		lock();
		try {
			return history.get(history.size() - 1);
		} catch ( ArrayIndexOutOfBoundsException e ) {
			throw new ValueNotExistsException();
		} finally {
			unlock();
		}
	}

	@Override
	public T get(int index) throws ValueException {
		lock();
		try {
			return history.get(normalizeIndex(index));
		} catch ( IndexOutOfBoundsException e ) {
			throw new ValueOutOfRangeException(e);
		} finally {
			unlock();
		}
	}

	@Override
	public int getLength() {
		lock();
		try {
			return history.size() + offset;
		} finally {
			unlock();
		}
	}
	
	@Override
	public void set(T value) throws ValueException {
		lock();
		try {
			int index = history.size() - 1;
			history.set(index, value);
		} catch ( ArrayIndexOutOfBoundsException e ) {
			throw new ValueNotExistsException();
		} finally {
			unlock();
		}
	}

	@Override
	public void clear() {
		lock();
		try {
			history.clear();
			offset = 0;
		} finally {
			unlock();
		}
	}

	/**
	 * Нормализовать индекс элемента.
	 * <p>
	 * Метод преобразует указанный индекс в реальный индекс элемента внутри
	 * массива значений. 
	 * <p>
	 * @param index исходный индекс
	 * @return нормализованный индекс
	 * @throws ValueException - If error occured.
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
	public LID getLID() {
		return lid;
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}
	
	@Override
	public void truncate(int new_length) {
		if ( new_length < 0 ) {
			throw new IllegalArgumentException("Expected positive or zero but: " + new_length);
		}
		lock();
		try {
			int hlen = history.size();
			if ( new_length > offset + hlen ) {
				return;
			}
			int start = new_length - offset;
			if ( start < 0 ) {
				throw new IllegalArgumentException("Minimum possible length is " + offset + " but " + start);
			}
			history.subList(start, hlen).clear();
		} finally {
			unlock();
		}
	}

}
