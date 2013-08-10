package ru.prolib.aquila.core.BusinessEntities;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;
import com.thoughtworks.xstream.annotations.*;


/**
 * Настройки позиций в рамках портфеля.
 * <p>
 * Данный класс инкапсулирует настройки позиций по инструментам, которые
 * включены в портфель и общие для всех позиций параметры в рамках портфеля. 
 */
@XStreamAlias("PortfolioSetup")
public class PortfolioSetup {
	@XStreamAlias("entries")
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
			setup = createPositionSetup();
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
	 * Получить карту спецификаций позиций.
	 * <p>
	 * @return карта спецификаций
	 */
	public synchronized Map<SecurityDescriptor, PositionSetup> getPositions() {
		return new LinkedHashMap<SecurityDescriptor, PositionSetup>(map);
	}
	
	/**
	 * Получить список инструментов.
	 * <p>
	 * @return список инструментов, включенных в сетап
	 */
	public synchronized List<SecurityDescriptor> getSecurities() {
		return new Vector<SecurityDescriptor>(map.keySet());
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PortfolioSetup.class ) {
			return false;
		}
		return fieldsEquals(other);
	}
	
	protected boolean fieldsEquals(Object other) {
		PortfolioSetup o = (PortfolioSetup) other;
		return new EqualsBuilder()
			.append(map, o.map)
			.isEquals();		
	}
	
	/**
	 * Удалить настройки позиций.
	 */
	public synchronized void removeAll() {
		map.clear();
	}
	
	/**
	 * Создать экземпляр спецификатора позиции.
	 * <p>
	 * @return спецификатор позиции
	 */
	protected PositionSetup createPositionSetup() {
		return new PositionSetup();
	}

}
