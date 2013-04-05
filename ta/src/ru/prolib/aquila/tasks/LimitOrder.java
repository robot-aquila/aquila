package ru.prolib.aquila.tasks;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetException;
import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.Portfolio;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.ta.ds.MarketData;

/**
 * Заготовка задачи, связанной с исполнением лимитной заявки.
 * Задача считается исполненной, когда исполняется лимитная заявка. Если
 * установлено ограничение по количеству баров, в течение которых заявка
 * выставлена, то в случае превышения указанного количества заявка снимается
 * а задача переводится в статус {@link Task#CANCELLED}. Вызов метода
 * {@link #cancel()} приводит к снятию заявки, если она выставлена и активна.
 * 
 * 2012-02-14
 * $Id: LimitOrder.java 201 2012-04-03 14:45:43Z whirlwind $
 */
public abstract class LimitOrder extends TaskUsesLocator implements Observer {
	protected MarketData data;
	protected Portfolio portfolio;
	protected Asset asset;
	protected Order order;
	protected Integer limitBars;
	protected Double price;
	protected String comment;
	protected int qty = 1;
	protected int firstBar = -1;

	/**
	 * Конструктор.
	 * 
	 * @param locator
	 * @throws Exception
	 */
	public LimitOrder(ServiceLocator locator) {
		super(locator);
		try {
			data = locator.getMarketData();
			portfolio = locator.getPortfolio();
			asset = portfolio.getAsset();
		} catch ( ServiceLocatorException e ) {
			debugException("Initialize failed", e);
			throw new RuntimeException("Initialize failed", e);
		}
	}

	/**
	 * Пытаться исполнить не более чем указанное количество баров.
	 * 
	 * Если к моменту last bar index + numBars заявка не исполнена, значит она
	 * будет снята, а задача завершена со статусом отменено. По умолчанию
	 * это ограничение не установлено.
	 * 
	 * @param numBars продолжительность удержания заявки в барах
	 * @return this
	 */
	public synchronized LimitOrder notLongerThan(int numBars) {
		limitBars = numBars;
		return this;
	}

	/**
	 * Установить цену покупки.
	 * 
	 * По умолчанию цена не указана. Цена должна быть обязательно указана
	 * для этой задачи, иначе запуск задачи сразу завершится отменой. Значение
	 * цены выравнивается в соответствии с параметрами актива.
	 * 
	 * @param price
	 * @return this
	 */
	public synchronized LimitOrder price(double price) {
		this.price = price;
		return this;
	}

	/**
	 * Установить количество заявки.
	 * 
	 * По умолчанию количество заявки 1 лот.
	 * 
	 * @param quantity
	 * @return this
	 */
	public synchronized LimitOrder qty(int quantity) {
		qty = quantity;
		return this;
	}

	/**
	 * Определить комментарий заявок.
	 * 
	 * По умолчанию комментарий неопределен.
	 * 
	 * @param comment
	 * @return this
	 */
	public synchronized LimitOrder comment(String comment) {
		this.comment = comment;
		return this;
	}
	
	/**
	 * Получить цену заявки.
	 * 
	 * После запуска цена округляется в соответствии с параметрами актива.
	 * Запрос цены после запуска задачи приведет к получению округленного
	 * значения, использующегося в реальной заявке.
	 * 
	 * @return
	 */
	public synchronized double getPrice() {
		return price;
	}

	@Override
	public synchronized void start() {
		if ( ! pending() ) {
			return;
		}
		if ( price == null ) {
			setCancelled();
			return;
		}
		setStarted();
		firstBar = data.getLastBarIndex();
		try {
			price = asset.roundPrice(price);
			order = createOrder();
			data.addObserver(this);
			order.addObserver(this);
		} catch ( PortfolioException e ) {
			debugException("Cannot place order", e);
			cleanup();
			setCancelled();
		} catch ( AssetException e ) {
			debugException("Cannot round price", e);
			cleanup();
			setCancelled();
		} catch ( InterruptedException e ) {
			debugException("Unexpected interruption", e);
			setCancelled();
			throw new RuntimeException("Thread interrupted", e);
		}
	}
	
	@Override
	public synchronized void cancel() {
		if ( started() ) {
			cleanup();
			setCancelled();
		}
	}
	
	private void cleanup() {
		data.deleteObserver(this);
		order.deleteObserver(this);
		if ( order.isActive() ) {
			try {
				portfolio.kill(order);
			} catch ( PortfolioException e ) {
				debugException("Cannot kill order", e);
			} catch ( InterruptedException e ) {
				debugException("Unexpeted interruption", e);
				throw new RuntimeException("Thread interrupted", e);
			}
		}
	}
	
	/**
	 * Выставить лимитную заявку.
	 * 
	 * Метод должен быть переопределен наследниками.
	 * 
	 * @return заявка
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	abstract protected Order createOrder()
		throws PortfolioException, InterruptedException;

	@Override
	public synchronized void update(Observable o, Object arg) {
		if ( ! this.started() ) {
			return;
		}
		if ( o == order ) {
			// Уведомление о смене статуса заявки.
			if ( order.isFilled() ) {
				setCompleted();
			} else {
				setCancelled();
			}
			cleanup();
		} else if ( o == data && limitBars != null ) {
			if ( data.getLastBarIndex() > firstBar + limitBars ) {
				// Превышен лимит разрешенного количества баров
				getClassLogger()
					.debug("Unable to complete task within {} bars", limitBars);
				setCancelled();
				cleanup();
			}
		}
	}

}