package ru.prolib.aquila.core.BusinessEntities;

import java.util.Hashtable;
import java.util.Map;

/**
 * Модифицируемый объект.
 * <p>
 * 2012-09-03<br>
 * $Id: EditableImpl.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class EditableImpl implements Editable {
	private boolean changed = false;
	private boolean available = false;
	private final Map<Integer, Boolean> changes;

	/**
	 * Конструктор. 
	 */
	public EditableImpl() {
		super();
		changes = new Hashtable<Integer, Boolean>();
	}

	@Override
	public synchronized boolean hasChanged() {
		return changed;
	}
	
	@Override
	public synchronized boolean hasChanged(Integer changeId) {
		Boolean flag = changes.get(changeId);
		return flag != null && flag == true;
	}

	@Override
	public synchronized void resetChanges() {
		changes.clear();
		changed = false;
	}

	@Override
	public synchronized void setChanged() {
		changed = true;
	}
	
	@Override
	public synchronized void setChanged(Integer markerId) {
		changes.put(markerId, true);
		changed = true;
	}

	@Override
	public synchronized void setAvailable(boolean flag) {
		available = flag;
	}

	@Override
	public synchronized boolean isAvailable() {
		return available;
	}

	@Override
	public void fireChangedEvent() {
		
	}

}