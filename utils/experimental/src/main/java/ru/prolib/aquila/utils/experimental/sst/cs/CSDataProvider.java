package ru.prolib.aquila.utils.experimental.sst.cs;

import java.util.Collection;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.TimeFrame;

public interface CSDataProvider {
	
	CSDataSlice getSlice(Symbol symbol, TimeFrame tf);
	
	Collection<CSDataSlice> getSlices(Symbol symbol);
	
	void start();
	
	void stop();
	
	Collection<Symbol> getSymbols();
	
	Collection<TimeFrame> getTimeFrames(Symbol symbol);

}
