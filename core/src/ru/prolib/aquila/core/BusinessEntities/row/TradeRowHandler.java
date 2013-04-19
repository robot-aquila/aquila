package ru.prolib.aquila.core.BusinessEntities.row;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TradeFactory;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.data.row.RowException;
import ru.prolib.aquila.core.data.row.RowHandler;

/**
 * Типовой обработчик ряда с данными о сделке.
 * <p>
 * Используется для организации цикла генерации объектов сделок на основании
 * ряда. Использует фабрику сделок, для генерации сделки. Использует дескриптор
 * инструмента, который должен быть определен в процессе модификации сделки,
 * для генерации соответствующего события по инструменту. Если после применения
 * модификатора дескриптор сделки не определен, то генерируется событие о
 * паническом состоянии терминала.
 * <p>
 * TODO: удалить после того, как в связанных проектах использование будет
 * заменено на самостоятельную реализацию.
 * <p> 
 * 2012-09-03<br>
 * $Id$
 */
@Deprecated
public class TradeRowHandler implements RowHandler {
	private final EditableTerminal terminal;
	private final TradeFactory factory;
	private final S<Trade> modifier;
	
	/**
	 * Создать обработчик.
	 * <p>
	 * @param terminal терминал
	 * @param factory фабрика сделок
	 * @param modifier модификатор сделки
	 */
	public TradeRowHandler(EditableTerminal terminal,
			TradeFactory factory, S<Trade> modifier)
	{
		super();
		this.terminal = terminal;
		this.factory = factory;
		this.modifier = modifier;
	}
	
	/**
	 * Получить модификатор сделки.
	 * <p>
	 * @return модификатор сделки
	 */
	public S<Trade> getTradeModifier() {
		return modifier;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public FirePanicEvent getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить фабрику сделок.
	 * <p>
	 * @return фабрика
	 */
	public TradeFactory getTradeFactory() {
		return factory;
	}

	@Override
	public void handle(Row row) throws RowException {
		Trade trade = factory.createTrade();
		synchronized ( trade ) {
			try {
				modifier.set(trade, row);
			} catch ( ValueException e ) {
				throw new RowException(e);
			}
		}
		SecurityDescriptor descr = trade.getSecurityDescriptor();
		if ( descr == null ) {
			terminal.firePanicEvent(1,
					"Handle trade failed: security descriptor is NULL: {}",
					new Object[] { trade });
		} else {
			terminal.getEditableSecurity(descr).fireTradeEvent(trade);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == TradeRowHandler.class ) {
			TradeRowHandler o = (TradeRowHandler) other;
			return new EqualsBuilder()
				.append(terminal, o.terminal)
				.append(factory, o.factory)
				.append(modifier, o.modifier)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121105, /*0*/51623)
			.append(terminal)
			.append(factory)
			.append(modifier)
			.toHashCode();
	}

}
