package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * Спецификация позиции.
 * <p>
 * 2012-12-26<br>
 * $Id: SetupPositionImpl.java 406 2013-01-11 10:08:56Z whirlwind $
 */
public class SetupPositionImpl implements SetupPosition {
	private final SecurityDescriptor descr;
	private Price quota; 
	private PositionType type;
	private PositionType allowed = PositionType.BOTH;
	
	public SetupPositionImpl(SecurityDescriptor descr,
			Price quota, PositionType type)
	{
		super();
		this.descr = descr;
		this.quota = quota;
		this.type = type;
	}
	
	public SetupPositionImpl(SecurityDescriptor descr, Price quota) {
		this(descr, quota, PositionType.CLOSE);
	}
	
	public SetupPositionImpl(SecurityDescriptor descr) {
		this(descr, new Price(PriceUnit.PERCENT, 0.0d));
	}
	
	public SetupPositionImpl(SetupPosition setup) {
		super();
		synchronized ( setup ) {
			descr = setup.getSecurityDescriptor();
			quota = setup.getQuota();
			type = setup.getType();
			allowed = setup.getAllowedType();
		}
	}
	
	@Override
	public SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}

	@Override
	public synchronized Price getQuota() {
		return quota;
	}
	
	@Override
	public synchronized void setQuota(Price value) {
		quota = value;
	}

	@Override
	public synchronized PositionType getType() {
		return type;
	}

	@Override
	public synchronized void setType(PositionType value) {
		if ( value == PositionType.BOTH ) {
			throw new IllegalArgumentException(value.toString());
		}
		type = value;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		return other != null && other.getClass() == SetupPositionImpl.class ?
			fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		SetupPositionImpl o = (SetupPositionImpl) other;
		return new EqualsBuilder()
			.append(descr, o.descr)
			.append(quota, o.quota)
			.append(type, o.type)
			.append(allowed, o.allowed)
			.isEquals();
	}
	
	@Override
	public synchronized int hashCode() {
		return new HashCodeBuilder(20121231, 165329)
			.append(descr)
			.append(quota)
			.append(type)
			.append(allowed)
			.toHashCode();
	}
	
	@Override
	public synchronized SetupPositionImpl clone() {
		return new SetupPositionImpl(this);
	}

	@Override
	public synchronized void setAllowedType(PositionType value) {
		allowed = value;
	}

	@Override
	public synchronized PositionType getAllowedType() {
		return allowed;
	}

	@Override
	public synchronized boolean isOpenAllowed(PositionType type) {
		if ( type == PositionType.LONG ) {
			return allowed == PositionType.LONG || allowed == PositionType.BOTH;
		} else if ( type == PositionType.SHORT ) {
			return allowed == PositionType.SHORT || allowed== PositionType.BOTH;
		}
		return false;
	}

	@Override
	public synchronized boolean isShouldClose(PositionType current) {
		if ( current == PositionType.SHORT ) {
			if ( type == PositionType.CLOSE ) {
				return true;
			}
			if ( type == PositionType.LONG && ( allowed == PositionType.SHORT
					|| allowed == PositionType.CLOSE ) )
			{
				return true;
			}
		} else if ( current == PositionType.LONG ) {
			if ( type == PositionType.CLOSE ) {
				return true;
			}
			if ( type == PositionType.SHORT && ( allowed == PositionType.LONG
					|| allowed == PositionType.CLOSE ) )
			{
				return true;
			}
		}
		return false;
	}

}
