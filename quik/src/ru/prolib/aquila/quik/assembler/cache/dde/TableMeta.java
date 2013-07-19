package ru.prolib.aquila.quik.assembler.cache.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.dde.utils.table.DDETableRange;

/**
 * Дескриптор области DDE таблицы.
 */
public class TableMeta {
	private final DDETableRange range;
	
	public TableMeta(DDETableRange range) {
		super();
		this.range = range;
	}
	
	/**
	 * Проверить, имеется ли в области таблицы строка с заголовками.
	 * <p>
	 * @return true - есть строка заголовков, false - нет
	 */
	public boolean hasHeaderRow() {
		return range.getFirstRow() == 1;
	}
	
	/**
	 * Проверить, имеется ли в области таблицы строки с данными.
	 * <p>
	 * @return true - есть строки данных, false - нет
	 */
	public boolean hasDataRows() {
		return range.getLastRow() > 1;
	}
	
	/**
	 * Получить номер строки для первой строки области.
	 * <p>
	 * Возвращает номер строки оригинальной таблицы, соответствующей первой
	 * строке области таблицы.
	 * <p>
	 * @return номер строки или null, если таблица не содержит строк данных
	 */
	public Integer getDataFirstRowNumber() {
		if ( ! hasDataRows() ) {
			return null;
		}
		return range.getFirstRow() == 1 ? 2 : range.getFirstRow();
	}
	
	/**
	 * Получить границы области таблицы.
	 * <p>
	 * @return границы области
	 */
	public DDETableRange getTableRange() {
		return range;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != TableMeta.class ) {
			return false;
		}
		TableMeta o = (TableMeta) other;
		return new EqualsBuilder()
			.append(range, o.range)
			.isEquals();
	}
	
	/**
	 * Получить количество строк данных.
	 * <p>
	 * @return количество строк
	 */
	public int getDataRowCount() {
		return hasDataRows() ?
				range.getLastRow() - getDataFirstRowNumber() + 1 : 0;
	}

}
