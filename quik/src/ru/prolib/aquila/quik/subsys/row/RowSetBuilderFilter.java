package ru.prolib.aquila.quik.subsys.row;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.data.row.RowSetFilter;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.utils.table.DDETableRowSetBuilder;

/**
 * Конструктора фильтра набора.
 * <p>
 * Конструктор набора на основе DDE таблицы, декорируя создаваемый исходным
 * конструктором набор рядов фильтром типа {@link
 * ru.prolib.aquila.core.data.row.RowSetFilter RowSetFilter} с указанным
 * валидатором ряда. 
 * <p>
 * TODO: выпилить, после полного перехода на DDE-кэш
 * <p>
 * 2013-02-18<br>
 * $Id$
 */
@Deprecated
public class RowSetBuilderFilter implements DDETableRowSetBuilder {
	private final DDETableRowSetBuilder builder;
	private final Validator validator;
	
	public RowSetBuilderFilter(DDETableRowSetBuilder builder,
			Validator validator)
	{
		super();
		this.builder = builder;
		this.validator = validator;
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
	 * Получить валидатор ряда.
	 * <p>
	 * @return валидатор
	 */
	public Validator getRowValidator() {
		return validator;
	}

	@Override
	public RowSet createRowSet(DDETable table) throws DDEException {
		return new RowSetFilter(builder.createRowSet(table), validator);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == RowSetBuilderFilter.class ) {
			RowSetBuilderFilter o = (RowSetBuilderFilter) other;
			return new EqualsBuilder()
				.append(builder, o.builder)
				.append(validator, o.validator)
				.isEquals();
		} else {
			return false;
		}
	}

}
