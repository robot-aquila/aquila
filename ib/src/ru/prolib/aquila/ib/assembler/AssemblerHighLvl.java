package ru.prolib.aquila.ib.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.assembler.cache.*;

/**
 * Высокоуровневые функции сборки.
 */
public class AssemblerHighLvl {
	private final AssemblerMidLvl middle;
	
	AssemblerHighLvl(AssemblerMidLvl middle) {
		super();
		this.middle = middle;
	}
	
	AssemblerHighLvl(IBEditableTerminal terminal) {
		this(new AssemblerMidLvl(terminal));
	}
	
	/**
	 * Получить среднеуровневые функции сборки.
	 * <p> 
	 * <b>Прим.</b> служебный метод.
	 * <p>
	 * @return набор функций
	 */
	AssemblerMidLvl getMiddleLevelAssembler() {
		return middle;
	}
	
	/**
	 * Получить фасад кэша данных.
	 * <p>
	 * @return экземпляр кэша
	 */
	Cache getCache() {
		return middle.getCache();
	}
	
	/**
	 * Получить экземпляр терминала.
	 * <p>
	 * @return терминал
	 */
	IBEditableTerminal getTerminal() {
		return middle.getTerminal();
	}
	
	/**
	 * Обновить атрибут портфеля.
	 * <p>
	 * @param entry кэш-запись значения атрибута
	 */
	public void update(PortfolioValueEntry entry) {
		middle.update(entry);
	}
	
	/**
	 * Собрать инструмент.
	 * <p>
	 * @param entry кэш-запись деталей контракта
	 */
	public void update(ContractEntry entry) {
		middle.update(entry);
	}
	
	/**
	 * Согласовать заявки.
	 */
	public void assembleOrders() {
		for ( OrderEntry entry : getCache().getOrderEntries() ) {
			middle.update(entry);
		}
	}
	
	/**
	 * Согласовать позиции.
	 */
	public void assemblePositions() {
		for ( PositionEntry entry : getCache().getPositionEntries() ) {
			middle.update(entry);
		}
	}
	
	/**
	 * Согласовать позицию.
	 * <p>
	 * @param entry кэш-запись позиции
	 */
	public void assemblePosition(PositionEntry entry) {
		middle.update(entry);
	}
	
	/**
	 * Согласовать заявку.
	 * <p>
	 * @param entry кэш-запись заявки
	 */
	public void assembleOrder(OrderEntry entry) {
		middle.update(entry);
	}
	
	/**
	 * Согласовать заявку по кэш-записи статуса.
	 * <p>
	 * @param entry кэш-запись статуса заявки
	 */
	public void assembleOrder(OrderStatusEntry entry) {
		OrderEntry orderEntry = getCache().getOrder(entry.getId());
		if ( orderEntry != null ) {
			middle.update(orderEntry);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AssemblerHighLvl.class ) {
			return false;
		}
		AssemblerHighLvl o = (AssemblerHighLvl) other;
		return new EqualsBuilder()
			.append(o.middle, middle)
			.isEquals();
	}

}
