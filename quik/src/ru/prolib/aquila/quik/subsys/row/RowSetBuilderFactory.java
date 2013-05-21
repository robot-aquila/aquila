package ru.prolib.aquila.quik.subsys.row;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.dde.utils.table.DDETableRowSetBuilder;
import ru.prolib.aquila.dde.utils.table.DDETableRowSetBuilderImpl;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;
import ru.prolib.aquila.quik.subsys.TableHeadersValidator;

/**
 * Фабрика конструкторов наборов рядов.
 * <p>
 * TODO: выпилить, после полного перехода на DDE-кэш
 * <p>
 * 2013-02-17<br>
 * $Id$
 */
@Deprecated
public class RowSetBuilderFactory {
	private final QUIKServiceLocator locator;
	private final RowAdapters adapters;
	
	public RowSetBuilderFactory(QUIKServiceLocator locator,
			RowAdapters adapters)
	{
		super();
		this.locator = locator;
		this.adapters = adapters;
	}
	
	public QUIKServiceLocator getServiceLocator() {
		return locator;
	}
	
	public RowAdapters getRowAdapters() {
		return adapters;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == RowSetBuilderFactory.class ) {
			RowSetBuilderFactory o = (RowSetBuilderFactory) other;
			return new EqualsBuilder()
				.append(locator, o.locator)
				.append(adapters, o.adapters)
				.isEquals();
		} else {
			return false;
		}
	}
	
	/**
	 * Создать конструктор рядов для таблицы всех сделок.
	 * <p>
	 * @return конструктор рядов
	 */
	public DDETableRowSetBuilder createAllDealsRowSetBuilder() {
		return new RowSetBuilder(new DDETableRowSetBuilderImpl(1, 1,
			new TableHeadersValidator(locator.getTerminal(),
				locator.getConfig().getAllDeals(),
				adapters.getAllDealsRequiredFields())),
			adapters.createAllDealsAdapters());
	}
	
	/**
	 * Создать конструктор рядов для таблицы портфелей по бумагам.
	 * <p>
	 * @return конструктор рядов
	 */
	public DDETableRowSetBuilder createPortfolioStkRowSetBuilder() {
		return new RowSetBuilder(new DDETableRowSetBuilderImpl(1, 1,
			new TableHeadersValidator(locator.getTerminal(),
				locator.getConfig().getPortfoliosSTK(),
				adapters.getPortfolioStkRequiredFields())),
			adapters.createPortfolioStkAdapters());
	}
	
	/**
	 * Создать конструктор рядов для таблицы портфелей по деривативам.
	 * <p>
	 * @return конструктор рядов
	 */
	public DDETableRowSetBuilder createPortfolioFutRowSetBuilder() {
		return new RowSetBuilderFilter( 
			new RowSetBuilderFilter( 
				new RowSetBuilder(new DDETableRowSetBuilderImpl(1, 1,
					new TableHeadersValidator(locator.getTerminal(),
						locator.getConfig().getPortfoliosFUT(),
						adapters.getPortfolioFutRequiredFields())),
					adapters.createPortfolioFutAdapters()),
				new ValidateLimitType()),
			new ValidatePortfolioRow(locator));
	}
	
	/**
	 * Создать конструктор рядов для таблицы позиций по бумагам.
	 * <p>
	 * @return конструктор рядов
	 */
	public DDETableRowSetBuilder createPositionStkRowSetBuilder() {
		return new RowSetBuilderFilter(
			new RowSetBuilder(new DDETableRowSetBuilderImpl(1, 1,
				new TableHeadersValidator(locator.getTerminal(),
					locator.getConfig().getPositionsSTK(),
					adapters.getPositionStkRequiredFields())),
				adapters.createPositionStkAdapters()),
			new ValidatePositionRow(locator));
	}
	
	/**
	 * Создать конструктор рядов для таблицы позиций по деривативам.
	 * <p>
	 * @return конструктор рядов
	 */
	public DDETableRowSetBuilder createPositionFutRowSetBuilder() {
		return new RowSetBuilder(new DDETableRowSetBuilderImpl(1, 1,
				new TableHeadersValidator(locator.getTerminal(),
					locator.getConfig().getPositionsFUT(),
					adapters.getPositionFutRequiredFields())),
				adapters.createPositionFutAdapters());
	}
	
	/**
	 * Создать конструктор рядов таблицы заявок.
	 * <p>
	 * @return конструктор рядов
	 */
	public DDETableRowSetBuilder createOrderRowSetBuilder() {
		return new RowSetBuilder(new DDETableRowSetBuilderImpl(1, 1,
			new TableHeadersValidator(locator.getTerminal(),
				locator.getConfig().getOrders(),
				adapters.getOrderRequiredFields())),
			adapters.createOrderAdapters());
	}
	
	/**
	 * Создать конструктор рядов таблицы стоп-заявок.
	 * <p>
	 * @return конструктор рядов
	 */
	public DDETableRowSetBuilder createStopOrderRowSetBuilder() {
		return new RowSetBuilder(new DDETableRowSetBuilderImpl(1, 1,
			new TableHeadersValidator(locator.getTerminal(),
				locator.getConfig().getStopOrders(),
				adapters.getStopOrderRequiredFields())),
			adapters.createStopOrderAdapters());
	}
	
	/**
	 * Создать конструктор рядов таблицы инструментов.
	 * <p>
	 * @return конструктор рядов
	 */
	public DDETableRowSetBuilder createSecurityRowSetBuilder() {
		return new RowSetBuilderFilter(
			new RowSetBuilder(new DDETableRowSetBuilderImpl(1, 1,
				new TableHeadersValidator(locator.getTerminal(),
					locator.getConfig().getSecurities(),
					adapters.getSecurityRequiredFields())),
				adapters.createSecurityAdapters()),
			new ValidateSecurityRow(locator));
	}

}
