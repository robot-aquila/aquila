package ru.prolib.aquila.core.report.trades;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.lang3.builder.*;
import org.joda.time.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.RTrade;

/**
 * Отчет по трейду.
 */
public class RTradeImpl implements ERTrade {
	private final SecurityDescriptor descr;
	private final PositionType type;
	private DateTime enterTime;
	private DateTime exitTime;
	private Long enterQty;
	private Long exitQty;
	private Double sumByEnterPrice;
	private Double sumByExitPrice;
	private Double enterVol;
	private Double exitVol;
	
	/**
	 * Конструктор (для тестов).
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param type тип трейда
	 * @param enterTime время входа
	 * @param exitTime время выхода
	 * @param enterQty количество трейда
	 * @param exitQty закрытое количество
	 * @param sumByEnterPrice сумма по цене входа
	 * @param sumByExitPrice сумма по цене выхода
	 * @param enterVol суммарный объем входа
	 * @param exitVol суммарный объем выхода
	 */
	public RTradeImpl(SecurityDescriptor descr, PositionType type,
			DateTime enterTime, DateTime exitTime, Long enterQty, Long exitQty,
			Double sumByEnterPrice, Double sumByExitPrice,
			Double enterVol, Double exitVol)
	{
		super();
		this.descr = descr;
		this.type = type;
		this.enterTime = enterTime;
		this.exitTime = exitTime;
		this.enterQty = enterQty;
		this.exitQty = exitQty;
		this.sumByEnterPrice = sumByEnterPrice;
		this.sumByExitPrice = sumByExitPrice;
		this.enterVol = enterVol;
		this.exitVol = exitVol;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param trade открывающая сделка
	 */
	public RTradeImpl(Trade trade) {
		super();
		descr = trade.getSecurityDescriptor();
		type = trade.getDirection() == Direction.BUY ?
				PositionType.LONG : PositionType.SHORT;
		enterTime = trade.getTime();
		enterQty = trade.getQty();
		sumByEnterPrice = trade.getPrice() * trade.getQty();
		enterVol = trade.getVolume();
	}

	@Override
	public PositionType getType() {
		return type;
	}
	
	@Override
	public synchronized Double getExitVolume() {
		return exitVol;
	}
	
	@Override
	public synchronized Double getEnterVolume() {
		return enterVol;
	}
	
	@Override
	public synchronized Double getExitPrice() {
		return sumByExitPrice != null && exitQty != null ?
				sumByExitPrice / exitQty : null;
	}
	
	@Override
	public synchronized Double getEnterPrice() {		
		return enterQty > 0 ? sumByEnterPrice / enterQty : null;
	}
	
	@Override
	public synchronized Long getQty() {
		return enterQty;
	}
	
	@Override
	public synchronized Long getUncoveredQty() {
		return exitQty != null ? enterQty - exitQty : enterQty;
	}
	
	@Override
	public SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}
	
	@Override
	public synchronized DateTime getExitTime() {
		return exitTime;
	}
	
	@Override
	public synchronized DateTime getEnterTime() {
		return enterTime;
	}
	
	@Override
	public synchronized ERTrade addTrade(Trade trade) {
		Direction dir = trade.getDirection(); 
		if ( (type == PositionType.LONG && dir == Direction.SELL)
		  || (type == PositionType.SHORT && dir == Direction.BUY) )
		{
			return appendToExit(trade);
		} else {
			appendToEnter(trade);
			return null;
		}
	}
	
	@Override
	public synchronized boolean isOpen() {
		return exitQty != null ? enterQty > exitQty : true;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != RTradeImpl.class ) {
			return false;
		}
		RTradeImpl o = (RTradeImpl) other;
		return new EqualsBuilder()
			.append(o.exitQty, exitQty)
			.append(o.exitTime, exitTime)
			.append(o.exitVol, exitVol)
			.append(o.descr, descr)
			.append(o.enterQty, enterQty)
			.append(o.enterTime, enterTime)
			.append(o.enterVol, enterVol)
			.append(o.sumByExitPrice, sumByExitPrice)
			.append(o.sumByEnterPrice, sumByEnterPrice)
			.append(o.type, type)
			.isEquals();
	}
	
	/**
	 * Добавить сделку в последовательность открытия.
	 * <p>
	 * @param trade сделка
	 */
	private void appendToEnter(Trade trade) {
		enterQty += trade.getQty();
		sumByEnterPrice += (trade.getPrice() * trade.getQty());
		enterVol += trade.getVolume();
	}
	
	/**
	 * Добавить сделку в последовательность закрытия.
	 * <p>
	 * @param trade сделка
	 * @return новый отчет, если сделка привела к развороту, иначе null
	 */
	private ERTrade appendToExit(Trade trade) {
		ERTrade next = null;
		Long currQty = trade.getQty();
		Double volPerUnit = trade.getVolume() / currQty;
		Long uncoveredQty = getUncoveredQty();
		if ( currQty > uncoveredQty ) {
			// Необходимо разбить сделку
			Long nextQty = currQty - uncoveredQty;
			next = new RTradeImpl(descr,
				type == PositionType.LONG ?
						PositionType.SHORT : PositionType.LONG,
				trade.getTime(), null,
				nextQty, null,
				trade.getPrice() * (double) nextQty, null,
				volPerUnit * (double) nextQty, null);
			currQty = uncoveredQty;
		}
		if ( exitQty == null ) {
			exitQty = currQty;
			sumByExitPrice = trade.getPrice() * (double) currQty;
			exitVol = volPerUnit * (double) currQty;
		} else {
			exitQty += currQty;
			sumByExitPrice += trade.getPrice() * (double) currQty;
			exitVol += volPerUnit * (double) currQty;
		}
		if ( ! isOpen() ) {
			exitTime = trade.getTime();
		}
		return next;
	}
	
	@Override
	public synchronized String toString() {
		return getClass().getSimpleName() + "["
			+ "sec=" + descr + ", "
			+ "type=" + type + ", "
			+ "enterTime=" + enterTime + ", "
			+ "enterPrice=" + getEnterPrice() + ", "
			+ "enterVol=" + enterVol + ", "
			+ "qty=" + enterQty + ", "
			+ "uncovered=" + getUncoveredQty() + ", "
			+ "exitPrice=" + getExitPrice() + ", "
			+ "exitVol=" + exitVol + ", "
			+ "exitTime=" + exitTime + "]";
	}
	
	@Override
	public synchronized int compareTo(RTrade o) {
		if ( o == null ) {
			return 1;
		}
		if ( enterTime.equals(o.getEnterTime()) ) {
			return 0;
		}
		return enterTime.isAfter(o.getEnterTime()) ? 1 : -1;
	}
	
	@Override
	public synchronized RTrade clone() {
		return new RTradeImpl(descr, type, enterTime, exitTime,
				enterQty, exitQty, sumByEnterPrice, sumByExitPrice,
				enterVol, exitVol);
	}

	@Override
	public synchronized Double getProfit() {
		if ( getUncoveredQty() != 0L ) {
			return null;
		} else {
			Double profit = round4(sumByExitPrice - sumByEnterPrice);
			return type == PositionType.SHORT ? -profit : profit;
		}
	}

	@Override
	public synchronized Double getProfitPerc() {
		if ( getUncoveredQty() != 0L ) {
			return null;
		} else {
			return round4(getProfit() / sumByEnterPrice * 100d);
		}
	}
	
	private Double round4(Double value) {
		if ( value == null ) {
			return null;
		} else {
			return new BigDecimal(value)
				.setScale(4, RoundingMode.HALF_UP)
				.doubleValue();
		}
	}

}
