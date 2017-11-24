package ru.prolib.aquila.utils.experimental.sst.cs;

import java.util.Collection;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ZTFrame;

public interface CSDataSlice {

	Symbol getSymbol();

	ZTFrame getTF();

	ObservableSeries<Candle> getCandleSeries();

	Series<CDecimal> getCandleCloseSeries();

	Series<CDecimal> getIndicator(String id);

	void addIndicator(Series<CDecimal> series);
	
	boolean hasIndicator(String id);
	
	Collection<Series<CDecimal>> getIndicators();

}