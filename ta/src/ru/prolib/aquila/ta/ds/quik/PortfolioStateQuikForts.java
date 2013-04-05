package ru.prolib.aquila.ta.ds.quik;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetException;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioState;
import ru.prolib.aquila.ChaosTheory.PortfolioTimeoutException;
import ru.prolib.aquila.rxltdde.Xlt.ITable;

/**
 * Состояние портфеля по секции FORTS через QUIK. 
 */
public class PortfolioStateQuikForts extends Observable
	implements PortfolioState,RXltDdeTableHandler
{
	private static final Logger logger = LoggerFactory.getLogger(PortfolioStateQuikForts.class);
	
	private static final String MONEY_LIMIT_TYPE = "Ден.средства";
	
	/**
	 * Идентификатор таблицы Позиции по клиентским счетам (фьючерсы)
	 * Таблица используется для определения текущей позиции. 
	 */
	private final String positionTable;
	
	/**
	 * Идентификатор таблицы Ограничения по клиентским счетам
	 * Таблица используется для определения доступных денежных средств.
	 */
	private final String limitsTable;
	
	private final String account;
	private final Asset asset;
	private double money = 0.0d;
	private double margin = 0.0d;
	private int pos = 0;
	
	public PortfolioStateQuikForts(String account, Asset asset,
							  String positionTable, String limitsTable)
	{
		super();
		this.account = account;
		this.asset = asset;
		this.positionTable = positionTable;
		this.limitsTable = limitsTable;
		logger.info("Configured for {} account and {} asset.",
				account, asset.getAssetCode());
		logger.info("Listening {} position table and {} limits table.",
				positionTable, limitsTable);
	}
	
	public String getAccount() {
		return account;
	}
	
	public Asset getAsset() {
		return asset;
	}
	
	public String getPositionTableName() {
		return positionTable;
	}
	
	public String getLimitsTableName() {
		return limitsTable;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.PortfolioState#getMoney()
	 */
	@Override
	public synchronized double getMoney() {
		return money;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.PortfolioState#getPosition()
	 */
	@Override
	public synchronized int getPosition() {
		return pos;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.quik.RXltDdeTableHandler#onTable(ru.prolib.aquila.rxltdde.Xlt.ITable)
	 */
	@Override
	public void onTable(ITable table) {
		String topic = table.getTopic(); 
		if ( topic.equals(limitsTable) ) {
			// Таблица - Доступные денежные средства
			// Требуются колонки:
			//		Торговый счет,
			//		Тип лимита,
			// 		Планируемая чистая позиция
			for ( int row = 0; row < table.getRows(); row ++ ) {
				if ( table.getCell(row, 0).equals(account)
					&& table.getCell(row, 1).equals(MONEY_LIMIT_TYPE) )
				{
					synchronized ( this ) {
						money = (Double)table.getCell(row, 2);
						setChanged();
						notifyObservers();
					}
				}
			}

		} else
		if ( topic.equals(positionTable) ) {
			// Таблица - Позиции по клиентским счетам (фьючерсы)
			// Требуются колонки:
			// 		Торговый счет,
			// 		Код инструмента,
			// 		Текущая чистая позиция,
			//		Вариационная маржа	// since 2012-01-07
			for ( int row = 0; row < table.getRows(); row ++ ) {
				if ( table.getCell(row, 0).equals(account)
					&& table.getCell(row, 1).equals(asset.getAssetCode()) )
				{
					synchronized ( this ) {
						pos = ((Double)table.getCell(row, 2)).intValue();
						margin = (Double)table.getCell(row, 3);
						setChanged();
						notifyObservers();
						notifyAll();
					}
				}
			}
			
		} else {
			logger.warn("Unknown table: {}", topic);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.quik.RXltDdeTableHandler#registerHandler(ru.prolib.aquila.ta.ds.quik.RXltDdeDispatcher)
	 */
	@Override
	public void registerHandler(RXltDdeDispatcher dispatcher) {
		dispatcher.add(limitsTable, this);
		dispatcher.add(positionTable, this);
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.quik.RXltDdeTableHandler#unregisterHandler(ru.prolib.aquila.ta.ds.quik.RXltDdeDispatcher)
	 */
	@Override
	public void unregisterHandler(RXltDdeDispatcher dispatcher) {
		dispatcher.remove(limitsTable, this);
		dispatcher.remove(positionTable, this);
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.PortfolioState#waitForNeutralPosition(long)
	 */
	@Override
	public synchronized void waitForNeutralPosition(long timeout)
		throws PortfolioTimeoutException,
			   PortfolioException,
			   InterruptedException
	{
		while ( pos != 0 && timeout > 0 ) {
			long start = System.currentTimeMillis();
			wait(timeout);
			timeout -= (System.currentTimeMillis() - start);
			if ( timeout <= 0 ) {
				throw new PortfolioTimeoutException("Position unchanged "
						+ timeout + " ms");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.PortfolioState#getVariationMargin()
	 */
	@Override
	public synchronized double getVariationMargin() throws PortfolioException {
		return margin;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.PortfolioState#getInitialMargin()
	 */
	@Override
	public synchronized double getInitialMargin() throws PortfolioException {
		try {
			return Math.abs(pos) * asset.getInitialMarginMoney();
		} catch ( AssetException e ) {
			throw new PortfolioException(e);
		}
	}

	@Override
	public double getTotalMoney() throws PortfolioException {
		return getMoney() + getInitialMargin() + getVariationMargin();
	}
	
}