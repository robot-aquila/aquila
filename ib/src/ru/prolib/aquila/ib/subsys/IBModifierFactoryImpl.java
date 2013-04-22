package ru.prolib.aquila.ib.subsys;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.ib.getter.IBGetterFactory;
import ru.prolib.aquila.ib.getter.IBGetterFactoryImpl;
import ru.prolib.aquila.ib.subsys.security.IBSecurityModifierOfContract;
import ru.prolib.aquila.ib.subsys.security.IBSecurityModifierOfTick;

/**
 * Фабрика модификаторов. 
 * <p>
 * 2012-12-20<br>
 * $Id: IBModifierFactoryImpl.java 553 2013-03-01 13:37:31Z whirlwind $
 */
public class IBModifierFactoryImpl implements IBModifierFactory {
	private final IBGetterFactory gfactory;
	private final SetterFactory sfactory;
	private final IBServiceLocator locator;
	
	/**
	 * Конструктор (для теста).
	 * <p>
	 * @param locator локатор сервисов
	 * @param gfactory фабрика геттеров
	 * @param sfactory фабрика сеттеров
	 */
	public IBModifierFactoryImpl(IBServiceLocator locator,
			IBGetterFactory gfactory, SetterFactory sfactory)
	{
		super();
		this.gfactory = gfactory;
		this.sfactory = sfactory;
		this.locator = locator;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param locator локатор сервисов
	 */
	public IBModifierFactoryImpl(IBServiceLocator locator) {
		this(locator, new IBGetterFactoryImpl(locator),new SetterFactoryImpl());
	}
	
	/**
	 * Получить фабрику геттеров.
	 * <p>
	 * @return фабрика геттеров
	 */
	public IBGetterFactory getGetterFactory() {
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
	
	/**
	 * Получить локатор сервисов.
	 * <p>
	 * @return локатор
	 */
	public IBServiceLocator getServiceLocator() {
		return locator;
	}
	
	/**
	 * Создать стандартный модификатор заявки.
	 * <p>
	 * @param getter геттер
	 * @param setter сеттер
	 * @return модификатор
	 */
	private S<EditableOrder> orderStd(G<?> getter, S<EditableOrder> setter) {
		return new MStd<EditableOrder>(getter, setter);
	}

	@Override
	public S<EditableOrder> orderOoAccount() {
		return orderStd(gfactory.openOrderAccount(),sfactory.orderSetAccount());
	}

	@Override
	public S<EditableOrder> orderOoDir() {
		return orderStd(gfactory.openOrderDir(), sfactory.orderSetDirection());
	}

	@Override
	public S<EditableOrder> orderOoQty() {
		return orderStd(gfactory.openOrderQty(), sfactory.orderSetQty());
	}

	@Override
	public S<EditableOrder> orderOoSecurityDescr() {
		return orderStd(gfactory.openOrderSecDescr(),
				sfactory.orderSetSecurityDescriptor());
	}

	@Override
	public S<EditableOrder> orderOoStatus() {
		return orderStd(gfactory.openOrderStatus(), sfactory.orderSetStatus());
	}

	@Override
	public S<EditableOrder> orderOoType() {
		return orderStd(gfactory.openOrderType(), sfactory.orderSetType());
	}

	@Override
	public S<EditableOrder> orderOsStatus() {
		return orderStd(gfactory.orderStatusStatus(),sfactory.orderSetStatus());
	}

	@Override
	public S<EditableOrder> orderOsQtyRest() {
		return orderStd(gfactory.orderStatusRemaining(),
				sfactory.orderSetQtyRest());
	}

	@Override
	public S<EditableSecurity> securityContract() {
		return new IBSecurityModifierOfContract();
	}

	/**
	 * Создать стандартный модификатор портфеля.
	 * <p>
	 * @param getter геттер
	 * @param setter сеттер
	 * @return модификатор
	 */
	private S<EditablePortfolio>
			portStd(G<?> getter, S<EditablePortfolio> setter)
	{
		return new MStd<EditablePortfolio>(getter, setter);
	}

	@Override
	public S<EditablePortfolio> portCash() {
		return portStd(gfactory.portCash(), sfactory.portfolioSetCash());
	}

	/**
	 * Создать стандартный модификатор позиции.
	 * <p>
	 * @param getter геттер
	 * @param setter сеттер
	 * @return модификатор
	 */
	private S<EditablePosition> posStd(G<?> getter,S<EditablePosition> setter) {
		return new MStd<EditablePosition>(getter, setter);
	}

	@Override
	public S<EditablePosition> posCurrQty() {
		return posStd(gfactory.posCurrValue(), sfactory.positionSetCurrQty());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121221, 211515)
			.append(locator)
			.append(gfactory)
			.append(sfactory)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBModifierFactoryImpl.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBModifierFactoryImpl o = (IBModifierFactoryImpl) other;
		return new EqualsBuilder()
			.append(locator, o.locator)
			.append(gfactory, o.gfactory)
			.append(sfactory, o.sfactory)
			.isEquals();
	}

	@Override
	public S<EditableSecurity> securityTick() {
		return new IBSecurityModifierOfTick();
	}

	@Override
	public S<EditableOrder> orderOsExecutedVolume() {
		return new MStd<EditableOrder>(gfactory.orderStatusExecutedVolume(),
				sfactory.orderSetExecutedVolume());
	}

	@Override
	public S<EditablePortfolio> portBalance() {
		return new MStd<EditablePortfolio>(gfactory.portBalance(),
				sfactory.portfolioSetBalance());
	}

	@Override
	public S<EditablePosition> posMarketValue() {
		return new MStd<EditablePosition>(gfactory.posMarketValue(),
				sfactory.positionSetMarketValue());
	}

	@Override
	public S<EditablePosition> posBookValue() {
		return new MStd<EditablePosition>(gfactory.posBalanceCost(),
				sfactory.positionSetBookValue());
	}

	@Override
	public S<EditablePosition> posVarMargin() {
		return new MStd<EditablePosition>(gfactory.posPL(),
				sfactory.positionSetVarMargin());
	}

	@Override
	public S<EditableOrder> orderOsAvgExecutedPrice() {
		return new MStd<EditableOrder>(gfactory.orderStatusAvgExecutedPrice(),
				sfactory.orderSetAvgExecutedPrice());
	}

}
