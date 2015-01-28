package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderActivatorLink;

/**
 * Активатор заявки типа: стоп-заявка.
 * <p>
 * После старта начинает отслеживать изменение цены инструмента. Отслеживание
 * выполняется либо по сделкам инструмента, либо по атрибуту последней цены
 * инструмента. Активация выполняется в момент, когда: для заявки на продажу
 * цена будет меньше или равна стоп-цене, для заявки на покупку цена будет
 * больше или равна стоп-цене.
 */
public class StopOrderActivator extends OrderActivator
	implements EventListener
{
	private final Double stopPrice;
	private Security security;

	/**
	 * Служебный конструктор.
	 * <p>
	 * @param link контейнер
	 * @param stopPrice стоп-цена
	 */
	StopOrderActivator(OrderActivatorLink link, Double stopPrice) {
		super(link);
		this.stopPrice = stopPrice;
	}
	
	/**
	 * Получить стоп-цену.
	 * <p>
	 * @return стоп-цена
	 */
	public Double getStopPrice() {
		return stopPrice;
	}
	
	/**
	 * Получить контейнер активатора.
	 * <p>
	 * Служебный, только для тестов.
	 * <p>
	 * @return контейнер
	 */
	OrderActivatorLink getLink() {
		return link;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param stopPrice стоп-цена
	 */
	public StopOrderActivator(Double stopPrice) {
		this(new OrderActivatorLink(), stopPrice);
	}

	@Override
	protected synchronized void begin() throws OrderException {
		try {
			security = getOrder().getSecurity();
		} catch ( SecurityException e ) {
			throw new OrderException(e);
		}
		security.OnChanged().addListener(this);
		security.OnTrade().addListener(this);
	}

	@Override
	protected synchronized void finish() {
		security.OnTrade().removeListener(this);
		security.OnChanged().removeListener(this);
		security = null;
	}

	@Override
	public synchronized void onEvent(Event event) {
		if ( security == null ) {
			return;
		}
		
		if ( event.isType(security.OnChanged()) ) {
			control(security.getLastPrice());
		} else if ( event.isType(security.OnTrade()) ) {
			control(((SecurityTradeEvent) event).getTrade().getPrice());
		}
	}
	
	/**
	 * Обработка изменения цены.
	 * <p>
	 * @param price последняя цена инструмента
	 */
	private void control(Double price) {
		if ( price == null ) {
			return;
		}
		if ( getOrder().getDirection() == Direction.BUY ) {
			if ( price >= stopPrice ) {
				activate();
			}
		} else {
			if ( price <= stopPrice ) {
				activate();
			}
		}
	}
	
	/**
	 * Установить инструмент.
	 * <p>
	 * Служебный, только для тестов.
	 * <p>
	 * @param security инструмент
	 */
	void setSecurity(Security security) {
		this.security = security;
	}
	
	/**
	 * Получить инструмент.
	 * <p>
	 * Служебный, только для тестов.
	 * <p>
	 * @return инструмент
	 */
	synchronized Security getSecurity() {
		return security;
	}
	
	@Override
	public String toString() {
		return "StopPrice=" + stopPrice;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != StopOrderActivator.class ) {
			return false;
		}
		StopOrderActivator o = (StopOrderActivator) other;
		return new EqualsBuilder()
			.append(o.security, security)
			.append(o.link, link)
			.append(o.stopPrice, stopPrice)
			.isEquals();
	}

}
