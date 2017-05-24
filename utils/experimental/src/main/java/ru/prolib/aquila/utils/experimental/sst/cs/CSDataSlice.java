package ru.prolib.aquila.utils.experimental.sst.cs;

import java.util.Collection;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.TimeFrame;

public interface CSDataSlice {

	Symbol getSymbol();

	TimeFrame getTF();

	ObservableSeries<Candle> getCandleSeries();

	Series<Double> getCandleCloseSeries();

	Series<Double> getIndicator(String id);

	void addIndicator(Series<Double> series);
	
	boolean hasIndicator(String id);
	
	Collection<Series<Double>> getIndicators();

}