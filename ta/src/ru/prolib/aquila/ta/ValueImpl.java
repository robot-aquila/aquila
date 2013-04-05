package ru.prolib.aquila.ta;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Значение неопределенного типа.
 *
 * @param <T> - тип значения
 */
public class ValueImpl<T> implements Value<T> {
	/**
	 * Идентификатор значения по-умолчанию.
	 */
	public static final String DEFAULT_ID = "default";
	
	/**
	 * Константа, отключающая реальное ограничение длины истории значений.
	 * Это означает, что история будет накапливаться до тех пор, пока не
	 * закончится память.  
	 */
	public static final int LENGTH_NOT_LIMITED = 0;
	
	private final String id;
	private final int limit;
	private final LinkedList<T> history = new LinkedList<T>();
	
	/**
	 * Реальный индекс первого элемента в хранилище.
	 */
	private int offset = 0;
	
	/**
	 * Конструктор по-умолчанию.
	 * В качестве идентификатора значения используется {@link #DEFAULT_ID}.
	 * Реальное ограничение длины не используется.  
	 */
	public ValueImpl() {
		this(DEFAULT_ID);
	}
	
	/**
	 * Конструктор с идентификатором значения.
	 * Реальное ограничение длины не используется.
	 * @param valueId идентификатор значения
	 */
	public ValueImpl(String valueId) {
		this(valueId, LENGTH_NOT_LIMITED);
	}
	
	/**
	 * Конструктор.
	 * @param valueId идентификатор значения
	 * @param lengthLimit реальное ограничение длины. Если меньше нуля, то
	 * используется {@link #LENGTH_NOT_LIMITED}
	 */
	public ValueImpl(String valueId, int lengthLimit) {
		super();
		if ( valueId == null ) {
			throw new NullPointerException("valueId");
		}
		if ( lengthLimit < 0 ) {
			lengthLimit = LENGTH_NOT_LIMITED;
		}
		id = valueId;
		limit = lengthLimit;
	}
	
	/**
	 * Получить реальный лимит длины исторических данных.
	 * 
	 * Лимит может быть определен при создании экземпляра. Лимит определяет
	 * реальный размер хранилища значений. Когда количество исторических
	 * значений превышает установленный лимит в два раза, то наиболее ранние
	 * значения удаляются. При этом, индексы исторических значений, используемые
	 * как аргумент {@link #get(int)} и длина истории, получаемая через
	 * {@link #getLength()}, не связаны с содержимым хранилища. Индексы
	 * исторических значений постоянны, а длина исторических данных всегда
	 * определяет суммарное количество исторических значений.
	 * 
	 * @return количество реально-доступных значений или
	 * {@link #LENGTH_NOT_LIMITED}, если лимит не используется. 
	 */
	public int getLengthLimit() {
		return limit;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public synchronized void add(T value) throws ValueException {
		history.add(value);
		if ( limit > 0 ) {
			if ( history.size() >= limit * 2 ) {
				history.subList(0, limit).clear();
				offset += limit;
			}
		}
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
			if ( index >= 0 ) {
				index -= offset;
			} else {
				index = history.size() - 1 + index;
			}
			if ( index < 0 ) {
				throw new ValueOutOfDateException();
			}
			return history.get(index);
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
		int length = history.size();
		if ( length == 0 ) {
			throw new ValueNotExistsException();
		}
		history.set(length - 1, value);
	}
	
	@Override
	public synchronized void update() throws ValueUpdateException {
		throw new ValueUpdateException("Not implemented");
	}

}