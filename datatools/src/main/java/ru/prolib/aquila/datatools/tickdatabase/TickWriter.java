package ru.prolib.aquila.datatools.tickdatabase;

import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;

public interface TickWriter {

	public void write(Tick tick) throws GeneralException;
	
	public void close();

}