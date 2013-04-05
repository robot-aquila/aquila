package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.data.S;

/**
 * Фабрика сеттеров.
 * <p>
 * 2012-10-28<br>
 * $Id: SetterFactoryImpl.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class SetterFactoryImpl implements SetterFactory {
	
	/**
	 * Создать фабрику.
	 */
	public SetterFactoryImpl() {
		super();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof SetterFactoryImpl;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121103, 233205).toHashCode();
	}

	@Override
	public S<EditableOrder> orderSetAccount() {
		return new OrderSetAccount();
	}

	@Override
	public S<EditableOrder> orderSetDirection() {
		return new OrderSetDirection();
	}

	@Override
	public S<EditableOrder> orderSetId() {
		return new OrderSetId();
	}

	@Override
	public S<EditableOrder> orderSetLinkedOrderId() {
		return new OrderSetLinkedOrderId();
	}

	@Override
	public S<EditableOrder> orderSetOffset() {
		return new OrderSetOffset();
	}

	@Override
	public S<EditableOrder> orderSetPrice() {
		return new OrderSetPrice();
	}

	@Override
	public S<EditableOrder> orderSetQty() {
		return new OrderSetQty();
	}

	@Override
	public S<EditableOrder> orderSetQtyRest() {
		return new OrderSetQtyRest();
	}

	@Override
	public S<EditableOrder> orderSetSecurityDescriptor() {
		return new OrderSetSecurityDescriptor();
	}

	@Override
	public S<EditableOrder> orderSetSpread() {
		return new OrderSetSpread();
	}

	@Override
	public S<EditableOrder> orderSetStatus() {
		return new OrderSetStatus();
	}

	@Override
	public S<EditableOrder> orderSetStopLimitPrice() {
		return new OrderSetStopLimitPrice();
	}

	@Override
	public S<EditableOrder> orderSetTakeProfitPrice() {
		return new OrderSetTakeProfitPrice();
	}

	@Override
	public S<EditableOrder> orderSetTransactionId() {
		return new OrderSetTransactionId();
	}

	@Override
	public S<EditableOrder> orderSetType() {
		return new OrderSetType();
	}

	@Override
	public S<EditablePosition> positionSetCurrQty() {
		return new PositionSetCurrQty();
	}

	@Override
	public S<EditablePosition> positionSetLockQty() {
		return new PositionSetLockQty();
	}

	@Override
	public S<EditablePosition> positionSetOpenQty() {
		return new PositionSetOpenQty();
	}

	@Override
	public S<EditablePosition> positionSetVarMargin() {
		return new PositionSetVarMargin();
	}

	@Override
	public S<EditablePortfolio> portfolioSetCash() {
		return new PortfolioSetCash();
	}

	@Override
	public S<EditablePortfolio> portfolioSetVarMargin() {
		return new PortfolioSetVariationMargin();
	}

	@Override
	public S<EditableSecurity> securitySetLotSize() {
		return new SecuritySetLotSize();
	}

	@Override
	public S<EditableSecurity> securitySetMaxPrice() {
		return new SecuritySetMaxPrice();
	}

	@Override
	public S<EditableSecurity> securitySetMinPrice() {
		return new SecuritySetMinPrice();
	}

	@Override
	public S<EditableSecurity> securitySetMinStepPrice() {
		return new SecuritySetMinStepPrice();
	}

	@Override
	public S<EditableSecurity> securitySetMinStepSize() {
		return new SecuritySetMinStepSize();
	}

	@Override
	public S<EditableSecurity> securitySetPrecision() {
		return new SecuritySetPrecision();
	}

	@Override
	public S<EditableSecurity> securitySetLastPrice() {
		return new SecuritySetLastPrice();
	}

	@Override
	public S<Trade> tradeSetDirection() {
		return new TradeSetDirection();
	}

	@Override
	public S<Trade> tradeSetId() {
		return new TradeSetId();
	}

	@Override
	public S<Trade> tradeSetPrice() {
		return new TradeSetPrice();
	}

	@Override
	public S<Trade> tradeSetQty() {
		return new TradeSetQty();
	}

	@Override
	public S<Trade> tradeSetSecurityDescr() {
		return new TradeSetSecurityDescriptor();
	}

	@Override
	public S<Trade> tradeSetTime() {
		return new TradeSetTime();
	}

	@Override
	public S<Trade> tradeSetVolume() {
		return new TradeSetVolume();
	}

	@Override
	public S<EditableSecurity> securitySetDisplayName() {
		return new SecuritySetDisplayName();
	}

	@Override
	public S<EditableSecurity> securitySetAskSize() {
		return new SecuritySetAskSize();
	}

	@Override
	public S<EditableSecurity> securitySetAskPrice() {
		return new SecuritySetAskPrice();
	}

	@Override
	public S<EditableSecurity> securitySetBidSize() {
		return new SecuritySetBidSize();
	}

	@Override
	public S<EditableSecurity> securitySetBidPrice() {
		return new SecuritySetBidPrice();
	}

	@Override
	public S<EditableOrder> orderSetExecutedVolume() {
		return new OrderSetExecutedVolume();
	}

	@Override
	public S<EditablePortfolio> portfolioSetBalance() {
		return new PortfolioSetBalance();
	}

	@Override
	public S<EditableSecurity> securitySetOpenPrice() {
		return new SecuritySetOpenPrice();
	}

	@Override
	public S<EditableSecurity> securitySetClosePrice() {
		return new SecuritySetClosePrice();
	}

	@Override
	public S<EditableSecurity> securitySetHighPrice() {
		return new SecuritySetHighPrice();
	}

	@Override
	public S<EditableSecurity> securitySetLowPrice() {
		return new SecuritySetLowPrice();
	}

	@Override
	public S<EditableSecurity> securitySetStatus() {
		return new SecuritySetStatus();
	}

	@Override
	public S<EditablePosition> positionSetBookValue() {
		return new PositionSetBookValue();
	}

	@Override
	public S<EditablePosition> positionSetMarketValue() {
		return new PositionSetMarketValue();
	}

	@Override
	public S<EditableOrder> orderSetTime() {
		return new OrderSetTime();
	}

	@Override
	public S<EditableOrder> orderSetLastChangeTime() {
		return new OrderSetLastChangeTime();
	}

}
