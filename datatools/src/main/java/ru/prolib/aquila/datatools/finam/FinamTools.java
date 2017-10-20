package ru.prolib.aquila.datatools.finam;

import java.io.File;

import ru.prolib.aquila.datatools.tickdatabase.TickDatabase;
import ru.prolib.aquila.datatools.tickdatabase.simple.SimpleTickDatabase;

public class FinamTools {
	
	public static TickDatabase newTickDatabase(File dbpath) throws Exception {
		return new SimpleTickDatabase(new CsvDataSegmentManager(dbpath));
	}

}
