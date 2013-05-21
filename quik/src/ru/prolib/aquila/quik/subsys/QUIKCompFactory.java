package ru.prolib.aquila.quik.subsys;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.dde.utils.table.*;

/**
 * Фабрика компонентов конвертации данных.
 * <p>
 * TODO: выпилить, после полного перехода на DDE-кэш
 * <p>
 * 2012-10-19<br>
 * $Id: QUIKCompFactory.java 543 2013-02-25 06:35:27Z whirlwind $
 */
@Deprecated
public class QUIKCompFactory implements BMFactory {
	private final BMFactory bfactory;
	private final QUIKServiceLocator locator;
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QUIKCompFactory.class);
	}
	
	/**
	 * Создать фабрику компонентов.
	 * <p>
	 * @param locator сервис-локатор
	 * @param bfactory фабрика объектов бизнес-модели
	 */
	public QUIKCompFactory(QUIKServiceLocator locator, BMFactory bfactory) {
		super();
		this.locator = locator;
		this.bfactory = bfactory;
	}
	
	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return сервис-локатор
	 */
	public QUIKServiceLocator getServiceLocator() {
		return locator;
	}
	
	/**
	 * Получить фабрику объектов бизнес-модели.
	 * <p>
	 * @return фабрика бизнес-модели
	 */
	public BMFactory getBusinessModelFactory() {
		return bfactory;
	}

	/**
	 * Создать наблюдатель таблицы.
	 * <p>
	 * Создает наблюдатель таблицы с указанным топиком и обработчиком ряда.
	 * <p>
	 * @param tableName имя таблицы (топик)
	 * @param handler обработчик ряда
	 * @return наблюдатель таблицы
	 */
	public DDETableListener listenTable(String tableName, RowHandler handler) {
		return new DDETableListener(tableName,
			new DDETableHandlerImpl(new DDETableRowSetBuilderImpl(), handler));
	}
	
	/**
	 * Создать наблюдатель таблицы.
	 * <p>
	 * Создает наблюдатель таблицы с указанным топиком, обработчиком ряда и
	 * валидатором набора заголовков.
	 * <p>
	 * @param tableName имя таблицы (топик)
	 * @param handler обработчик ряда
	 * @param hdrValidator валидатор заголовков (получает Set&lt;String&gt;)
	 * @return наблюдатель таблицы
	 */
	public DDETableListener listenTable(String tableName, RowHandler handler,
			Validator hdrValidator)
	{
		return new DDETableListener(tableName,
			new DDETableHandlerImpl(
				new DDETableRowSetBuilderImpl(1, 1, hdrValidator), handler));
	}

	@Override
	public OrderFactory createOrderFactory() {
		return bfactory.createOrderFactory();
	}

	@Override
	public EditableOrders createOrders() {
		return bfactory.createOrders();
	}

	@Override
	public EditableSecurities createSecurities() {
		return bfactory.createSecurities();
	}

	@Override
	public TradeFactory createTradeFactory() {
		return bfactory.createTradeFactory();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121109, 94401)
			.append(locator)
			.append(bfactory)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == QUIKCompFactory.class ) {
			QUIKCompFactory o = (QUIKCompFactory) other;
			return new EqualsBuilder()
				.append(locator, o.locator)
				.append(bfactory, o.bfactory)
				.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public S<EditableOrder> createOrderEG() {
		return bfactory.createOrderEG();
	}

	@Override
	public S<EditableOrder> createOrderEG(Validator isAvailable) {
		return bfactory.createOrderEG(isAvailable);
	}

	@Override
	public S<EditablePortfolio> createPortfolioEG() {
		return bfactory.createPortfolioEG();
	}

	@Override
	public S<EditablePortfolio> createPortfolioEG(Validator isAvailable) {
		return bfactory.createPortfolioEG(isAvailable);
	}

	@Override
	public S<EditablePosition> createPositionEG() {
		return bfactory.createPositionEG();
	}

	@Override
	public S<EditablePosition> createPositionEG(Validator isAvailable) {
		return bfactory.createPositionEG(isAvailable);
	}

	@Override
	public S<EditableSecurity> createSecurityEG() {
		return bfactory.createSecurityEG();
	}

	@Override
	public S<EditableSecurity> createSecurityEG(Validator isAvailable) {
		return bfactory.createSecurityEG(isAvailable);
	}

	@Override
	public S<EditableOrder> createStopOrderEG() {
		return bfactory.createStopOrderEG();
	}

	@Override
	public S<EditableOrder> createStopOrderEG(Validator isAvailable) {
		return bfactory.createStopOrderEG(isAvailable);
	}

	@Override
	public EditablePortfolios createPortfolios() {
		return bfactory.createPortfolios();
	}

}
