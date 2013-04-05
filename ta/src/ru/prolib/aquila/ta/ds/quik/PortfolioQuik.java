package ru.prolib.aquila.ta.ds.quik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetException;
import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderImpl;
import ru.prolib.aquila.ChaosTheory.Portfolio;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioOrders;
import ru.prolib.aquila.ChaosTheory.PortfolioState;
import ru.prolib.aquila.ChaosTheory.PortfolioTimeoutException;

/**
 * Управление портфелем состоящего из одного актива.
 * Для подачи поручений используются файлы импорта транзакций QUIK.
 */
public class PortfolioQuik implements Portfolio {
	private static final Logger logger = LoggerFactory.getLogger(PortfolioQuik.class);
	
	private static final String ACCOUNT = "ACCOUNT=";
	private static final String CLASSCODE = "CLASSCODE=";
	private static final String SECCODE = "SECCODE=";
	private static final String ACTION = "ACTION=";
	private static final String OPERATION = "OPERATION=";
	private static final String PRICE = "PRICE=";
	private static final String QUANTITY = "QUANTITY=";
	private static final String STOPPRICE = "STOPPRICE=";
	private static final String ORDER_KEY = "ORDER_KEY=";
	private static final String KILL_STOP_ORDER = "KILL_STOP_ORDER";
	private static final String KILL_ORDER = "KILL_ORDER";
	private static final String KILL_ALL_STOP_ORDERS = "KILL_ALL_STOP_ORDERS";
	private static final String KILL_ALL_ORDERS = "KILL_ALL_ORDERS";
	private static final String NEW_ORDER = "NEW_ORDER";
	private static final String NEW_STOP_ORDER = "NEW_STOP_ORDER";
	private static final String B = "B";
	private static final String S = "S";
	
	private final Tr2Quik tr2quik;
	private final PortfolioState state;
	private final PortfolioOrders orders;
	private final Asset asset;
	private final String account;
	
	public PortfolioQuik(String account, Asset asset,
							  Tr2Quik tr2quik, PortfolioState state,
							  PortfolioOrders orders)
	{
		super();
		this.tr2quik = tr2quik;
		this.state = state;
		this.orders = orders;
		this.asset = asset;
		this.account = account;
		logger.info("Configured for {} account and {} asset",
				account, asset.getAssetCode());
	}
	
	@Override
	public Asset getAsset() {
		return asset;
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
	public void kill(Order order)
		throws PortfolioException,InterruptedException
	{
		if ( order.getStatus() == Order.KILLED
		  || order.getStatus() == Order.FILLED )
		{
			return;
		}
		send(ACTION + (order.isStopOrder() ? KILL_STOP_ORDER : KILL_ORDER) + ";"
			+ ORDER_KEY + order.getId(), 0);
	}
	
	@Override
	public void killAll()
		throws PortfolioException, InterruptedException
	{
		send(ACTION + KILL_ALL_STOP_ORDERS, 0);
		send(ACTION + KILL_ALL_ORDERS, 0);
	}
	
	@Override
	public void killAll(int type)
		throws PortfolioException, InterruptedException 
	{
		String op = type == Order.BUY ? B : S;
		send(ACTION + KILL_ALL_STOP_ORDERS + ";" + OPERATION + op, 0);
		send(ACTION + KILL_ALL_ORDERS + ";" + OPERATION + op, 0);
	}

	@Override
	public Order limitBuy(int qty, double price)
		throws PortfolioException,InterruptedException
	{
		return limitBuy(qty, price, null);
	}
	
	@Override
	public Order limitBuy(int qty, double price, String comment)
			throws PortfolioException, InterruptedException
	{
		long id = sendOrder(ACTION + NEW_ORDER + ";"
				  + OPERATION + B + ";"
				  + PRICE + formatPrice(price) + ";"
				  + QUANTITY + qty);
		OrderImpl order = new OrderImpl(id, Order.BUY, qty, price, comment);
		orders.startWatch(order);
		return order;
	}

	@Override
	public Order limitSell(int qty, double price)
		throws PortfolioException,InterruptedException
	{
		return limitSell(qty, price, null);
	}
	
	@Override
	public Order limitSell(int qty, double price, String comment)
			throws PortfolioException, InterruptedException
	{
		long id = sendOrder(ACTION + NEW_ORDER + ";"
				  + OPERATION + S + ";"
				  + PRICE + formatPrice(price) + ";"
				  + QUANTITY + qty);
		OrderImpl order = new OrderImpl(id, Order.SELL, qty, price, comment);
		orders.startWatch(order);
		return order;
	}

	@Override
	public Order stopBuy(int qty, double stopPrice, double price)
		throws PortfolioException,InterruptedException
	{
		return stopBuy(qty, stopPrice, price, null);
	}

	@Override
	public Order stopBuy(int qty, double stopPrice, double price, String msg)
		throws PortfolioException,InterruptedException
	{
		long id = sendOrder(ACTION + NEW_STOP_ORDER + ";"
						  + OPERATION + B + ";"
						  + STOPPRICE + formatPrice(stopPrice) + ";"
						  + PRICE + formatPrice(price) + ";"
						  + QUANTITY + qty);
		OrderImpl order = new OrderImpl(id, Order.BUY, qty, stopPrice, price, msg);
		orders.startWatch(order);
		return order;
	}

	@Override
	public Order stopSell(int qty, double stopPrice, double price)
		throws PortfolioException,InterruptedException
	{
		return stopSell(qty, stopPrice, price, null);
	}

	@Override
	public Order stopSell(int qty, double stopPrice, double price, String msg)
		throws PortfolioException,InterruptedException
	{
		long id = sendOrder(ACTION + NEW_STOP_ORDER + ";"
						  + OPERATION + S + ";"
						  + STOPPRICE + formatPrice(stopPrice) + ";"
						  + PRICE + formatPrice(price) + ";"
						  + QUANTITY + qty);
		OrderImpl order = new OrderImpl(id, Order.SELL, qty, stopPrice, price, msg);
		orders.startWatch(order);
		return order;
	}

	@Override
	public void waitForComplete(Order order, long timeout)
		throws InterruptedException,
			   PortfolioTimeoutException,
			   PortfolioException
	{
		orders.waitForComplete(order, timeout);
	}
	
	@Override
	public void waitForNeutralPosition(long timeout)
		throws PortfolioTimeoutException,
		       PortfolioException,
			   InterruptedException
	{
		state.waitForNeutralPosition(timeout);
	}
	
	/**
	 * Отправить транзакцию на выполнение.
	 * @param cmd специфическая часть транзакции
	 * @return
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	private final Tr2QuikResult send(String cmd, int expectedStatus)
		throws PortfolioException,InterruptedException
	{
		try {
			return tr2quik.transaction(cmd + ";" +
					ACCOUNT + account + ";" +
					CLASSCODE + asset.getClassCode() + ";" +
					SECCODE + asset.getAssetCode()
					, expectedStatus);
		} catch ( Tr2QuikTimeoutException e ) {
			throw new PortfolioTimeoutException(e);
		} catch ( Exception e ) {
			throw new PortfolioException(e.getMessage(), e); 
		}		
	}
	
	/**
	 * Отправить транзакцию по заявке на выполнение.
	 * @param cmd специфическая часть транзакции
	 * @return номер заявки
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	private final long sendOrder(String cmd)
		throws PortfolioException,InterruptedException
	{
		Tr2QuikResult result = send(cmd, 3);
		if ( result.orderNumber == 0 ) {
			throw new PortfolioException("Order number expected for trans: "
					+ result.transId);
		}
		return result.orderNumber;
	}
	
	private String formatPrice(double price) throws PortfolioException {
		try {
			return asset.formatPrice(price);
		} catch ( AssetException e ) {
			throw new PortfolioException(e.getMessage(), e);
		}
	}

}
