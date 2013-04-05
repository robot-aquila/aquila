package ru.prolib.aquila.ta.ds;

public class DataSetValueNotExistsException extends DataSetException {
	private static final long serialVersionUID = 1L;

	public DataSetValueNotExistsException(String name) {
		super("Value not exists: " + name);
	}

}
