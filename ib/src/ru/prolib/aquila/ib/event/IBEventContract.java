package ru.prolib.aquila.ib.event;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.ib.client.ContractDetails;
import ru.prolib.aquila.core.EventType;

/**
 * Событие: получены детали контракта.
 * <p>
 * 2012-11-18<br>
 * $Id: IBEventContract.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventContract extends IBEventRequest {
	public static final int SUBTYPE_NORM = 0x01;
	public static final int SUBTYPE_BOND = 0x02;
	public static final int SUBTYPE_END	 = 0x04;
	
	private final int subType;
	private final ContractDetails details;

	/**
	 * Создать событие.
	 * <p>
	 * @param type тип события
	 * @param reqId номер запроса
	 * @param subType подтип
	 * @param details детали контракта
	 */
	public IBEventContract(EventType type, int reqId, int subType,
			ContractDetails details)
	{
		super(type, reqId);
		this.subType = subType;
		this.details = details;
	}
	
	/**
	 * Создать событие на основании другого экземпляра события.
	 * <p>
	 * @param type тип нового события
	 * @param event событие-основание
	 */
	public IBEventContract(EventType type, IBEventContract event) {
		this(type, event.getReqId(), event.getSubType(),
				event.getContractDetails());
	}
	
	/**
	 * Получить подтип события.
	 * <p>
	 * @return идентификатор подтипа
	 */
	public int getSubType() {
		return subType;
	}
	
	/**
	 * Получить детали контракта.
	 * <p>
	 * @return детали контракта или null для {@link #SUBTYPE_END}.
	 */
	public ContractDetails getContractDetails() {
		return details;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof IBEventContract ) {
			IBEventContract o = (IBEventContract) other;
			return new EqualsBuilder()
				.appendSuper(super.equals(other))
				.append(subType, o.subType)
				.append(details, o.details)
				.isEquals();
		} else {
			return false;
		}
	}

}
