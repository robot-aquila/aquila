package ru.prolib.aquila.core.BusinessEntities;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;


/**
 * Спецификация портфеля.
 */
public class PortfolioSetup {
	private final Map<SecurityDescriptor, PositionSetup> map;
	
	/**
	 * Конструктор.
	 */
	public PortfolioSetup() {
		super();
		map = new LinkedHashMap<SecurityDescriptor, PositionSetup>();
	}
	
	/**
	 * Получить спецификацию позиции.
	 * <p>
	 * Если это новый инструмент, то создается настройка нейтральной позиции по
	 * инструменту.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return спецификация позиции
	 */
	public synchronized PositionSetup getPosition(SecurityDescriptor descr) {
		PositionSetup setup = map.get(descr);
		if ( setup == null ) {
			setup = new PositionSetup(descr);
			map.put(descr, setup);
		}
		return setup;
	}

	/**
	 * Удалить спецификацию позиции из портфеля.
	 * <p>
	 * @param descr дескриптор инструмента
	 */
	public synchronized void removePosition(SecurityDescriptor descr) {
		map.remove(descr);
	}

	/**
	 * Получить список спецификаций позиций.
	 * <p>
	 * @return список спецификаций
	 */
	public synchronized List<PositionSetup> getPositions() {
		return new Vector<PositionSetup>(map.values());
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PortfolioSetup.class ) {
			return false;
		}
		PortfolioSetup o = (PortfolioSetup) other;
		return new EqualsBuilder()
			.append(map, o.map)
			.isEquals();
	}
	
	@Override
	public synchronized PortfolioSetup clone() {
		PortfolioSetup copy = new PortfolioSetup();
		Iterator<Map.Entry<SecurityDescriptor, PositionSetup>> iterator;
		Map.Entry<SecurityDescriptor, PositionSetup> entry;
		iterator = map.entrySet().iterator();
		while ( iterator.hasNext() ) {
			entry = iterator.next();
			copy.map.put(entry.getKey(), entry.getValue().clone());
		}
		return copy;
	}

}
