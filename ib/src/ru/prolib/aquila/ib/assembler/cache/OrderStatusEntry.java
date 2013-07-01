package ru.prolib.aquila.ib.assembler.cache;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.OrderStatus;

/**
 * Кэш-запись статуса заявки.
 * <p>
 * Инкапсулирует данные, полученные через метод orderStatus.
 */
public class OrderStatusEntry extends CacheEntry {
	private final Long id;
	private final String nativeStatus;
	private final Long rest;
	private final double avgFillPrice;
	private final OrderStatus status;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param id номер заявки
	 * @param status статус
	 * @param remaining неисполненный остаток
	 * @param avgFillPrice средняя цена исполненной части
	 */
	public OrderStatusEntry(int id, String status, int remaining,
			double avgFillPrice)
	{
		super();
		this.id = new Long(id);
		this.nativeStatus = status;
		this.rest = new Long(remaining);
		this.avgFillPrice = avgFillPrice;
		this.status = OrderEntry.convertStatus(status);
	}
	
	/**
	 * Получить номер заявки.
	 * <p>
	 * @return номер заявки
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * Получить статус заявки.
	 * <p>
	 * @return статус заявки или null, если нет соответствующего статуса
	 */
	public OrderStatus getStatus() {
		return status;
	}
	
	/**
	 * Получить неисполненный остаток заявки.
	 * <p>
	 * @return неисполненный остаток
	 */
	public Long getQtyRest() {
		return rest;
	}
	
	/**
	 * Получить среднюю цену исполненной части заявки.
	 * <p>
	 * @return средняя цена исполненной части
	 */
	public Double getAvgExecutedPrice() {
		return avgFillPrice;
	}
	
	/**
	 * Получить оригинальную строку статуса.
	 * <p>
	 * @return строка статуса, полученная через IB API
	 */
	public String getNativeStatus() {
		return nativeStatus;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderStatusEntry.class ) {
			return false;
		}
		OrderStatusEntry o = (OrderStatusEntry) other;
		return new EqualsBuilder()
			.append(o.avgFillPrice, avgFillPrice)
			.append(o.id, id)
			.append(o.nativeStatus, nativeStatus)
			.append(o.rest, rest)
			.isEquals();
	}

}
