package ru.prolib.aquila.utils.experimental.sst.cs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.CSUtils;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleCloseSeries;
import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.core.data.ObservableSeriesImpl;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ta.QEMA;

public class CSDataSliceImpl implements CSDataSlice {
	private final Symbol symbol;
	private final TimeFrame tf;
	private final ObservableSeriesImpl<Candle> candleSeries;
	private final CandleCloseSeries closeSeries;
	private final Map<String, Series<Double>> indicators;
	private final Collection<Series<Double>> indicatorsRO;
	
	public CSDataSliceImpl(Symbol symbol, TimeFrame tf, ObservableSeriesImpl<Candle> candleSeries) {
		this.symbol = symbol;
		this.tf = tf;
		this.candleSeries = candleSeries;
		this.closeSeries = new CandleCloseSeries(candleSeries);
		this.indicators = new HashMap<>();
		this.indicatorsRO = Collections.unmodifiableCollection(indicators.values());
	}
	
	public CSDataSliceImpl(Symbol symbol, TimeFrame tf, EventQueue queue) {
		this(symbol, tf, new CSUtils().createCandleSeries(queue, "OHLC"));
	}
	
	public CSDataSliceImpl(Symbol symbol, TimeFrame tf, Terminal terminal) {
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
	public TimeFrame getTF() {
		return tf;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice#getCandleSeries()
	 */
	@Override
	public ObservableSeries<Candle> getCandleSeries() {
		return candleSeries;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice#getCandleCloseSeries()
	 */
	@Override
	public Series<Double> getCandleCloseSeries() {
		return closeSeries;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice#getIndicator(java.lang.String)
	 */
	@Override
	public synchronized Series<Double> getIndicator(String id) {
		Series<Double> r = indicators.get(id);
		if ( r == null ) {
			throw new IllegalArgumentException("Indicator not found: " + id);
		}
		return r;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice#addIndicator(ru.prolib.aquila.core.data.Series)
	 */
	@Override
	public synchronized void addIndicator(Series<Double> series) {
		String id = series.getId();
		Series<Double> r = indicators.get(id);
		if ( r != null ) {
			throw new IllegalArgumentException("Indicator already exists: " + id);
		}
		indicators.put(id, series);
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice#getQEMA(int)
	 */
	@Override
	public synchronized Series<Double> getQEMA(int period) {
		String id = "QEMA(" + period + ")";
		Series<Double> r = indicators.get(id);
		if ( r == null ) {
			r = new QEMA(id, closeSeries, period);
			addIndicator(r);
		}
		return r;
	}
	
	@Override
	public synchronized Collection<Series<Double>> getIndicators() {
		return indicatorsRO;
	}

}
