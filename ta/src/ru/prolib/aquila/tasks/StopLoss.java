package ru.prolib.aquila.tasks;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetException;
import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderException;
import ru.prolib.aquila.ChaosTheory.Portfolio;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.ta.ds.MarketData;

/**
 * Простой неподвижный стоп-лосс.
 * 
 * Выставляет стоп-заявку и ждет ее исполнения или отмены извне. При активации
 * стоп-заявки, сохраняет номер бара, на котором лимитная заявка была
 * выставлена. После чего подписывается на источник данных. Если в течение
 * двух баров лимитная заявка не была исполнена, запускает процедуру экстренного
 * закрытия позиции. Результат работы этой процедуры становится результатом
 * работы данной задачи.
 * 
 * 2012-02-14
 * $Id: StopLoss.java 201 2012-04-03 14:45:43Z whirlwind $
 */
public abstract class StopLoss extends TaskUsesLocator implements Observer {
	protected Portfolio portfolio;
	protected MarketData data;
	protected Asset asset;
	protected Order stopOrder;
	protected Order limitOrder;
	protected Double price;
	protected String comment;
	protected int qty = 1;
	protected int slippage = 2;
	protected int activatedAt = -1;
	
	public StopLoss(ServiceLocator locator) {
		super(locator);
		try {
			portfolio = locator.getPortfolio();
			asset = portfolio.getAsset();
			data = locator.getMarketData();
		} catch ( ServiceLocatorException e ) {
			debugException("Initialize failed", e);
			throw new RuntimeException("Initialize failed", e);
		}
	}
	
	public StopLoss price(double price) {
		this.price = price;
		return this;
	}
	
	public StopLoss qty(int quantity) {
		qty = quantity;
		return this;
	}
	
	public StopLoss comment(String comment) {
		this.comment = comment;
		return this;
	}
	
	public StopLoss slippage(int value) {
		this.slippage = value;
		return this;
	}
	
	@Override
	public void start() {
		if ( ! pending() ) {
			return;
		}
		if ( price == null ) {
			setCancelled();
			return;
		}
		setStarted();
		try {
			price = asset.roundPrice(price);
			stopOrder = createStopOrder();
			stopOrder.addObserver(this);
		} catch ( AssetException e ) {
			debugException("Cannot round price", e);
			cleanup();
			setCancelled();
		} catch ( PortfolioException e ) {
			debugException("Cannot place stop-order", e);
			cleanup();
			setCancelled();
		} catch ( InterruptedException e ) {
			debugException("Unexpected interruption", e);
			setCancelled();
			throw new RuntimeException("Thread interrupted", e);
		}
	}
	
	@Override
	public void cancel() {
		if ( started() ) {
			cleanup();
			setCancelled();
		}
	}
	
	private void cleanup() {
		data.deleteObserver(this);
		if ( stopOrder != null ) {
			stopOrder.deleteObserver(this);
			if ( stopOrder.isActive() ) {
				killOrder(stopOrder);
			}
		}
		if ( limitOrder != null ) {
			limitOrder.deleteObserver(this);
			if ( limitOrder.isActive() ) {
				killOrder(limitOrder);
			}
		}
	}

	private void killOrder(Order order) {
		try {
			portfolio.kill(order);
		} catch ( PortfolioException e ) {
			debugException("Cannot kill order", e);
		} catch ( InterruptedException e ) {
			debugException("Unexpeted interruption", e);
			throw new RuntimeException("Thread interrupted", e);
		}
	}
	
	abstract protected Order createStopOrder()
		throws PortfolioException, InterruptedException;
	
	@Override
	public synchronized void update(Observable o, Object arg) {
		if ( ! started() ) {
			return;
		}
		if ( o == stopOrder ) {
			if ( stopOrder.isFilled() ) {
				try {
					limitOrder = stopOrder.getRelatedOrder();
					limitOrder.addObserver(this);
					activatedAt = data.getLastBarIndex();
					data.addObserver(this);
				} catch ( OrderException e ) {
					debugException("Cannot obtain related order", e);
					setCancelled();
					cleanup();
				}
			} else {
				// стоп-заявка была снята
				setCancelled();
				cleanup();
			}
		} else if ( o == limitOrder ) {
			if ( limitOrder.isFilled() ) {
				setCompleted();
			} else {
				setCancelled();
			}
			cleanup();
		} else if ( o == data ) {
			if ( data.getLastBarIndex() - activatedAt >= 2 ) {
				// Слишком долго исполняется лимитная заявка.
				// Снимаем лимитную заявку и закрываем экстренно.
				cleanup();
				Task close = new CloseImmediately(locator);
				close.start();
				if ( close.completed() ) {
					setCompleted();
				} else {
					setCancelled();
				}
			}
		}
	}

}
