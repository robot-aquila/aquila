package ru.prolib.aquila.ta.ds.quik;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetImpl;
import ru.prolib.aquila.ChaosTheory.AssetsImpl;
import ru.prolib.aquila.rxltdde.Xlt.ITable;

/**
 * Источник информации об активах на основе таблиц квика.
 * Для получения базовой информации используется таблица текущих параметров.
 * Отслеживание цены выполняется по таблице всех сделок.
 */
public class AssetsQuik extends AssetsImpl implements RXltDdeTableHandler {
	static private final Logger logger = LoggerFactory.getLogger(AssetsQuik.class);
	
	private final String assetsTable;
	private final String dealsTable;
	
	/**
	 * Конструктор.
	 * @param assetsTable топик таблицы текущих параметров
	 * @param dealsTable топин таблицы всех сделок
	 */
	public AssetsQuik(String assetsTable, String dealsTable) {
		super();
		this.assetsTable = assetsTable;
		this.dealsTable = dealsTable;
		logger.info("Configured with {} assets table and {} deals table.",
				assetsTable, dealsTable);
	}

	@Override
	public void onTable(ITable table) {
		String topic = table.getTopic();
		if ( topic.equals(assetsTable) ) {
			onAssetsTable(table);
		} else
		if ( topic.equals(dealsTable) ) {
			onDealsTable(table);
		} else {
			logger.warn("Unknown table: {}", topic);
		}
	}
	
	/**
	 * Обработка таблицы текущих параметров.
	 * Ожидаются колонки:
	 * 	Код бумаги,
	 * 	Код класса,
	 * 	Шаг цены,
	 * 	Точность,
	 * 	Расчетная цена,			// since 2012-01-07
	 *  Стоимость шага цены,
	 *  ГО покупателя
	 * @param table
	 */
	private void onAssetsTable(ITable table) {
		if ( table.getCols() != 7 ) {
			logger.warn("Expected 7 cols for {} table, actual {}. Ignored.",
					assetsTable, table.getCols());
			return;
		}
		for ( int row = 0; row < table.getRows(); row ++ ) {
			try {
				String assetCode = (String) table.getCell(row, 0);
				synchronized ( this ) {
					if ( exists(assetCode) ) {
						AssetImpl asset = (AssetImpl) getByCode(assetCode);
						updateClearingAttrs(asset, table, row);
						asset.notifyObservers(Asset.EVENT_CLEARING);
					} else {
						AssetImpl asset = new AssetImpl(assetCode,
							(String)table.getCell(row, 1),
							(Double)table.getCell(row, 2),
							((Double)table.getCell(row, 3)).intValue());
						add(asset);
						asset.updatePrice((Double)table.getCell(row, 4));
						updateClearingAttrs(asset, table, row);
						logger.info("New asset added: {}", assetCode);
						asset.notifyObservers(Asset.EVENT_CLEARING);
					}
				}
			} catch ( Exception e ) {
				Object args[] = null;
				if ( logger.isDebugEnabled() ) {
					args = new Object[] { assetsTable, row, e };
				} else {
					args = new Object[] { assetsTable, row };
				}
				logger.error("Error processing {} table. Row {} ignored.",args);
			}
		}
	}
	
	private void updateClearingAttrs(AssetImpl asset, ITable table, int row) {
		asset.updateEstimatedPrice((Double)table.getCell(row, 4));
		asset.updatePriceStepMoney((Double)table.getCell(row, 5));
		asset.updateInitialMarginMoney((Double)table.getCell(row, 6));		
	}
	
	/**
	 * Обработка таблицы всех сделок.
	 * Ожидаются колонки: Номер, Дата, Время, Код бумаги, Цена, Кол-во
	 * @param table
	 */
	private void onDealsTable(ITable table) {
		if ( table.getCols() != 6 ) {
			logger.warn("Expected 6 cols for {} table, actual {}. Ignored.",
					dealsTable, table.getCols());
			return;
		}
		HashMap<String, Double> cache = new HashMap<String, Double>();
		for ( int row = 0; row < table.getRows(); row ++ ) {
			try {
				cache.put((String)table.getCell(row, 3),
						  (Double)table.getCell(row, 4));
			} catch ( Exception e ) {
				Object args[] = null;
				if ( logger.isDebugEnabled() ) {
					args = new Object[] { dealsTable, row, e };
				} else {
					args = new Object[] { dealsTable, row };
				}
				logger.error("Error processing {} table. Row {} ignored.",args);
			}
		}
		Iterator<Map.Entry<String, Double>> i = null;
		for ( i = cache.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry<String, Double> entry = i.next();
			synchronized ( this ) {
				try {
					if ( exists(entry.getKey()) ) {
						AssetImpl asset = (AssetImpl)getByCode(entry.getKey());
						asset.updatePrice(entry.getValue());
						asset.notifyObservers(Asset.EVENT_PRICE);
					}
				} catch ( Exception e ) {
					Object args[] = null;
					if ( logger.isDebugEnabled() ) {
						args = new Object[] { dealsTable, e.getMessage(), e };
					} else {
						args = new Object[] { dealsTable, e.getMessage() };
					}
					logger.error("Exception during processing {} table: {}",
							args);
				}
			}
		}
	}

	@Override
	public void registerHandler(RXltDdeDispatcher dispatcher) {
		dispatcher.add(assetsTable, this);
		dispatcher.add(dealsTable, this);
	}

	@Override
	public void unregisterHandler(RXltDdeDispatcher dispatcher) {
		dispatcher.remove(assetsTable, this);
		dispatcher.remove(dealsTable, this);
	}

}
