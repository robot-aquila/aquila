package ru.prolib.aquila.core.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.SetterFactory;
import ru.prolib.aquila.core.utils.*;

/**
 * Фабрика модификаторов.
 * <p>
 * 2012-09-29<br>
 * $Id: MFactoryImpl.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class MFactoryImpl implements MFactory {
	private final GetterFactory gfactory;
	private final SetterFactory sfactory;
	
	/**
	 * Создать фабрику модификаторов.
	 * <p>
	 * @param gfactory фабрика геттеров 
	 * @param sfactory фабрика сеттеров
	 */
	public MFactoryImpl(GetterFactory gfactory, SetterFactory sfactory) {
		super();
		this.gfactory = gfactory;
		this.sfactory = sfactory;
	}
	
	/**
	 * Получить фабрику геттеров.
	 * <p>
	 * @return фабрика геттеров
	 */
	public GetterFactory getGetterFactory() {
		return gfactory;
	}
	
	/**
	 * Получить фабрику сеттеров.
	 * <p>
	 * @return фабрика сеттеров
	 */
	public SetterFactory getSetterFactory() {
		return sfactory;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121105, /*00*/3619)
			.append(gfactory)
			.append(sfactory)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof MFactoryImpl ) {
			MFactoryImpl o = (MFactoryImpl) other;
			return new EqualsBuilder()
				.append(gfactory, o.gfactory)
				.append(sfactory, o.sfactory)
				.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public S<EditableOrder> rowOrdAccount(String name) {
		return new MStd<EditableOrder>(gfactory.rowAccount(name),
				sfactory.orderSetAccount());
	}
	
	@Override
	public S<EditableOrder> rowOrdAccount(String code, String subCode) {
		return new MStd<EditableOrder>(gfactory.rowAccount(code, subCode),
				sfactory.orderSetAccount());
	}

	@Override
	public S<EditableOrder> rowOrdDir(String name, Object buyEquiv) {
		return new MStd<EditableOrder>(gfactory.rowOrderDir(name, buyEquiv),
				sfactory.orderSetDirection());
	}

	@Override
	public S<EditableOrder> rowOrdId(String name) {
		return new MStd<EditableOrder>(gfactory.rowLong(name),
				sfactory.orderSetId());
	}

	@Override
	public S<EditableOrder> rowOrdPrice(String name) {
		return new MStd<EditableOrder>(gfactory.rowDouble(name),
				sfactory.orderSetPrice());
	}

	@Override
	public S<EditableOrder> rowOrdQty(String name) {
		return new MStd<EditableOrder>(gfactory.rowLong(name),
				sfactory.orderSetQty());
	}

	@Override
	public S<EditableOrder> rowOrdQtyRest(String name) {
		return new MStd<EditableOrder>(gfactory.rowLong(name),
				sfactory.orderSetQtyRest());
	}

	@Override
	public S<EditableOrder> rowOrdSecDescr(G<SecurityDescriptor> gSecDescr) {
		return new MStd<EditableOrder>(gSecDescr,
				sfactory.orderSetSecurityDescriptor());
	}

	@Override
	public S<EditableOrder> rowOrdStatus(String name, Map<?, OrderStatus> map) {
		return new MStd<EditableOrder>(
				new GMapTR<OrderStatus>(gfactory.rowObject(name), map),
				sfactory.orderSetStatus());
	}

	@Override
	public S<EditableOrder> rowOrdType(String name, Map<?, OrderType> map) {
		return new MStd<EditableOrder>(
				new GMapTR<OrderType>(gfactory.rowObject(name), map),
				sfactory.orderSetType());
	}

	@Override
	public S<EditableOrder> rowOrdType(Map<Validator, OrderType> map) {
		return new MStd<EditableOrder>(new GMapVR<OrderType>(map),
				sfactory.orderSetType());
	}

	@Override
	public S<EditableOrder> rowOrdType(G<Security> gSec, String price) {
		return new MStd<EditableOrder>(
				new GOrderType(gSec, gfactory.rowDouble(price)),
				sfactory.orderSetType());
	}

	@Override
	public S<EditableSecurity> rowSecLot(String name) {
		return new MStd<EditableSecurity>(gfactory.rowInteger(name),
				sfactory.securitySetLotSize());
	}

	@Override
	public S<EditableSecurity> rowSecMaxPrice(String name) {
		return new MStd<EditableSecurity>(gfactory.rowDouble(name),
				sfactory.securitySetMaxPrice());
	}

	@Override
	public S<EditableSecurity> rowSecMinPrice(String name) {
		return new MStd<EditableSecurity>(gfactory.rowDouble(name),
				sfactory.securitySetMinPrice());
	}

	@Override
	public S<EditableSecurity> rowSecMinStepPrice(String name) {
		return new MStd<EditableSecurity>(gfactory.rowDouble(name),
				sfactory.securitySetMinStepPrice());
	}

	@Override
	public S<EditableSecurity> rowSecMinStepSize(String name) {
		return new MStd<EditableSecurity>(gfactory.rowDouble(name),
				sfactory.securitySetMinStepSize());
	}

	@Override
	public S<EditableSecurity> rowSecPrecision(String name) {
		return new MStd<EditableSecurity>(gfactory.rowInteger(name),
				sfactory.securitySetPrecision());
	}

	@Override
	public S<EditablePortfolio> rowPortCash(String name) {
		return new MStd<EditablePortfolio>(gfactory.rowDouble(name),
				sfactory.portfolioSetCash());
	}

	@Override
	public S<EditablePortfolio> rowPortVarMargin(String name) {
		return new MStd<EditablePortfolio>(gfactory.rowDouble(name),
				sfactory.portfolioSetVarMargin());
	}

	@Override
	public S<EditablePosition> rowPosVarMargin(String name) {
		return new MStd<EditablePosition>(gfactory.rowDouble(name),
				sfactory.positionSetVarMargin());
	}

	@Override
	public S<EditablePosition> rowPosOpenValue(String name) {
		return new MStd<EditablePosition>(gfactory.rowDouble(name),
				sfactory.positionSetOpenQty());
	}

	@Override
	public S<EditablePosition> rowPosLockValue(String name) {
		return new MStd<EditablePosition>(gfactory.rowDouble(name),
				sfactory.positionSetLockQty());
	}

	@Override
	public S<EditablePosition> rowPosCurrValue(String name) {
		return new MStd<EditablePosition>(gfactory.rowDouble(name),
				sfactory.positionSetCurrQty());
	}

	@Override
	public S<Trade> rowTrdDir(String name, Object buyEquiv) {
		return new MStd<Trade>(gfactory.rowOrderDir(name, buyEquiv),
				sfactory.tradeSetDirection());
	}

	@Override
	public S<Trade> rowTrdId(String name) {
		return new MStd<Trade>(gfactory.rowLong(name),
				sfactory.tradeSetId());
	}

	@Override
	public S<Trade> rowTrdPrice(String name) {
		return new MStd<Trade>(gfactory.rowDouble(name),
				sfactory.tradeSetPrice());
	}

	@Override
	public S<Trade> rowTrdQty(String name) {
		return new MStd<Trade>(gfactory.rowLong(name),
				sfactory.tradeSetQty());
	}

	@Override
	public S<Trade> rowTrdSecDescr(G<SecurityDescriptor> gSecDescr) {
		return new MStd<Trade>(gSecDescr, sfactory.tradeSetSecurityDescr());
	}

	@Override
	public S<Trade> rowTrdTime(String date, String time,
			String dateFormat, String timeFormat)
	{
		return new MStd<Trade>(
				gfactory.rowDate(date, time, dateFormat, timeFormat),
				sfactory.tradeSetTime());
	}

	@Override
	public S<Trade> rowTrdVolume(String name) {
		return new MStd<Trade>(gfactory.rowDouble(name),
				sfactory.tradeSetVolume());
	}

	@Override
	public S<EditableSecurity> rowSecLastPrice(String name) {
		return new MStd<EditableSecurity>(gfactory.rowDouble(name),
				sfactory.securitySetLastPrice());
	}

	@Override
	public S<Trade> rowTrdTime(String name) {
		return new MStd<Trade>(gfactory.rowDate(name), sfactory.tradeSetTime());
	}

	@Override
	public S<EditableSecurity> rowSecAskPrice(String name) {
		return new MStd<EditableSecurity>(gfactory.rowDouble(name),
				sfactory.securitySetAskPrice());
	}

	@Override
	public S<EditableSecurity> rowSecAskSize(String name) {
		return new MStd<EditableSecurity>(gfactory.rowLong(name),
				sfactory.securitySetAskSize());
	}

	@Override
	public S<EditableSecurity> rowSecBidPrice(String name) {
		return new MStd<EditableSecurity>(gfactory.rowDouble(name),
				sfactory.securitySetBidPrice());
	}

	@Override
	public S<EditableSecurity> rowSecBidSize(String name) {
		return new MStd<EditableSecurity>(gfactory.rowLong(name),
				sfactory.securitySetBidSize());
	}

	@Override
	public S<EditableSecurity> rowSecClosePrice(String name) {
		return new MStd<EditableSecurity>(gfactory.rowDouble(name),
				sfactory.securitySetClosePrice());
	}

	@Override
	public S<EditableSecurity> rowSecDisplayName(String name) {
		return new MStd<EditableSecurity>(gfactory.rowString(name),
				sfactory.securitySetDisplayName());
	}

	@Override
	public S<EditableSecurity> rowSecHighPrice(String name) {
		return new MStd<EditableSecurity>(gfactory.rowDouble(name),
				sfactory.securitySetHighPrice());
	}

	@Override
	public S<EditableSecurity> rowSecLowPrice(String name) {
		return new MStd<EditableSecurity>(gfactory.rowDouble(name),
				sfactory.securitySetLowPrice());
	}

	@Override
	public S<EditableSecurity> rowSecOpenPrice(String name) {
		return new MStd<EditableSecurity>(gfactory.rowDouble(name),
				sfactory.securitySetOpenPrice());
	}

	@Override
	public S<EditableSecurity> rowSecStatus(G<SecurityStatus> gStatus) {
		return new MStd<EditableSecurity>(gStatus,
				sfactory.securitySetStatus());
	}

	@Override
	public S<EditablePortfolio> rowPortBalance(String name) {
		return new MStd<EditablePortfolio>(gfactory.rowDouble(name),
				sfactory.portfolioSetBalance());
	}

}
