package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс портфеля.
 * <p>
 * 2012-05-30<br>
 * $Id: Portfolio.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public interface Portfolio {
	public static final int VERSION = 1;
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public Terminal getTerminal();
	
	/**
	 * Получить торговый счет портфеля.
	 * <p>
	 * @return идентификатор счета
	 */
	public Account getAccount();
	
	/**
	 * Получить величину вариационной маржи.
	 * <p>
	 * @return вариационная маржа или null, если значение недоступно
	 */
	public Double getVariationMargin();
	
	/**
	 * Получить величину доступных денежных средств.
	 * <p>
	 * @return размер кэша или null, если значение недоступно
	 */
	public Double getCash();
	
	/**
	 * Получить баланс портфеля.
	 * <p>
	 * @return баланс или null, если баланс недоступен
	 */
	public Double getBalance();
	
	/**
	 * Получить тип события: при изменении атрибутов.
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged();
	
	
	/**
	 * Получить список позиций.
	 * <p>
	 * Возвращает список позиций по которым имеется какая-либо информация.
	 * <p>
	 * @return список позиций
	 */
	public List<Position> getPositions();
	
	/**
	 * Получить позицию.
	 * <p>
	 * Если позиция не существует, то создает и регистрирует новый экземпляр
	 * позиции. Никаких событий не генерирует и не меняет статус доступности.
	 * Фактически, если позиция создана в результате пользовательского запроса,
	 * первым событием будет событие о доступности, которое будет сгенерировано
	 * поставщиком данных. То есть, первичная установка атрибутов не
	 * рассматривается как изменение. Это нужно учитывать, в случае подписки
	 * на изменение позиции до того, как поставщик данных сделает ее доступной.
	 * <p>
	 * @param security инструмент
	 * @return позиция
	 */
	public Position getPosition(Security security);
	
	/**
	 * Получить тип события: при появлении информации о позиции.
	 * <p>
	 * Важно учитывать, что с точки зрения наблюдателя, получение события
	 * данного типа не гарантируется. Событие данного типа будет получено
	 * наблюдателем только для тех, позиций которые были добавлены в набор
	 * после подписки. Если на момент подписки в наборе уже есть позиции,
	 * наблюдатель не получит соответствующих событий, так как они уже
	 * отправлены наблюдателям. Такие позиции должны быть обработаны явно.
	 * <p>
	 * Если стоит задача обрабатывать поступление вообще всех позиций (например
	 * в целях подписки на изменение позиции), то в этом случае следует
	 * воспользоваться ретранслятором на более высоком уровне, например,
	 * соответствующее событие в составе терминала. 
	 * <p>
	 * @return тип события
	 */
	public EventType OnPositionAvailable();
	
	/**
	 * Перехватчик событий соответствующего типа от всех позиций.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPositionChanged();
	
	/**
	 * Получить количество позиций.
	 * <p>
	 * @return количество позиций
	 */
	public int getPositionsCount();
	
}