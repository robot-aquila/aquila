package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.data.S;

/**
 * Интерфейс фабрики сеттеров.
 * <p>
 * 2012-10-28<br>
 * $Id: SetterFactory.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public interface SetterFactory {
	
	public S<EditableOrder> orderSetAccount();
	
	public S<EditableOrder> orderSetDirection();
	
	public S<EditableOrder> orderSetId();
	
	public S<EditableOrder> orderSetPrice();
	
	public S<EditableOrder> orderSetQty();
	
	public S<EditableOrder> orderSetQtyRest();
	
	public S<EditableOrder> orderSetSecurityDescriptor();
	
	public S<EditableOrder> orderSetStatus();
	
	public S<EditableOrder> orderSetType();
	
	public S<EditableOrder> orderSetExecutedVolume();
	
	public S<EditableOrder> orderSetAvgExecutedPrice();
	
	public S<EditableOrder> orderSetTime();
	
	public S<EditableOrder> orderSetLastChangeTime();
	
	public S<EditablePosition> positionSetCurrQty();
	
	public S<EditablePosition> positionSetLockQty();
	
	public S<EditablePosition> positionSetOpenQty();
	
	public S<EditablePosition> positionSetVarMargin();
	
	public S<EditablePosition> positionSetBookValue();
	
	public S<EditablePosition> positionSetMarketValue();
	
	public S<EditablePortfolio> portfolioSetCash();
	
	public S<EditablePortfolio> portfolioSetVarMargin();
	
	public S<EditablePortfolio> portfolioSetBalance();
	
	public S<EditableSecurity> securitySetLotSize();
	
	public S<EditableSecurity> securitySetMaxPrice();
	
	public S<EditableSecurity> securitySetMinPrice();
	
	public S<EditableSecurity> securitySetMinStepPrice();
	
	public S<EditableSecurity> securitySetMinStepSize();
	
	public S<EditableSecurity> securitySetPrecision();
	
	public S<EditableSecurity> securitySetLastPrice();
	
	public S<EditableSecurity> securitySetDisplayName();
	
	public S<EditableSecurity> securitySetAskSize();
	
	public S<EditableSecurity> securitySetAskPrice();
	
	public S<EditableSecurity> securitySetBidSize();
	
	public S<EditableSecurity> securitySetBidPrice();
	
	public S<EditableSecurity> securitySetOpenPrice();
	
	public S<EditableSecurity> securitySetClosePrice();
	
	public S<EditableSecurity> securitySetHighPrice();
	
	public S<EditableSecurity> securitySetLowPrice();
	
	public S<EditableSecurity> securitySetStatus();

	public S<Trade> tradeSetDirection();
	
	public S<Trade> tradeSetId();
	
	public S<Trade> tradeSetPrice();
	
	public S<Trade> tradeSetQty();
	
	public S<Trade> tradeSetSecurityDescr();
	
	public S<Trade> tradeSetTime();
	
	public S<Trade> tradeSetVolume();

}
