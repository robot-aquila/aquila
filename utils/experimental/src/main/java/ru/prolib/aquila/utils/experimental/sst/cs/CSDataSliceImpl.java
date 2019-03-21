package ru.prolib.aquila.utils.experimental.sst.cs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.CSUtils;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleCloseSeries;
import ru.prolib.aquila.core.data.ObservableSeriesOLD;
import ru.prolib.aquila.core.data.ObservableSeriesImpl;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ZTFrame;

public class CSDataSliceImpl implements CSDataSlice {
	private final Symbol symbol;
	private final ZTFrame tf;
	private final ObservableSeriesImpl<Candle> candleSeries;
	private final CandleCloseSeries closeSeries;
	private final Map<String, Series<CDecimal>> indicators;
	private final Collection<Series<CDecimal>> indicatorsRO;
	
	public CSDataSliceImpl(Symbol symbol, ZTFrame tf, ObservableSeriesImpl<Candle> candleSeries) {
		this.symbol = symbol;
		this.tf = tf;
		this.candleSeries = candleSeries;
		this.closeSeries = new CandleCloseSeries(candleSeries);
		this.indicators = new HashMap<>();
		this.indicatorsRO = Collections.unmodifiableCollection(indicators.values());
	}
	
	public CSDataSliceImpl(Symbol symbol, ZTFrame tf, EventQueue queue) {
		this(symbol, tf, new CSUtils().createCandleSeries(queue, "OHLC"));
	}
	
	public CSDataSliceImpl(Symbol symbol, ZTFrame tf, Terminal terminal) {
		this(symbol, tf, new CSUtils().createCandleSeries(terminal, "OHLC"));
	}
	
	public ObservableSeriesImpl<Candle> getCandleSeries_() {
		return candleSeries;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice#getSymbol()
	 */
	@Override
	public Symbol getSymbol() {
		return symbol;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice#getTF()
	 */
	@Override
	public ZTFrame getTF() {
		return tf;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice#getCandleSeries()
	 */
	@Override
	public ObservableSeriesOLD<Candle> getCandleSeries() {
		return candleSeries;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice#getCandleCloseSeries()
	 */
	@Override
	public Series<CDecimal> getCandleCloseSeries() {
		return closeSeries;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice#getIndicator(java.lang.String)
	 */
	@Override
	public synchronized Series<CDecimal> getIndicator(String id) {
		Series<CDecimal> r = indicators.get(id);
		if ( r == null ) {
			throw new IllegalArgumentException("Indicator not found: " + id);
		}
		return r;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice#addIndicator(ru.prolib.aquila.core.data.Series)
	 */
	@Override
	public synchronized void addIndicator(Series<CDecimal> series) {
		String id = series.getId();
		Series<CDecimal> r = indicators.get(id);
		if ( r != null ) {
			throw new IllegalArgumentException("Indicator already exists: " + id);
		}
		indicators.put(id, series);
	}
	
	@Override
	public synchronized Collection<Series<CDecimal>> getIndicators() {
		return indicatorsRO;
	}

	@Override
	public synchronized boolean hasIndicator(String id) {
		return indicators.containsKey(id);
	}

}
