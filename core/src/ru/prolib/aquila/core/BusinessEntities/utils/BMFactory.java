package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrders;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolios;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurities;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.OrderBuilder;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.Validator;

/**
 * Интерфейс фабрики элементов бизнес-модели.
 * <p>
 * 2012-08-17<br>
 * $Id: BMFactory.java 503 2013-02-07 20:06:40Z whirlwind $
 */
public interface BMFactory {
	
	/**
	 * Создать редактируемый набор редактируемых инструментов.
	 * <p>
	 * @param defaultCurrency валюта по-кмолчанию
	 * @param defaultType тип по-умолчанию
	 * @return набор инструментов
	 */
	public EditableSecurities createSecurities(String defaultCurrency,
			SecurityType defaultType);
	
	/**
	 * Создать редактируемый набор редактируемых портфелей.
	 * <p>
	 * @return набор портфелей
	 */
	public EditablePortfolios createPortfolios();
	
	/**
	 * Создать редактируемый набор заявок.
	 * <p>
	 * @return набор заявок
	 */
	public EditableOrders createOrders();
	
	/**
	 * Создать фабрику экземпляров заявок.
	 * <p>
	 * @return фабрика заявок
	 */
	public OrderFactory createOrderFactory();
	
	/**
	 * Создать фабрику экземпляров портфелей.
	 * <p>
	 * @return фабрика портфелей
	 */
	public PortfolioFactory createPortfolioFactory();
	
	/**
	 * Создать фабрику экземпляров позиций для торгового счета.
	 * <p>
	 * @param account торговый счет
	 * @return фабрика позиций
	 */
	public PositionFactory createPositionFactory(Account account);
	
	/**
	 * Создать фабрику экземпляров инструментов.
	 * <p>
	 * @return фабрика инструментов
	 */
	public SecurityFactory createSecurityFactory();
	
	/**
	 * Создать фабрику сделок.
	 * <p>
	 * @return фабрика сделок
	 */
	public TradeFactory createTradeFactory();
	
	/**
	 * Создать конструктор заявок.
	 * <p>
	 * @return конструктор заявок
	 */
	public OrderBuilder createOrderBuilder();
	
	/**
	 * Создать конструктор заявок.
	 * <p>
	 * @param transId нумератор транзакций
	 * @return конструктор заявок
	 */
	public OrderBuilder createOrderBuilder(Counter transId);
	
	/**
	 * Создать конструктор заявок.
	 * <p>
	 * @param transId нумератор транзакций
	 * @param factory фабрика заявок
	 * @return конструктор заявок
	 */
	public OrderBuilder
			createOrderBuilder(Counter transId, OrderFactory factory);
	
	/**
	 * Создать сеттер, генерирующий стандартные события заявки.
	 * <p>
	 * Данный метод может использоваться в конструкторах модификаторов,
	 * для добавления в конец очереди специфических модификаторов объекта
	 * заявки. Данный фабричный метод объявлен в рамках фабрики бизнес модели,
	 * так как для инстанцирования требуется доступ к терминалу. 
	 * <p>
	 * @return сеттер-генератор событий
	 */
	@Deprecated
	public S<EditableOrder> createOrderEG();
	
	/**
	 * Создать сеттер, генерирующий стандартные события заявки.
	 * <p>
	 * Данный метод позволяет указывать специфический валидатор состояния
	 * заявки, который используется для всех объектов для которых не установлен
	 * признак доступности. Валидатор должен возвращать true, если объект
	 * может считаться доступным для использования. В отличии от этого метода,
	 * метод {@link #createOrderEG()} создает генератор, который рассматривает
	 * каждую заявку как доступную.
	 * <p>
	 * Данный метод может использоваться в конструкторах модификаторов,
	 * для добавления в конец очереди специфических модификаторов объекта
	 * заявки. Метод объявлен в рамках фабрики бизнес модели, так как для
	 * инстанцирования требуется доступ к атрибутам терминала.
	 * <p>
	 * @param isAvailable валидатор доступности заявки
	 * @return сеттер-генератор событий
	 */
	@Deprecated
	public S<EditableOrder> createOrderEG(Validator isAvailable);
	
	/**
	 * Создать сеттер, генерирующий стандартные события стоп-заявки.
	 * <p>
	 * См. {@link #createOrderEG()}. Отличается от генератора событий
	 * созданного методом {@link #createOrderEG()} только хранилищем заявок.
	 * Этот метод использует хранилище стоп-заявок, которое получено через
	 * вызов соответствующего метода терминала.
	 * <p>
	 * @return сеттер-генератор событий
	 */
	@Deprecated
	public S<EditableOrder> createStopOrderEG();
	
	/**
	/**
	 * Создать сеттер, генерирующий стандартные события стоп-заявки.
	 * <p>
	 * См. {@link #createOrderEG(Validator)}. Отличается от генератора событий
	 * созданного методом {@link #createOrderEG(Validator)} только хранилищем
	 * заявок. Этот метод использует хранилище стоп-заявок, которое получено
	 * через вызов соответствующего метода терминала.
	 * <p>
	 * @param isAvailable валидатор доступности заявки
	 * @return сеттер-генератор событий
	 */
	@Deprecated
	public S<EditableOrder> createStopOrderEG(Validator isAvailable);

	/**
	 * Создать сеттер, генерирующий стандартные события портфеля.
	 * <p>
	 * См. {@link BMFactory#createOrderEG()}
	 * <p>
	 * @return сеттер-генератор событий
	 */
	@Deprecated
	public S<EditablePortfolio> createPortfolioEG();
	
	/**
	 * Создать сеттер, генерирующий стандартные события портфеля.
	 * <p>
	 * См. {@link BMFactory#createOrderEG(Validator)}
	 * <p>
	 * @param isAvailable валидатор доступности портфеля
	 * @return сеттер-генератор событий
	 */
	@Deprecated
	public S<EditablePortfolio> createPortfolioEG(Validator isAvailable);

	/**
	 * Создать сеттер, генерирующий стандартные события позиции.
	 * <p>
	 * См. {@link BMFactory#createOrderEG()}
	 * <p>
	 * @return сеттер-генератор событий
	 */
	@Deprecated
	public S<EditablePosition> createPositionEG();
	
	/**
	 * Создать сеттер, генерирующий стандартные события позиции.
	 * <p>
	 * См. {@link BMFactory#createOrderEG(Validator)}
	 * <p>
	 * @param isAvailable валидатор доступности позиции
	 * @return сеттер-генератор событий
	 */
	@Deprecated
	public S<EditablePosition> createPositionEG(Validator isAvailable);
	
	/**
	 * Создать сеттер, генерирующий стандартные события инструмента.
	 * <p>
	 * См. {@link BMFactory#createOrderEG()}
	 * <p>
	 * @return сеттер-генератор событий
	 */
	@Deprecated
	public S<EditableSecurity> createSecurityEG();

	/**
	 * Создать сеттер, генерирующий стандартные события инструмента.
	 * <p>
	 * См. {@link BMFactory#createOrderEG(Validator)}
	 * <p>
	 * @param isAvailable валидатор доступности инструмента
	 * @return сеттер-генератор событий
	 */
	@Deprecated
	public S<EditableSecurity> createSecurityEG(Validator isAvailable);

}
