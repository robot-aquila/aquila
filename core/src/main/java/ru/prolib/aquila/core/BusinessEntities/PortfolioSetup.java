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
	private final Map<Symbol, PositionSetup> map;
	
	/**
	 * Конструктор.
	 */
	public PortfolioSetup() {
		super();
		map = new LinkedHashMap<Symbol, PositionSetup>();
	}
	
	/**
	 * Получить спецификацию позиции.
	 * <p>
	 * Если это новый инструмент, то создается настройка нейтральной позиции по
	 * инструменту.
	 * <p>
	 * @param symbol дескриптор инструмента
	 * @return спецификация позиции
	 */
	public synchronized PositionSetup getPosition(Symbol symbol) {
		PositionSetup setup = map.get(symbol);
		if ( setup == null ) {
			setup = createPositionSetup();
			map.put(symbol, setup);
		}
		return setup;
	}

	/**
	 * Удалить спецификацию позиции из портфеля.
	 * <p>
	 * @param symbol дескриптор инструмента
	 */
	public synchronized void removePosition(Symbol symbol) {
		map.remove(symbol);
	}

	/**
	 * Получить карту спецификаций позиций.
	 * <p>
	 * @return карта спецификаций
	 */
	public synchronized Map<Symbol, PositionSetup> getPositions() {
		return new LinkedHashMap<Symbol, PositionSetup>(map);
	}
	
	/**
	 * Получить список инструментов.
	 * <p>
	 * @return список инструментов, включенных в сетап
	 */
	public synchronized List<Symbol> getSecurities() {
		return new Vector<Symbol>(map.keySet());
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
