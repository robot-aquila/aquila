package ru.prolib.aquila.web.utils.moex.data;

import java.io.File;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.data.SymbolUpdateSource;
import ru.prolib.aquila.probe.datasim.SymbolUpdateSourceImpl;
import ru.prolib.aquila.web.utils.moex.MoexSymbolUpdateReaderFactory;

public class MoexData {
	
	public SymbolUpdateSource createSymbolUpdateSource(File dataRootDir, Scheduler scheduler) {
		return new SymbolUpdateSourceImpl(scheduler, new MoexSymbolUpdateReaderFactory(dataRootDir));
	}

}
