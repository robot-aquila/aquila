package ru.prolib.aquila.datatools.tickdatabase;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.Tick;

public interface TickWriter extends Closeable, Flushable {

	public void write(Tick tick) throws IOException;

}