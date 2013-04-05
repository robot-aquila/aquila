package ru.prolib.aquila.ta.ds;

import ru.prolib.aquila.core.data.Candle;

/**
 * Райтер, при каждом запросе на добавление бара выполняет назначение указанного
 * бара в качестве текущего бара для экземпляра {@link DataSetBar}.
 */
public class BarWriterDataSetBar implements BarWriter {
	private final DataSetBar set;
	
	public BarWriterDataSetBar(DataSetBar dataSet) {
		super();
		set = dataSet;
	}

	@Override
	public boolean addBar(Candle bar) throws BarWriterException {
		set.setBar(bar);
		return true;
	}

	@Override
	public boolean flush() throws BarWriterException {
		return false;
	}

}
