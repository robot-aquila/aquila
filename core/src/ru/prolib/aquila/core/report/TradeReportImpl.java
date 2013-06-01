package ru.prolib.aquila.core.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.builder.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Отчет по трейду.
 */
public class TradeReportImpl implements EditableTradeReport {
	private static final SimpleDateFormat format;
	private final SecurityDescriptor descr;
	private final PositionType type;
	private Date enterTime;
	private Date exitTime;
	private Long enterQty;
	private Long exitQty;
	private Double sumByEnterPrice;
	private Double sumByExitPrice;
	private Double enterVol;
	private Double exitVol;
	
	static {
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
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
	TradeReportImpl(SecurityDescriptor descr, PositionType type,
			Date enterTime, Date exitTime, Long enterQty, Long exitQty,
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
	public TradeReportImpl(Trade trade) {
		super();
		descr = trade.getSecurityDescriptor();
		type = trade.getDirection() == OrderDirection.BUY ?
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
	public synchronized Date getExitTime() {
		return exitTime;
	}
	
	@Override
	public synchronized Date getEnterTime() {
		return enterTime;
	}
	
	@Override
	public synchronized EditableTradeReport addTrade(Trade trade) {
		OrderDirection dir = trade.getDirection(); 
		if ( (type == PositionType.LONG && dir == OrderDirection.SELL)
		  || (type == PositionType.SHORT && dir == OrderDirection.BUY) )
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
		if ( other == null || other.getClass() != TradeReportImpl.class ) {
			return false;
		}
		TradeReportImpl o = (TradeReportImpl) other;
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
	private EditableTradeReport appendToExit(Trade trade) {
		EditableTradeReport next = null;
		Long currQty = trade.getQty();
		Double volPerUnit = trade.getVolume() / currQty;
		Long uncoveredQty = getUncoveredQty();
		if ( currQty > uncoveredQty ) {
			// Необходимо разбить сделку
			Long nextQty = currQty - uncoveredQty;
			next = new TradeReportImpl(descr,
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
			+ "enterTime=" + formatTime(enterTime) + ", "
			+ "enterPrice=" + getEnterPrice() + ", "
			+ "enterVol=" + enterVol + ", "
			+ "qty=" + enterQty + ", "
			+ "uncovered=" + getUncoveredQty() + ", "
			+ "exitPrice=" + getExitPrice() + ", "
			+ "exitVol=" + exitVol + ", "
			+ "exitTime=" + formatTime(exitTime) + "]";
	}
	
	/**
	 * Форматировать время.
	 * <p>
	 * @param time время
	 * @return строка времени или null, если время не определено
	 */
	private String formatTime(Date time) {
		return time == null ? null : format.format(time);
	}

	@Override
	public int compareTo(TradeReport o) {
		if ( o == null ) {
			return 1;
		}
		if ( enterTime.equals(o.getEnterTime()) ) {
			return 0;
		}
		return enterTime.after(o.getEnterTime()) ? 1 : -1;
	}
	
	@Override
	public TradeReport clone() {
		return new TradeReportImpl(descr, type, enterTime, exitTime,
				enterQty, exitQty, sumByEnterPrice, sumByExitPrice,
				enterVol, exitVol);
	}

}
