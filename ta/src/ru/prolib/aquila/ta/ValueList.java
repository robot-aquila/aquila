package ru.prolib.aquila.ta;

import ru.prolib.aquila.util.Observable;

public interface ValueList extends Observable {

	public void update() throws ValueException;

	@SuppressWarnings("rawtypes")
	public void addValue(Value value) throws ValueExistsException;

	@SuppressWarnings("rawtypes")
	public Value getValue(String id) throws ValueNotExistsException;
	
	

}