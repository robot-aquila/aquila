package ru.prolib.aquila.ta;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Список значений.
 * Аггрегирует значения разного типа и позволяет выполнять обновление одним
 * вызовом. 
 */
public class ValueListImpl extends Observable implements ValueList {
	private final LinkedHashMap<String, Value<?>> values;
	
	public ValueListImpl() {
		values = new LinkedHashMap<String, Value<?>>();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ValueList#update()
	 */
	@Override
	public synchronized void update() throws ValueException {
		Iterator<Map.Entry<String, Value<?>>> i = values.entrySet().iterator();
		while ( i.hasNext() ) {
			Map.Entry<String, Value<?>> entry = i.next();
			entry.getValue().update();
		}
		setChanged();
		notifyObservers();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ValueList#addValue(ru.prolib.aquila.ta.Value)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public synchronized void addValue(Value value)
		throws ValueExistsException
	{
		addValue(value.getId(), value);
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized void addValue(String id, Value value)
		throws ValueExistsException
	{
		if ( values.containsKey(id) ) {
			throw new ValueExistsException(id);
		}
		values.put(id, value);
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ValueList#getValue(int)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public synchronized Value getValue(String id)
		throws ValueNotExistsException
	{
		Value value = values.get(id);
		if ( value == null ) {
			throw new ValueNotExistsException(id);
		}
		return value;
	}

	@Override
	public void addObserver(Observer o) {
		super.addObserver(o);
	}

	@Override
	public void deleteObserver(Observer o) {
		super.deleteObserver(o);
	}

	@Override
	public void deleteObservers() {
		super.deleteObservers();
	}

}
