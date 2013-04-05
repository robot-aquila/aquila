package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderResolver;

/**
 * Геттер заявки на основе определителя заявки.
 * <p>
 * 2012-10-17<br>
 * $Id: GOrder.java 542 2013-02-23 04:15:34Z whirlwind $
 */
@Deprecated
public class GOrder implements G<EditableOrder> {
	private final OrderResolver resolver;
	private final G<Long> gTransId;
	private final G<Long> gId;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param resolver определитель заявок
	 * @param gTransId
	 * @param gId
	 */
	public GOrder(OrderResolver resolver, G<Long> gTransId, G<Long> gId) {
		super();
		this.resolver = resolver;
		this.gTransId = gTransId;
		this.gId = gId;
	}
	
	/**
	 * Получить определитель заявок.
	 * <p>
	 * @return определитель заявок
	 */
	public OrderResolver getOrderResolver() {
		return resolver;
	}
	
	/**
	 * Получить геттер идентификатора транзакции.
	 * <p>
	 * @return геттер идентификатора транзакции
	 */
	public G<Long> getTransIdGetter() {
		return gTransId;
	}
	
	/**
	 * Получить геттер идентификатора заявки.
	 * <p>
	 * @return геттер
	 */
	public G<Long> getIdGetter() {
		return gId;
	}
	
	@Override
	public EditableOrder get(Object row) {
		Long id = (Long) gId.get(row);
		Long transId = (Long) gTransId.get(row);
		if ( id == null ) {
			// При отсутствии идентификатора заявки в режиме приема данных
			// дальнейшая работа не имеет смысла, так как такую заявку
			// нельзя поместить в хранилище.
			return null;
		}
		return resolver.resolveOrder(id, transId);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == GOrder.class ) {
			GOrder o = (GOrder) other;
			return new EqualsBuilder()
				.append(resolver, o.resolver)
				.append(gTransId, o.gTransId)
				.append(gId, o.gId)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121103, /*0*/64401)
			.append(resolver)
			.append(gTransId)
			.append(gId)
			.toHashCode();
	}

}
