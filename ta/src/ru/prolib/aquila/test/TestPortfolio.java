package ru.prolib.aquila.test;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderException;
import ru.prolib.aquila.ChaosTheory.OrderImpl;
import ru.prolib.aquila.ChaosTheory.Portfolio;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioOrders;
import ru.prolib.aquila.ChaosTheory.PortfolioState;
import ru.prolib.aquila.ChaosTheory.PortfolioTimeoutException;
import ru.prolib.aquila.util.Sequence;

/**
 * Портфель для тестирования.
 */
public class TestPortfolio implements Portfolio {
	private static final Logger logger = LoggerFactory.getLogger(TestPortfolio.class);
	private final Asset asset;
	private final Sequence<Long> id;
	private final PortfolioOrders orders;
	private final PortfolioState state;
	
	public TestPortfolio(Asset asset, Sequence<Long> id,
						 PortfolioOrders orders, PortfolioState state)
	{
		super();
		this.asset = asset;
		this.id = id;
		this.orders = orders;
		this.state = state;
	}
	
	@Override
	public Asset getAsset() {
		return asset;
	}
	
	public Sequence<Long> getIdSeq() {
		return id;
	}
	
	public PortfolioOrders getPortfolioOrders() {
		return orders;
	}
	
	public PortfolioState getPortfolioState() {
		return state;
	}
	
	@Override	
	public double getMoney() throws PortfolioException {
		return state.getMoney();
	}

	@Override
	public int getPosition() throws PortfolioException {
		return state.getPosition();
	}
	
	@Override
	public void kill(Order order) throws PortfolioException {
		try {
			logger.debug("Kill order: {}", order);
			order.kill();
		} catch ( OrderException e ) {
			throw new PortfolioException(e);
		}
	}
	
	@Override
	public Order stopBuy(int qty, double stopPrice, double price)
		throws PortfolioException
	{
		return stopBuy(qty, stopPrice, price, null);
	}
	
	@Override
	public Order stopBuy(int qty, double stopPrice, double price, String msg)
		throws PortfolioException
	{
		Order order = new OrderImpl(id.next(), Order.BUY, qty,
				stopPrice, price, msg);
		orders.startWatch(order);
		logger.debug("New order: {}", order);
		return order;
	}

	@Override
	public Order stopSell(int qty, double stopPrice, double price)
		throws PortfolioException
	{
		return stopSell(qty, stopPrice, price, null);
	}
	
	@Override
	public Order stopSell(int qty, double stopPrice, double price, String msg)
		throws PortfolioException
	{
		Order order = new OrderImpl(id.next(), Order.SELL, qty,
				stopPrice, price, msg);
		orders.startWatch(order);
		logger.debug("New order: {}", order);
		return order;
	}
	
	@Override
	public void killAll() throws PortfolioException {
		Iterator<Order> i = orders.getActiveOrders().iterator();
		Order order = null;
		while ( i.hasNext() ) {
			order = i.next();
			try {
				logger.debug("Kill order: {}", order);
				order.kill();
			} catch ( OrderException e ) {
				throw new PortfolioException(e);
			}
		}
	}
	
	@Override
	public void killAll(int type)
		throws PortfolioException, InterruptedException
	{
		Iterator<Order> i = orders.getActiveOrders().iterator();
		Order order = null;
		while ( i.hasNext() ) {
			order = i.next();
			if ( order.getType() == type ) {
				try {
					if ( order.getStatus() == Order.PENDING ) {
						order.activate();
					}
					logger.debug("Kill order: {}", order);
					order.kill();
				} catch ( OrderException e ) {
					throw new PortfolioException(e);
				}
			}
		}
	}
	
	@Override
	public Order limitBuy(int qty, double price) throws PortfolioException {
		return limitBuy(qty, price, null);
	}

	@Override
	public Order limitBuy(int qty, double price, String comment)
		throws PortfolioException
	{
		Order order = new OrderImpl(id.next(), Order.BUY, qty, price, comment);
		orders.startWatch(order);
		logger.debug("New order: {}", order);
		return order;
	}

	@Override
	public Order limitSell(int qty, double price) throws PortfolioException {
		return limitSell(qty, price, null);
	}
	
	@Override
	public Order limitSell(int qty, double price, String comment)
		throws PortfolioException
	{
		Order order = new OrderImpl(id.next(), Order.SELL, qty, price, comment);
		orders.startWatch(order);
		logger.debug("New order: {}", order);
		return order;
	}

	@Override
	public void waitForComplete(Order order, long timeout)
		throws PortfolioException,InterruptedException
	{
		orders.waitForComplete(order, timeout);
	}

	@Override
	public void waitForNeutralPosition(long timeout)
			throws PortfolioTimeoutException, PortfolioException,
			InterruptedException
	{
		state.waitForNeutralPosition(timeout);
	}

}