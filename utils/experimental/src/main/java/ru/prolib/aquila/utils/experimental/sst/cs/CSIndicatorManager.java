package ru.prolib.aquila.utils.experimental.sst.cs;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ta.QEMA;

public class CSIndicatorManager {
	private static final CSIndicatorManager instance = new CSIndicatorManager();
	
	public static CSIndicatorManager getInstance() {
		return instance;
	}
	
	public Series<Double> getQEMA(CSDataSlice slice, int period) {
		String id = "QEMA(" + period + ")";
		synchronized ( slice ) {
			if ( slice.hasIndicator(id) ) {
				return slice.getIndicator(id);
			}
			Series<Double> r = new QEMA(id, slice.getCandleCloseSeries(), period);
			slice.addIndicator(r);
			return r;
		}
	}

}
