package ru.prolib.aquila.utils.experimental.sst.cs;

import java.util.Collection;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.ZTFrame;

public interface CSDataProvider {
	
	CSDataSlice getSlice(Symbol symbol, ZTFrame tf);
	
	Collection<CSDataSlice> getSlices(Symbol symbol);
	
	void start();
	
	void stop();
	
	Collection<Symbol> getSymbols();
	
	Collection<ZTFrame> getTimeFrames(Symbol symbol);

}
