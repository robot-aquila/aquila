package ru.prolib.aquila.ChaosTheory;

import java.util.Observable;
import java.util.Observer;

/**
 * Заявка.
 * 
 * Может представлять лимитную, стоп или рыночную заявку на покупку или продажу.
 * Для рыночной заявки не указывается цена. Для лимитной заявки указывается цена
 * заявки, но не указывается стоп-цена. Стоп-заявка подразумевает указание
 * как цены заявки и стоп-цены. Тип заявки определяется на основе комбинации цен
 * при создании экземпляра.  
 */
public class OrderImpl extends Observable implements Order {
	final long id;
	final String comment;
	final int type;
	final int qty;
	final Double price;
	final Double stopPrice;
	
	int status = PENDING;
	OrderImpl related = null;
	
	/**
	 * Конструктор стоп-заявки.
	 * 
	 * @param id
	 * @param type
	 * @param qty
	 * @param stopPrice
	 * @param price
	 */
	public OrderImpl(long id, int type, int qty, Double stopPrice, Double price) {
		this(id, type, qty, stopPrice, price, null);
	}
	
	/**
	 * Конструктор стоп-заявки с комментарием.
	 * 
	 * @param id
	 * @param type
	 * @param qty
	 * @param stopPrice
	 * @param price
	 * @param comment
	 */
	public OrderImpl(long id, int type, int qty, Double stopPrice,
			Double price, String comment)
	{
		super();
		this.id = id;
		this.type = type;
		this.qty = qty;
		this.stopPrice = stopPrice;
		this.price = price;
		this.comment = comment; 
	}
	
	/**
	 * Конструктор рыночной заявки.
	 * 
	 * @param id
	 * @param type
	 * @param qty
	 */
	public OrderImpl(long id, int type, int qty) {
		this(id, type, qty, null);
	}
	
	/**
	 * Конструктор рыночной заявки с комментарием.
	 * 
	 * @param id
	 * @param type
	 * @param qty
	 * @param comment
	 */
	public OrderImpl(long id, int type, int qty, String comment) {
		super();
		this.id = id;
		this.type = type;
		this.qty = qty;
		this.stopPrice = null;
		this.price = null;
		this.comment = comment;
	}
	
	/**
	 * Конструктор лимитной заявки.
	 * 
	 * @param id
	 * @param type
	 * @param qty
	 * @param price
	 */
	public OrderImpl(long id, int type, int qty, double price) {
		this(id, type, qty, price, null);
	}
	
	/**
	 * Конструктор лимитной заявки с комментарием.
	 * 
	 * @param id
	 * @param type
	 * @param qty
	 * @param price
	 * @param comment
	 */
	public OrderImpl(long id, int type, int qty, double price, String comment) {
		super();
		this.id = id;
		this.type = type;
		this.qty = qty;
		this.stopPrice = null;
		this.price = price;
		this.comment = comment;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#getComment()
	 */
	@Override
	public String getComment() {
		return comment;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#getId()
	 */
	@Override
	public long getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#getType()
	 */
	@Override
	public int getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#isBuy()
	 */
	@Override
	public boolean isBuy() {
		return type == BUY;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#isSell()
	 */
	@Override
	public boolean isSell() {
		return ! isBuy();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#isLimitOrder()
	 */
	@Override
	public boolean isLimitOrder() {
		return price != null && stopPrice == null;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#isMarketOrder()
	 */
	@Override
	public boolean isMarketOrder() {
		return price == null && stopPrice == null;
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#isStopOrder()
	 */
	@Override
	public boolean isStopOrder() {
		return stopPrice != null && price != null;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#getQty()
	 */
	@Override
	public int getQty() {
		return qty;
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#getPrice()
	 */
	@Override
	public double getPrice() throws OrderException {
		if ( isMarketOrder() ) {
			throw new OrderException("This order has no price");
		}
		return  price;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#getStopPrice()
	 */
	@Override
	public double getStopPrice() throws OrderException {
		if ( ! isStopOrder() ) {
			throw new OrderException("This order has no stop-price");
		}
		return stopPrice;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#getStatus()
	 */
	@Override
	public synchronized int getStatus() {
		return status;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#activate()
	 */
	@Override
	public synchronized void activate() throws OrderException {
		switch ( status ) {
		case PENDING:
			break;
		case ACTIVE:
			return;
		default:
			throw new OrderException("Unknown status: " + status);
		}
		status = ACTIVE;
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#fill()
	 */
	@Override
	public synchronized void fill() throws OrderException {
		switch ( status ) {
		case KILLED:
		case FILLED:
			return;
		case ACTIVE:
		case PENDING:
			break;
		default:
			throw new OrderException("Unknown status: " + status);
		}
		
		if ( isStopOrder() ) {
			throw new OrderException("Stop-order cannot be filled by this way");
		}
		status = FILLED;
		notifyAndDeleteObservers();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#fill(long)
	 */
	@Override
	public synchronized Order fill(long newOrderId) throws OrderException {
		switch ( status ) {
		case ACTIVE:
		case PENDING:
			break;
		case KILLED:
			throw new OrderException("Order killed");
		default:
			throw new OrderException("Unknown status: " + status);
		}
		
		if ( ! isStopOrder() ) {
			throw new OrderException
				("Limit and market orders cannot be filled by this way");
		}
		status = FILLED;
		related = new OrderImpl(newOrderId, type, qty, price, comment);
		related.activate();
		notifyAndDeleteObservers();
		return related;
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#kill()
	 */
	@Override
	public synchronized void kill() throws OrderException {
		switch ( status ) {
		case KILLED:
		case FILLED:
			return;
		case ACTIVE:
		case PENDING:
			break;
		default:
			throw new OrderException("Unknown status: " + status);
		}
		status = KILLED;
		notifyAndDeleteObservers();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Order#getRelatedOrder()
	 */
	@Override
	public synchronized Order getRelatedOrder() throws OrderException {
		if ( ! this.isStopOrder() ) {
			throw new OrderException("This works only for stop-orders");
		}
		return related;
	}
	
	@Override
	public synchronized String toString() {
		String s = "PENDING";
		switch ( status ) {
		case KILLED: s = "KILLED"; break;
		case FILLED: s = "FILLED"; break;
		case ACTIVE: s = "ACTIVE"; break;
		}
		
		return "Order#" + id + " " + (type == BUY ? "BUY " : "SELL")
			+ " qty=" + qty
			+ (stopPrice == null ? "" : " stop-price=" + stopPrice)
			+ (price == null ? "" : " price=" + price )
			+ " status=" + s
			+ (comment == null ? "" : " [" + comment  + "]");
	}
	
	@Override
	public synchronized boolean equals(Object obj) {
		if ( obj == null || !(obj instanceof OrderImpl) ) return false;
		OrderImpl order = (OrderImpl)obj;
		
		if ( id != order.id ) return false;
		if ( qty != order.qty ) return false;
		if ( status != order.status ) return false;
		if ( type != order.type ) return false;

		if ( price == null ) {
			if ( order.price != null) return false;
		} else {
			if ( ! price.equals(order.price) ) return false;
		}
		
		if ( stopPrice == null ){
			if ( order.stopPrice != null) return false;
		} else {
			if ( ! stopPrice.equals(order.stopPrice) ) return false;
		}
		
		if ( related == null ) {
			if ( order.related != null ) return false;
		} else {
			if ( ! related.equals(order.related) ) return false;
		}
		
		if ( comment == null ) {
			if ( order.comment != null ) return false;
		} else {
			if ( ! comment.equals(order.comment) ) return false;
		}
		return true;
	}
	
	private synchronized void notifyAndDeleteObservers() {
		notifyAll();
		setChanged();
		notifyObservers();
		deleteObservers();
	}
	
	/**
	 * Переопределенный обработчик проверяет ситуацию, когда обозреватель
	 * добавляется к исполненному или отмененному ордеру. В этом случае,
	 * уведомление отправляется немедленно, после чего список обозревателей
	 * очищается. Такой подход позволяет избежать многочисленных проверок 
	 * в пользовательском коде.
	 */
	@Override
	public synchronized void addObserver(Observer o) {
		super.addObserver(o);
		if ( status == Order.KILLED || status == Order.FILLED ) {
			notifyAndDeleteObservers();
		}
	}

	@Override
	public boolean isKilled() {
		return getStatus() == Order.KILLED;
	}

	@Override
	public boolean isFilled() {
		return getStatus() == Order.FILLED;
	}

	@Override
	public boolean isActive() {
		return ! isKilled() && ! isFilled();
	}
	
}