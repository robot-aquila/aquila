package ru.prolib.aquila.ta.ds;

import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueImpl;
import ru.prolib.aquila.ta.ValueUpdateException;

@Deprecated
public class DataSetString extends ValueImpl<String> {
	private final ru.prolib.aquila.ta.indicator.DataSetString value;

	public DataSetString(DataSet dataSet, String name) {
		this(ValueImpl.DEFAULT_ID, dataSet, name);
	}
	
	public DataSetString(String valueId, DataSet dataSet, String name) {
		super(valueId);
		value = new ru.prolib.aquila.ta.indicator.DataSetString(dataSet, name);
	}

	@Override
	public void update() throws ValueUpdateException {
		try {
			add(value.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}
	
	public DataSet getDataSet() {
		return value.getDataSet();
	}
	
	public String getName() {
		return value.getName();
	}

}
