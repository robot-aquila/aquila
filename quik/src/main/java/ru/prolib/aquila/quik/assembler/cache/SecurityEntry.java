package ru.prolib.aquila.quik.assembler.cache;

import java.util.Currency;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Запись строки таблицы инструментов.
 */
public class SecurityEntry extends CacheEntry {
	private final Integer lotSize;
	private final Double maxPrice;
	private final Double minPrice;
	private final Double minStepPrice;
	private final Double minStepSize;
	private final Integer precision;
	private final Double lastPrice;
	private final Double openPrice;
	private final Double closePrice;
	private final String displayName;
	private final String shortName;
	private final Double askPrice;
	private final Double bidPrice;
	private final Double highPrice;
	private final Double lowPrice;
	private final String code, classCode;
	private final Currency currency;
	private final SecurityType type;
	private QUIKSecurityDescriptor descriptor;
	private final Double initialPrice;
	private final Double initialMargin;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param lotSize размер лота
	 * @param maxPrice максимально-возможная цена (null - не определена)
	 * @param minPrice минимально-возможная цена (null - не определена)
	 * @param minStepPrice цена минимального шага в валюте шага
	 * @param minStepSize минимальный шаг цены
	 * @param precision точность цены в десятичных знаках
	 * @param lastPrice цена последней сделки
	 * @param openPrice цена открытия последней сессии
	 * @param closePrice цена закрытия предыдущей сессии
	 * @param displayName полное наименование
	 * @param shortName краткое наименование
	 * @param askPrice цена предложения
	 * @param bidPrice цена спроса
	 * @param highPrice максимальная цена за сессию
	 * @param lowPrice минимальная цена за сессию
	 * @param code код инструмента
	 * @param classCode код класса
	 * @param currency валюта инструмента
	 * @param type тип инструмента
	 * @param initialPrice расчетная цена
	 * @param initialMargin начальная маржа 
	 */
	public SecurityEntry(Integer lotSize, Double maxPrice, Double minPrice,
			Double minStepPrice, Double minStepSize, Integer precision,
			Double lastPrice, Double openPrice, Double closePrice,
			String displayName, String shortName, Double askPrice,
			Double bidPrice, Double highPrice, Double lowPrice,
			String code, String classCode, Currency currency, SecurityType type,
			Double initialPrice, Double initialMargin)
	{
		super();
		this.lotSize = lotSize;
		this.maxPrice = maxPrice;
		this.minPrice = minPrice;
		this.minStepPrice = minStepPrice;
		this.minStepSize = minStepSize;
		this.precision = precision;
		this.lastPrice = lastPrice;
		this.openPrice = openPrice;
		this.closePrice = closePrice;
		this.displayName = displayName;
		this.shortName = shortName;
		this.askPrice = askPrice;
		this.bidPrice = bidPrice;
		this.highPrice = highPrice;
		this.lowPrice = lowPrice;
		this.initialPrice = initialPrice;
		this.initialMargin = initialMargin;
		this.code = code;
		this.classCode = classCode;
		this.currency = currency;
		this.type = type;
	}
	
	public Double getInitialPrice() {
		return initialPrice;
	}
	
	public Double getInitialMargin() {
		return initialMargin;
	}
	
	public Integer getLotSize() {
		return lotSize;
	}
	
	public Double getMaxPrice() {
		return maxPrice;
	}
	
	public Double getMinPrice() {
		return minPrice;
	}
	
	public Double getMinStepPrice() {
		return minStepPrice;
	}
	
	public Double getMinStepSize() {
		return minStepSize;
	}
	
	public Integer getPrecision() {
		return precision;
	}
	
	public Double getLastPrice() {
		return lastPrice;
	}
	
	public Double getOpenPrice() {
		return openPrice;
	}
	
	public Double getClosePrice() {
		return closePrice;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public Double getAskPrice() {
		return askPrice;
	}
	
	public Double getBidPrice() {
		return bidPrice;
	}
	
	public Double getHighPrice() {
		return highPrice;
	}
	
	public Double getLowPrice() {
		return lowPrice;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getClassCode() {
		return classCode;
	}
	
	public Currency getCurrency() {
		return currency;
	}
	
	public SecurityType getType() {
		return type;
	}
		
	public synchronized QUIKSecurityDescriptor getDescriptor() {
		if ( descriptor == null ) {
			if ( type ==SecurityType.FUT ) {
				descriptor = new QUIKSecurityDescriptor(displayName, classCode,
						currency, type, code, shortName, displayName);
			} else {
				descriptor = new QUIKSecurityDescriptor(code, classCode,
						currency, type, code, shortName, displayName);
			}
		}
		return descriptor;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != SecurityEntry.class ) {
			return false;
		}
		SecurityEntry o = (SecurityEntry) other;
		return new EqualsBuilder()
			.append(askPrice, o.askPrice)
			.append(bidPrice, o.bidPrice)
			.append(closePrice, o.closePrice)
			.append(displayName, o.displayName)
			.append(highPrice, o.highPrice)
			.append(lastPrice, o.lastPrice)
			.append(lotSize, o.lotSize)
			.append(lowPrice, o.lowPrice)
			.append(maxPrice, o.maxPrice)
			.append(minPrice, o.minPrice)
			.append(minStepPrice, o.minStepPrice)
			.append(minStepSize, o.minStepSize)
			.append(openPrice, o.openPrice)
			.append(precision, o.precision)
			.append(shortName, o.shortName)
			.append(code, o.code)
			.append(classCode, o.classCode)
			.append(currency, o.currency)
			.append(type, o.type)
			.append(initialPrice, o.initialPrice)
			.append(initialMargin, o.initialMargin)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "["
			+ "code=" + code + ", "
			+ "class=" + classCode + ", "
			+ "currency=" + currency + ", "
			+ "type=" + type + ", "
			+ "dispName=" + displayName + ", "
			+ "shortName=" + shortName + ", "
			
			+ "ask=" + askPrice + ", "
			+ "bid=" + bidPrice + ", "
			+ "close=" + closePrice + ", "
			+ "high=" + highPrice + ", "
			+ "last=" + lastPrice + ", "
			+ "lot=" + lotSize + ", "
			+ "low=" + lowPrice + ", "
			+ "max=" + maxPrice + ", "
			+ "min=" + minPrice + ", "
			+ "minStepPrice=" + minStepPrice + ", "
			+ "minStepSize=" + minStepSize + ", "
			+ "open=" + openPrice + ", "
			+ "prec=" + precision + ", "
			+ "initPrice=" + initialPrice + ", "
			+ "initMargin=" + initialMargin
			+ "]";
	}

}
