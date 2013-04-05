package ru.prolib.aquila.t2q;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Детали сделки.
 * <p>
 * 2013-03-13<br>
 * $Id: T2QTrade.java 576 2013-03-14 12:07:25Z whirlwind $
 */
public class T2QTrade {
	private final int mode;
	private final long id;
	private final long orderId;
	private final String classCode;
	private final String secCode;
	private final double price;
	private final long qty;
	private final double value;
	private final boolean isSell;
	private final long date;
	private final long settleDate;
	private final long time;
	private final boolean isMarginal;
	private final double accruedInt;
	private final double yield;
	private final double tsCommission;
	private final double clearingCenterCommission;
	private final double exchangeCommission;
	private final double tradingSystemCommission;
	private final double price2;
	private final double repoRate;
	private final double repoValue;
	private final double repo2Value;
	private final double accruedInt2;
	private final long repoTerm;
	private final double startDiscount;
	private final double lowerDiscount;
	private final double upperDiscount;
	private final boolean blockSecurities;
	private final String currency;
	private final String settleCurrency;
	private final String settleCode;
	private final String account;
	private final String brokerRef;
	private final String clientCode;
	private final String userId;
	private final String firmId;
	private final String partnerFirmId;
	private final String exchangeCode;
	private final String stationId;
	
	public T2QTrade(int mode, long id, long orderId, String classCode,
			String secCode, double price, long qty, double value,
			boolean isSell, long date, long settleDate, long time,
			boolean isMarginal, double accruedInt, double yield,
			double tsCommission, double clearingCenterCommission,
			double exchangeCommission, double tradingSystemCommission,
			double price2, double repoRate, double repoValue, double repo2Value,
			double accruedInt2, long repoTerm, double startDiscount,
			double lowerDiscount, double upperDiscount, boolean blockSecurities,
			String currency, String settleCurrency, String settleCode,
			String account, String brokerRef, String clientCode, String userId,
			String firmId, String partnerFirmId, String exchangeCode,
			String stationId)
	{
		super();
		this.mode = mode;
		this.id = id;
		this.orderId = orderId;
		this.classCode = classCode;
		this.secCode = secCode;
		this.price = price;
		this.qty = qty;
		this.value = value;
		this.isSell = isSell;
		this.date = date;
		this.settleDate = settleDate;
		this.time = time;
		this.isMarginal = isMarginal;
		this.accruedInt = accruedInt;
		this.yield= yield;
		this.tsCommission = tsCommission;
		this.clearingCenterCommission = clearingCenterCommission;
		this.exchangeCommission = exchangeCommission;
		this.tradingSystemCommission = tradingSystemCommission;
		this.price2 = price2;
		this.repoRate = repoRate;
		this.repoValue = repoValue;
		this.repo2Value = repo2Value;
		this.accruedInt2 = accruedInt2;
		this.repoTerm = repoTerm;
		this.startDiscount = startDiscount;
		this.lowerDiscount = lowerDiscount;
		this.upperDiscount = upperDiscount;
		this.blockSecurities = blockSecurities;
		this.currency = currency;
		this.settleCurrency = settleCurrency;
		this.settleCode = settleCode;
		this.account = account;
		this.brokerRef = brokerRef;
		this.clientCode = clientCode;
		this.userId = userId;
		this.firmId = firmId;
		this.partnerFirmId = partnerFirmId;
		this.exchangeCode = exchangeCode;
		this.stationId = stationId;
	}
	
	public long getMode() {
		return mode;
	}
	
	public long getId() {
		return id;
	}
	
	public long getOrderId() {
		return orderId;
	}
	
	public String getClassCode() {
		return classCode;
	}
	
	public String getSecCode() {
		return secCode;
	}
	
	public double getPrice() {
		return price;
	}
	
	public long getQty() {
		return qty;
	}
	
	public double getValue() {
		return value;
	}
	
	public boolean isSell() {
		return isSell;
	}

	public long getDate() {
		return date;
	}
	
	public long getSettleDate() {
		return settleDate;
	}
	
	public long getTime() {
		return time;
	}
	
	public boolean isMarginal() {
		return isMarginal;
	}
	
	public double getAccruedInt() {
		return accruedInt;
	}
	
	public double getYield() {
		return yield;
	}
	
	public double getTsCommission() {
		return tsCommission;
	}
	
	public double getClearingCenterCommission() {
		return clearingCenterCommission;
	}
	
	public double getExchangeCommission() {
		return exchangeCommission;
	}
	
	public double getTradingSystemCommission() {
		return tradingSystemCommission;
	}
	
	public double getPrice2() {
		return price2;
	}
	
	public double getRepoRate() {
		return repoRate;
	}
	
	public double getRepoValue() {
		return repoValue;
	}
	
	public double getRepo2Value() {
		return repo2Value;
	}
	
	public double getAccruedInt2() {
		return accruedInt2;
	}
	
	public long getRepoTerm() {
		return repoTerm;
	}
	
	public double getStartDiscount() {
		return startDiscount;
	}
	
	public double getLowerDiscount() {
		return lowerDiscount;
	}
	
	public double getUpperDiscount() {
		return upperDiscount;
	}
	
	public boolean getBlockSecurities() {
		return blockSecurities;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public String getSettleCurrency() {
		return settleCurrency;
	}
	
	public String getSettleCode() {
		return settleCode;
	}
	
	public String getAccount() {
		return account;
	}
	
	public String getBrokerRef() {
		return brokerRef;
	}
	
	public String getClientCode() {
		return clientCode;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getFirmId() {
		return firmId;
	}
	
	public String getPartnerFirmId() {
		return partnerFirmId;
	}
	
	public String getExchangeCode() {
		return exchangeCode;
	}
	
	public String getStationId() {
		return stationId;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == T2QTrade.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}
	
	protected boolean fieldsEquals(Object other) {
		T2QTrade o = (T2QTrade) other;
		return new EqualsBuilder()
			.append(mode, o.mode)
			.append(id, o.id)
			.append(orderId, o.orderId)
			.append(classCode, o.classCode)
			.append(secCode, o.secCode)
			.append(price, o.price)
			.append(qty, o.qty)
			.append(value, o.value)
			.append(isSell, o.isSell)
			.append(date, o.date)
			.append(settleDate, o.settleDate)
			.append(time, o.time)
			.append(isMarginal, o.isMarginal)
			.append(accruedInt, o.accruedInt)
			.append(yield, o.yield)
			.append(tsCommission, o.tsCommission)
			.append(clearingCenterCommission, o.clearingCenterCommission)
			.append(exchangeCommission, o.exchangeCommission)
			.append(tradingSystemCommission, o.tradingSystemCommission)
			.append(price2, o.price2)
			.append(repoRate, o.repoRate)
			.append(repoValue, o.repoValue)
			.append(repo2Value, o.repo2Value)
			.append(accruedInt2, o.accruedInt2)
			.append(repoTerm, o.repoTerm)
			.append(startDiscount, o.startDiscount)
			.append(lowerDiscount, o.lowerDiscount)
			.append(upperDiscount, o.upperDiscount)
			.append(blockSecurities, o.blockSecurities)
			.append(currency, o.currency)
			.append(settleCurrency, o.settleCurrency)
			.append(settleCode, o.settleCode)
			.append(account, o.account)
			.append(brokerRef, o.brokerRef)
			.append(clientCode, o.clientCode)
			.append(userId, o.userId)
			.append(firmId, o.firmId)
			.append(partnerFirmId, o.partnerFirmId)
			.append(exchangeCode, o.exchangeCode)
			.append(stationId, o.stationId)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "["
			+ (isSell ? "Sell" : "Buy") + ", "
			+ "sec=" + secCode + "@" + classCode + ", "
			+ "price=" + price + ", "
			+ "qty=" + qty + ", "
			+ "val=" + value + ", "
			+ "id=" + id + ", "
			+ "orderId=" + orderId + ", "
			+ "date=" + date + ", "
			+ "time=" + time
			+ "]";
	}
	
}
