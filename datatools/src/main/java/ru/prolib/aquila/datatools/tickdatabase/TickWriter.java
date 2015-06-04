package ru.prolib.aquila.datatools.tickdatabase;

import java.io.Closeable;
import java.io.Flushable;

import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;

public interface TickWriter extends Closeable, Flushable {

	public void write(Tick tick) throws GeneralException;

}