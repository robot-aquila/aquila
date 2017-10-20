package ru.prolib.aquila.quik;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Конфигурация QUIK терминала.
 * <p>
 * 2012-11-09<br>
 * $Id: QUIKConfigImpl.java 547 2013-02-26 04:45:07Z whirlwind $
 */
public class QUIKConfigImpl implements QUIKConfig {
	public String quikPath;
	public String serviceName;
	public String securities;
	public String allDeals;
	public String trades;
	public String portfoliosSTK;
	public String portfoliosFUT;
	public String positionsSTK;
	public String positionsFUT;
	public String orders;
	public String stopOrders;
	public String dateFormat, timeFormat;
	public boolean skipTRANS2QUIK = false;
	
	/**
	 * Создать конфигурацию.
	 */
	public QUIKConfigImpl() {
		super();
	}

	@Override
	public String getSecurities() {
		return securities;
	}

	@Override
	public String getAllDeals() {
		return allDeals;
	}

	@Override
	public String getPortfoliosSTK() {
		return portfoliosSTK;
	}

	@Override
	public String getPortfoliosFUT() {
		return portfoliosFUT;
	}

	@Override
	public String getPositionsSTK() {
		return positionsSTK;
	}

	@Override
	public String getPositionsFUT() {
		return positionsFUT;
	}

	@Override
	public String getOrders() {
		return orders;
	}

	@Override
	public String getStopOrders() {
		return stopOrders;
	}
	
	@Override
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public String getQUIKPath() {
		return quikPath;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof QUIKConfigImpl ) {
			QUIKConfigImpl o = (QUIKConfigImpl) other;
			return new EqualsBuilder()
				.append(allDeals, o.allDeals)
				.append(orders, o.orders)
				.append(portfoliosFUT, o.portfoliosFUT)
				.append(portfoliosSTK, o.portfoliosSTK)
				.append(positionsFUT, o.positionsFUT)
				.append(positionsSTK, o.positionsSTK)
				.append(securities, o.securities)
				.append(stopOrders, o.stopOrders)
				.append(serviceName, o.serviceName)
				.append(quikPath, o.quikPath)
				.append(dateFormat, o.dateFormat)
				.append(timeFormat, o.timeFormat)
				.append(skipTRANS2QUIK, o.skipTRANS2QUIK)
				.append(trades, o.trades)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121109, 125809)
			.append(allDeals)
			.append(orders)
			.append(portfoliosFUT)
			.append(portfoliosSTK)
			.append(positionsFUT)
			.append(positionsSTK)
			.append(securities)
			.append(stopOrders)
			.append(serviceName)
			.append(quikPath)
			.append(dateFormat)
			.append(timeFormat)
			.append(skipTRANS2QUIK)
			.append(trades)
			.toHashCode();
	}

	@Override
	public String getDateFormat() {
		return dateFormat;
	}

	@Override
	public String getTimeFormat() {
		return timeFormat;
	}

	@Override
	public boolean skipTRANS2QUIK() {
		return skipTRANS2QUIK;
	}

	@Override
	public String getTrades() {
		return trades;
	}

}
