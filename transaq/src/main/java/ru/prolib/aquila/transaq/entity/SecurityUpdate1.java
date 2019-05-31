package ru.prolib.aquila.transaq.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SecurityUpdate1 {
	public static final int OPMASK_USECREDIT = 0x01;
	public static final int OPMASK_BYMARKET = 0x02;
	public static final int OPMASK_NOSPLIT = 0x04;
	public static final int OPMASK_FOK = 0x08;
	public static final int OPMASK_IOC = 0x10;
	
	private final int secID;
	private final boolean active;
	private final String secCode, secClass;
	private final String defaultBoardCode;
	private final int marketID;
	private final String shortName;
	private final SecurityBoardParams boardParams;
	private final int opmask;
	private final SecType secType;
	private final String secTZ;
	private final int quotesType;
	
	public SecurityUpdate1(int secID,
			boolean active,
			String secCode,
			String secClass,
			String defaultBoardCode,
			int marketID,
			String shortName,
			SecurityBoardParams boardParams,
			int opmask,
			SecType secType,
			String secTZ,
			int quotesType)
	{
		this.secID = secID;
		this.active = active;
		this.secCode = secCode;
		this.secClass = secClass;
		this.defaultBoardCode = defaultBoardCode;
		this.marketID = marketID;
		this.shortName = shortName;
		this.boardParams = boardParams;
		this.opmask = opmask;
		this.secType = secType;
		this.secTZ = secTZ;
		this.quotesType = quotesType;
	}
	
	public int getSecID() {
		return secID;
	}
	
	public boolean getActive() {
		return active;
	}
	
	public String getSecCode() {
		return secCode;
	}
	
	public String getSecClass() {
		return secClass;
	}
	
	public String getDefaultBoardCode() {
		return defaultBoardCode;
	}
	
	public int getMarketID() {
		return marketID;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public SecurityBoardParams getDefaultBoardParams() {
		return boardParams;
	}
	
	public int getOpMask() {
		return opmask;
	}
	
	public SecType getSecType() {
		return secType;
	}
	
	public String getSecTZ() {
		return secTZ;
	}
	
	public int getQuotesType() {
		return quotesType;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SecurityUpdate1.class ) {
			return false;
		}
		SecurityUpdate1 o = (SecurityUpdate1) other;
		return new EqualsBuilder()
				.append(o.secID, secID)
				.append(o.active, active)
				.append(o.secCode, secCode)
				.append(o.secClass, secClass)
				.append(o.defaultBoardCode, defaultBoardCode)
				.append(o.marketID, marketID)
				.append(o.shortName, shortName)
				.append(o.boardParams, boardParams)
				.append(o.opmask, opmask)
				.append(o.secType, secType)
				.append(o.secTZ, secTZ)
				.append(o.quotesType, quotesType)
				.build();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(10087251, 7129)
				.append(secID)
				.append(active)
				.append(secCode)
				.append(secClass)
				.append(defaultBoardCode)
				.append(marketID)
				.append(shortName)
				.append(boardParams)
				.append(opmask)
				.append(secType)
				.append(secTZ)
				.append(quotesType)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
