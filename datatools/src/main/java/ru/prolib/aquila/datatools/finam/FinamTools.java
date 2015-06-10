package ru.prolib.aquila.datatools.finam;

import java.io.File;

import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.tickdatabase.TickDatabase;
import ru.prolib.aquila.datatools.tickdatabase.simple.DataWriterFactoryImpl;
import ru.prolib.aquila.datatools.tickdatabase.simple.SimpleTickDatabase;
import ru.prolib.aquila.datatools.tickdatabase.util.SmartFlushSetup;

public class FinamTools {
	
	public static TickDatabase newTickDatabase(Terminal terminal, File dbpath)
			throws GeneralException
	{
		return new SimpleTickDatabase(new DataWriterFactoryImpl(
			new CsvDataSegmentManager(terminal, dbpath, new SchedulerLocal(),
					new SmartFlushSetup())));
	}

}
