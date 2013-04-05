package ru.prolib.aquila.quik.subsys.row;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.data.row.RowSetAdapter;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.utils.table.DDETableRowSetBuilder;

/**
 * Конструктор адаптируемого набора рядов для таблиц DDE.
 * <p>
 * 2013-02-16<br>
 * $Id$
 */
public class RowSetBuilder implements DDETableRowSetBuilder {
	private final DDETableRowSetBuilder builder;
	private final Map<String, G<?>> adapters;

	/**
	 * Конструктор.
	 * <p>
	 * @param builder оригинальный конструктор набора рядов
	 * @param adapters набор адаптеров эелементов ряда
	 */
	public RowSetBuilder(DDETableRowSetBuilder builder,
			Map<String, G<?>> adapters)
	{
		super();
		this.builder = builder;
		this.adapters = adapters;
	}
	
	/**
	 * Получить оригинальный конструктор.
	 * <p>
	 * @return конструктор
	 */
	public DDETableRowSetBuilder getOriginalBuilder() {
		return builder;
	}
	
	/**
	 * Получить набор адаптеров.
	 * <p>
	 * @return адаптеры
	 */
	public Map<String, G<?>> getAdapters() {
		return adapters;
	}

	@Override
	public RowSet createRowSet(DDETable table) {
		return new RowSetAdapter(builder.createRowSet(table), adapters);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == RowSetBuilder.class ) {
			RowSetBuilder o = (RowSetBuilder) other;
			return new EqualsBuilder()
				.append(builder, o.builder)
				.append(adapters, o.adapters)
				.isEquals();
		} else {
			return false;
		}
	}

}
