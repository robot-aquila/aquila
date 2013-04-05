package ru.prolib.aquila.ta.ds.quik;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Export;
import ru.prolib.aquila.rxltdde.Xlt.ITable;
import ru.prolib.aquila.ta.DealImpl;
import ru.prolib.aquila.ta.ds.DealWriter;
import ru.prolib.aquila.ta.ds.DealWriterException;

/**
 * Сделка игнорируется, если комбинация времени сделки и номера сделки меньше
 * чем аналогичной комбинации последней обработанной сделки.
 */
public class ExportQuik implements Export,DealDispatcher,RXltDdeTableHandler {
	static private final Logger logger = LoggerFactory.getLogger(ExportQuik.class);
	protected final HashMap<String,DealWriter> map;
	protected ExportQuikActualityPoint actuality = null;
	private final String dealsTable;
	private final SimpleDateFormat dateFormat;
	
	public ExportQuik(String dealsTable) {
		super();
		this.dealsTable = dealsTable;
		map = new HashMap<String,DealWriter>();
		dateFormat = new SimpleDateFormat("dd.MM.yyyyHH:mm:ss");
		logger.info("Configured with {} deals table.", dealsTable);
	}
	
	public String getDealsTableName() {
		return dealsTable;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.quik.DealDispatcher#dispatch(long, java.util.Date, java.lang.String, double, long)
	 */
	@Override
	public synchronized void dispatch(long number, Date dealTime,
			String asset, double price, long qty)
	{
		ExportQuikActualityPoint ap = new ExportQuikActualityPoint(dealTime, number);
		DealWriter writer = map.get(asset);
		if ( writer != null && ! ap.before(actuality) ) {
			try {
				writer.addDeal(new DealImpl(dealTime, price, qty));
			} catch ( DealWriterException e ) {
				error("Dispatching error", e);
			}
		}
		
		actuality = ap;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.quik.DealDispatcher#flushAll()
	 */
	public synchronized void flushAll() {
		Iterator<DealWriter> i = map.values().iterator();
		while ( i.hasNext() ) {
			try {
				i.next().flush();
			} catch ( DealWriterException e ) {
				error("Flushing error", e);
			}
		}
	}
	
	/**
	 * Добавить обработчик сделок по бумаге
	 * @param asset - код бумаги
	 * @param writer - экземпляр обработчика
	 */
	public synchronized void attachWriter(String asset, DealWriter writer) {
		map.put(asset, writer);
	}
	
	/**
	 * Удалить обработчик сделок по бумаге
	 * @param asset - код бумаги
	 */
	public synchronized void detachWriter(String asset) {
		map.remove(asset);
	}
	
	public synchronized Map<String, DealWriter> getMap() {
		return new HashMap<String, DealWriter>(map);
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.quik.RXltDdeTableHandler#onTable(ru.prolib.aquila.rxltdde.Xlt.ITable)
	 */
	@Override
	public void onTable(ITable table) {
		if ( table.getCols() != 6 ) {
			logger.warn("Expected 6 cols for {} table, actual {}. Ignored.",
					dealsTable, table.getCols());
			return;
		}

		for ( int row = 0; row < table.getRows(); row ++ ) {
			try {
				Date dealTime = dateFormat.parse((String)table.getCell(row, 1)
											   + (String)table.getCell(row, 2));
				dispatch(((Double)table.getCell(row, 0)).longValue(),
						dealTime,
						(String)table.getCell(row, 3),
						(Double)table.getCell(row, 4),
						((Double)table.getCell(row, 5)).longValue());
			} catch ( Exception e ) {
				error("Error processing " + dealsTable +
					". Row " + row + " ignored", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.quik.RXltDdeTableHandler#registerHandler(ru.prolib.aquila.ta.ds.quik.RXltDdeDispatcher)
	 */
	@Override
	public void registerHandler(RXltDdeDispatcher dispatcher) {
		dispatcher.add(dealsTable, this);
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.quik.RXltDdeTableHandler#unregisterHandler(ru.prolib.aquila.ta.ds.quik.RXltDdeDispatcher)
	 */
	@Override
	public void unregisterHandler(RXltDdeDispatcher dispatcher) {
		dispatcher.remove(dealsTable, this);
	}
	
	private void error(String msg, Exception e) {
		if ( logger.isDebugEnabled() ) {
			logger.error(msg + ": " + e.getMessage(), e);
		} else {
			logger.error(msg + ": " + e.getMessage());
		}
	}

}