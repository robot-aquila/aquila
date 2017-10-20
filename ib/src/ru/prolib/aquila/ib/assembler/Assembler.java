package ru.prolib.aquila.ib.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.assembler.cache.*;

/**
 * Фасад сборщика.
 */
public class Assembler {
	private final IBEditableTerminal terminal;
	private final AssemblerHighLvl high;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param high высокоуровневые функции сборки
	 */
	Assembler(IBEditableTerminal terminal, AssemblerHighLvl high) {
		super();
		this.terminal = terminal;
		this.high = high;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 */
	public Assembler(IBEditableTerminal terminal) {
		this(terminal, new AssemblerHighLvl(terminal));
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * <b>Прим.</b> Служебный метод.
	 * <p>
	 * @return терминал
	 */
	IBEditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить высокоуровневые функции сборки.
	 * <p>
	 * <b>Прим.</b> Служебный метод.
	 * <p>
	 * @return набор методов
	 */
	AssemblerHighLvl getHighLevelAssembler() {
		return high;
	}
	
	/**
	 * Обновить данные на основании деталей контракта.
	 * <p>
	 * @param entry кэш-запись контракта
	 */
	public void update(ContractEntry entry) {
		synchronized ( terminal ) {
			getCache().update(entry);
			high.update(entry);
			high.assemblePositions();
		}
	}
	
	public void update(PositionEntry entry) {
		synchronized ( terminal ) {
			getCache().update(entry);
			if ( getCache().getContract(entry.getContractId()) == null ) {
				terminal.requestContract(entry.getContractId());
			} else {
				high.assemblePosition(entry);
			}
		}
	}
	
	public void update(ExecEntry entry) {
		synchronized ( terminal ) {
			getCache().update(entry);
		}
	}
	
	/**
	 * Обновить портфель.
	 * <p>
	 * @param entry кэш-запись атрибута портфеля 
	 */
	public void update(PortfolioValueEntry entry) {
		synchronized ( terminal ) {
			high.update(entry);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Assembler.class ) {
			return false;
		}
		Assembler o = (Assembler) other;
		return new EqualsBuilder()
			.append(high, o.high)
			.appendSuper(terminal == o.terminal)
			.isEquals();
	}
	
	/**
	 * Получить фасад кэша данных.
	 * <p>
	 * @return кэш данных
	 */
	private Cache getCache() {
		return terminal.getCache();
	}

}
