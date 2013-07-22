package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.quik.QUIKEditableTerminal;
import ru.prolib.aquila.quik.assembler.cache.*;

/**
 * Функции сборки объектов модели.
 * <p>
 * Данный класс представляет собой набор функций уровня групп объектов.
 * Запросы на сборку отдельных объектов делегируются на более низкий уровень
 * сборки.
 */
public class AssemblerL1 {
	private final QUIKEditableTerminal terminal;
	private final AssemblerL2 l2;
	
	AssemblerL1(QUIKEditableTerminal terminal, AssemblerL2 l2) {
		super();
		this.terminal = terminal;
		this.l2 = l2;
	}
	
	AssemblerL1(QUIKEditableTerminal terminal) {
		this(terminal, new AssemblerL2(terminal));
	}
	
	QUIKEditableTerminal getTerminal() {
		return terminal;
	}
	
	AssemblerL2 getAssemblerL2() {
		return l2;
	}
	
	/**
	 * Собрать портфель.
	 * <p>
	 * См. {@link AssemblerL2#tryAssemble(PortfolioEntry)}.
	 * <p>
	 * @param entry кэш-запись портфеля
	 * @return всегда true
	 */
	public boolean tryAssemble(PortfolioEntry entry) {
		return l2.tryAssemble(entry);
	}

	/**
	 * Выполнить попытку сборки позиции.
	 * <p>
	 * См. {@link AssemblerL2#tryAssemble(PositionEntry)}.
	 * <p>
	 * @param entry кэш-запись позиции
	 * @return true - данные были применены, false - данные не согласованы
	 */
	public boolean tryAssemble(PositionEntry entry) {
		return l2.tryAssemble(entry);
	}
	
	/**
	 * Выполнить сборку инструмента.
	 * <p>
	 * См. {@link AssemblerL2#tryAssemble(SecurityEntry)}.
	 * <p>
	 * @param entry кэш-запись инструмента
	 * @return всегда true
	 */
	public boolean tryAssemble(SecurityEntry entry) {
		return l2.tryAssemble(entry);
	}
	
	/**
	 * Выполнить цикл сборки позиций по инструменту.
	 * <p>
	 * Выполняет попытку сборки всех кэшированных позиций, соответствующих
	 * инструменту с указанным кратким наименованием. В случае успешной сборки,
	 * кэш-запись позиции удаляется из кэша позиций.
	 * <p>
	 * @param securityShortName краткое наименование инструмента
	 */
	public void tryAssemblePositions(String securityShortName) {
		PositionsCache cache = terminal.getDataCache().getPositionsCache();
		synchronized ( cache ) {
			for ( PositionEntry entry :  cache.get(securityShortName) ) {
				if ( l2.tryAssemble(entry) ) {
					cache.purge(entry);
				}
			}
		}
	}
	
	/**
	 * Выполнить цикл обработки анонимных сделок.
	 */
	public void tryAssembleTrades() {
		TradesCache cache = terminal.getDataCache().getTradesCache();
		TradesEntry entry;
		while ( (entry = cache.getFirst()) != null ) {
			if ( ! l2.tryAssemble(entry) ) {
				break;
			}
			cache.purgeFirst();
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AssemblerL1.class ) {
			return false;
		}
		AssemblerL1 o = (AssemblerL1) other;
		return new EqualsBuilder()
			.appendSuper(o.terminal == terminal)
			.append(o.l2, l2)
			.isEquals();
	}

}
