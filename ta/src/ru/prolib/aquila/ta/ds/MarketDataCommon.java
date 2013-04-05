package ru.prolib.aquila.ta.ds;

import java.util.Date;
import java.util.Observable;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueExistsException;
import ru.prolib.aquila.ta.ValueList;
import ru.prolib.aquila.ta.ValueNotExistsException;
import ru.prolib.aquila.ta.indicator.BollingerBands;
import ru.prolib.aquila.ta.math.Alligator;
import ru.prolib.aquila.ta.math.BollingerBand;
import ru.prolib.aquila.ta.math.Cross;
import ru.prolib.aquila.ta.math.Ema;
import ru.prolib.aquila.ta.math.Max;
import ru.prolib.aquila.ta.math.Median;
import ru.prolib.aquila.ta.math.Min;
import ru.prolib.aquila.ta.math.QuikSmma;
import ru.prolib.aquila.ta.math.Shift;
import ru.prolib.aquila.ta.math.Sma;
import ru.prolib.aquila.ta.math.Smma;
import ru.prolib.aquila.ta.math.Stdev;
import ru.prolib.aquila.ta.math.Sub;
import ru.prolib.aquila.ta.math.WilliamsZones;

abstract public class MarketDataCommon
	extends Observable implements MarketData
{
	protected final ValueList values;

	public MarketDataCommon(ValueList values) {
		super();
		this.values = values;
	}

	/**
	 * Получить обслуживаемый список значений
	 * @return
	 */
	public ValueList getValueList() {
		return values;
	}
	
	@Override
	public synchronized Candle getBar() throws ValueException {
		return getBar(getLastBarIndex());
	}
	
	@Override
	public synchronized Candle getBar(int index) throws ValueException {
		return new Candle(index >= 0 ? index : getLastBarIndex() + index,
				getTime().get(index),
				getOpen().get(index),
				getHigh().get(index),
				getLow().get(index),
				getClose().get(index),
				getVolume().get(index).longValue());
	}
	
	@Override
	public synchronized int getLength() {
		try {
			return getTime().getLength();
		} catch ( ValueException e ) {
			return 0;
		}
	}
	
	@Override
	public synchronized int getLastBarIndex() {
		return getLength() - 1;
	}

	@Override
	public synchronized Median getMedian() throws ValueException {
		return (Median) values.getValue(MEDIAN);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Value<Double> getClose() throws ValueException {
		return values.getValue(CLOSE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Value<Double> getHigh() throws ValueException {
		return values.getValue(HIGH);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Value<Double> getLow() throws ValueException {
		return values.getValue(LOW);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Value<Double> getOpen() throws ValueException {
		return values.getValue(OPEN);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Value<Date> getTime() throws ValueException {
		return values.getValue(TIME);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Value<Double> getVolume() throws ValueException {
		return values.getValue(VOL);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public synchronized void addValue(Value value)
			throws ValueExistsException
	{
		values.addValue(value);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public synchronized Value getValue(String id)
		throws ValueNotExistsException
	{
		return values.getValue(id);
	}

	@SuppressWarnings("unchecked" )
	@Override
	public synchronized Cross addCross(String src1, String src2, String id)
			throws ValueException
	{
		Cross value = new Cross(getValue(src1), getValue(src2), id);
		addValue(value);
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Ema addEma(String src, int period, String id)
			throws ValueException
	{
		Ema value = new Ema(getValue(src), period, id);
		addValue(value);
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Sma addSma(String src, int period, String id)
			throws ValueException
	{
		Sma value = new Sma(getValue(src), period, id);
		addValue(value);
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Smma addSmma(String src, int period, String id)
		throws ValueException
	{
		Smma value = new Smma(getValue(src), period, id);
		addValue(value);
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized QuikSmma addQuikSmma(String src, int period,
			String id) throws ValueException {
				QuikSmma value = new QuikSmma(getValue(src), period, id); 
				addValue(value);
				return value;
			}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Median addMedian(String src1, String src2, String id)
			throws ValueException {
				Median value = new Median(getValue(src1), getValue(src2), id);
				addValue(value);
				return value;
			}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Shift<Double> addShift(String src, int period, String id)
			throws ValueException {
				Shift<Double> value = new Shift<Double>(getValue(src), period, id);
				addValue(value);
				return value;
			}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Sub addSub(String one, String two, String id)
			throws ValueException {
				Sub value = new Sub(getValue(one), getValue(two), id);
				addValue(value);
				return value;
			}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized WilliamsZones addWilliamsZones(String ao, String ac,
			String id) throws ValueException {
				WilliamsZones value = new WilliamsZones(getValue(ao), getValue(ac), id);
				addValue(value);
				return value;
			}

	@Override
	public synchronized Sub addAwesomeOscillator(String id)
			throws ValueException
	{
		String ma5 = id + ".ma5";
		String ma34 = id + ".ma34";
		addSma(MEDIAN, 5, ma5);
		addSma(MEDIAN, 34, ma34);
		Sub value = addSub(ma5, ma34, id);
		return value;
	}

	@Override
	public synchronized Sub addAccelerationOscillator(String ao, String id)
		throws ValueException
	{
		addSma("ao", 5, id + ".ma5");
		return addSub("ao", id + ".ma5", id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Alligator addAlligator(String id) throws ValueException {
		addQuikSmma(MEDIAN,  5, id + ".ma5");
		addQuikSmma(MEDIAN,  8, id + ".ma8");
		addQuikSmma(MEDIAN, 13, id + ".ma13");
		addShift(id + ".ma5",  3, id + ".lips");
		addShift(id + ".ma8",  5, id + ".teeth");
		addShift(id + ".ma13", 8, id + ".jaw");
		return new Alligator(getValue(id + ".lips"),
							 getValue(id + ".teeth"),
							 getValue(id + ".jaw"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Max addMax(String src, int periods, String maxId)
		throws ValueException
	{
		Max value = new Max(getValue(src), periods, maxId);
		addValue(value);
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Min addMin(String src, int periods, String minId)
		throws ValueException
	{
		Min value = new Min(getValue(src), periods, minId);
		addValue(value);
		return value;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BollingerBands addBollingerBands(String src, int period,
		double factor, String id) throws ValueException
	{
		Sma ma = addSma(src, period, id + ".central");
		Stdev dev = new Stdev(getValue(src), period, id + ".stdev");
		BollingerBand upper = new BollingerBand(ma, dev, factor, id + ".upper");
		BollingerBand lower = new BollingerBand(ma, dev,-factor, id + ".lower");
		addValue(dev);
		addValue(upper);
		addValue(lower);
		return new BollingerBands(ma, upper, lower);
	}
}