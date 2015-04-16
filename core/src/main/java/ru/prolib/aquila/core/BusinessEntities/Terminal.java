package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.Starter;

/**
 * Интерфейс терминала.
 * <p>
 * Терминал представляет собой фасад подключения к биржевому терминалу.
 * Данный интерфейс позволяет абстрагироваться от специфической реализации
 * подключений к различным терминальным программам. Интерфейс обеспечивает
 * доступ к основным объектам биржевой торговли таким, как инструменты
 * типа {@link Security} и портфели типа {@link Portfolio}.  
 * <p>
 * 2012-05-30<br>
 * $Id: Terminal.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public interface Terminal extends Starter, OrderProcessor, Scheduler {
	public static final int VERSION = 1;
	
	/**
	 * Проверить состояние терминала.
	 * <p>
	 * Терминал может в промежуточном состоянии. То есть, если терминал не
	 * запущен, это не гарантирует true в результате вызова {@link #stopped()}.
	 * <p>
	 * @return true если терминал запущен, иначе false
	 */
	public boolean started();
	
	/**
	 * Проверить состояние терминала.
	 * <p>
	 * Терминал может в промежуточном состоянии. То есть, если терминал не
	 * остановлен, это не гарантирует положительный результат вызова
	 * {@link #started()}.
	 * <p>
	 * @return true если терминал остановлен, иначе false
	 */
	public boolean stopped();
	
	/**
	 * Проверить состояние подключения терминала.
	 * <p>
	 * @return true если терминал подключен, иначе false
	 */
	public boolean connected();
	
	/**
	 * Получить текущее состояние терминала.
	 * <p>
	 * @return состояние терминала
	 */
	public TerminalState getTerminalState();
	
	/**
	 * Получить тип события: при подключении к удаленной системе.
	 * <p>
	 * Данное событие сигнализирует о подключении терминала к удаленной системе.
	 * Фактически данное событие должно рассматриваться программой как появление
	 * технической возможности получения данных и выполнения транзакций. Каждая
	 * реализация терминала должна реализовывать генерацию события данного
	 * типа независимо от того, подразумевает протокол взаимодействия с
	 * удаленной системой такое понятие как соединение или нет. Если соединение
	 * не предусмотрено протоколом, терминал должен генерировать событие данного
	 * типа сразу после старта терминала.
	 * <p>
	 * @return тип события
	 */
	public EventType OnConnected();
	
	/**
	 * Получить тип события: при отключении от удаленной системы.
	 * <p>
	 * Данное событие сигнализирует о разрыве соединения или запланированном
	 * отключении от удаленной системы после того, как соединение было
	 * установлено ранее. Каждая реализация терминала обязательно
	 * предусматривает генерацию данного события, даже если протокол
	 * взаимодействия не подразумевает наличие соединения. В этом случае,
	 * терминал генерирует событие данного типа непосредственно перед остановом
	 * терминала.  
	 * <p>
	 * @return тип события
	 */
	public EventType OnDisconnected();

	/**
	 * Получить тип события: при запуске терминала.
	 * <p>
	 * Данное событие генерируется после успешного старта терминала. Фактически
	 * данное событие должно рассматриваться как признание факта успешного
	 * функционирование терминала. При этом техническая возможность получения
	 * данных и исполнения транзакций не гарантируется, но подразумевается, что
	 * теоретически такая возможность доступна - все подсистемы терминала
	 * запущены, функционируют штатно и готовы принимать и обрабатывать данные
	 * удаленной системы. Сам терминал при этом может находиться например в
	 * состоянии открытия соединения с удаленной системой. 
	 * <p>
	 * @return тип события
	 */
	public EventType OnStarted();

	/**
	 * Получить тип события: при останове терминала.
	 * <p>
	 * Данное событие сигнализирует о запланированном или экстренном останове
	 * терминала после того, как он был запущен в работу. Получение данного
	 * событие свидетельствует о том, что терминал находится в нерабочем
	 * состоянии: никакие данные не поступают и не обрабатываются, транзакции
	 * не могут быть выполнены, попытки установления соединения с удаленной
	 * системой не выполняются. Попытки обращения к объектам терминала
	 * в состоянии останова могут завершиться неопределенным результатом или
	 * получением несогласованных данных (в случае экстренного останова).  
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopped();
	
	/**
	 * Получить тип события: паническое состояние терминала.
	 * <p>
	 * Позволяет отлавливать события типа {@link PanicEvent}.
	 * Данное событие сигнализирует о возникновении ситуации, когда возникает
	 * проблема технического характера, которая не может быть решена
	 * автоматически и по своему характеру может повлечь фатальные ошибки в
	 * логике работы торговой системы. Подобные ситуации должны быть детально
	 * рассмотрены и разрешены оператором. Например, восстановление соединения с
	 * удаленной системой может быть выполнено автоматически и не относится
	 * к паническому состоянию. Конечно это справедливо только в том случае,
	 * если не нарушены установленные для терминала пределы, после которых
	 * данная ситуация может рассматривается как неразрешимая. Но в другой
	 * ситуации, например в случае невозможности связать одни объекты
	 * бизнес-процесса с другими (позицию с инструментом, сделку с инструментом,
	 * заявку с инструментом, стоп-заявку с порожденной заявкой и т.п.) по
	 * причине отсутствия каких либо данных со стороны удаленной системы
	 * (удаленная система не настроена должным образом), продолжение работы
	 * может привести к потенциальными убытками. Как правило, после данного
	 * события следует экстренный останов терминала. Каждая пользовательская
	 * система в обязательном порядке должна информировать оператора о возникшей
	 * проблеме всеми доступными способами, выполняя мониторинг событий данного
	 * типа. 
	 * <p>
	 * @return тип события
	 */
	public EventType OnPanic();

	/**
	 * Получить тип события: терминал готов к работе.
	 * <p>
	 * Данное событие сигнализирует о переходе терминала в состояние готовности
	 * к приему запросов.
	 * <p>
	 * @return тип события
	 */
	public EventType OnReady();
	
	/**
	 * Получить тип события: терминал не готов к работе.
	 * <p>
	 * Данное событие сигнализирует о выходе терминала из состояния готовности
	 * к приему запросов. 
	 * <p>
	 * @return тип события
	 */
	public EventType OnUnready();
	
	/**
	 * Создать лимитную заявку.
	 * <p>
	 * Данный метод создает лимитную заявку. Новой заявке автоматически
	 * назначается очередной номер, по которому можно обращаться к заявке
	 * через терминал. В завершении генерируется событие о доступности новой
	 * заявки. Для подачи заявки в торговую систему следует использовать
	 * метод {@link #placeOrder(Order)}.
	 * <p>
	 * @param account торговый счет
	 * @param dir операция (направление заявки)
	 * @param security инструмент
	 * @param qty количество
	 * @param price цена
	 * @return экземпляр заявки
	 */
	public Order createOrder(Account account, Direction dir, Security security,
			long qty, double price);

	/**
	 * Создать рыночную заявку.
	 * <p>
 	 * Данный метод создает рыночную заявку. Новой заявке автоматически
	 * назначается очередной номер, по которому можно обращаться к заявке
	 * через терминал. В завершении генерируется событие о доступности новой
	 * заявки. Для подачи заявки в торговую систему следует использовать
	 * метод {@link #placeOrder(Order)}.
	 * <p>
	 * @param account торговый счет
	 * @param dir операция (направление заявки)
	 * @param security инструмент
	 * @param qty количество
	 * @return экземпляр заявки
	 */
	public Order createOrder(Account account, Direction dir, Security security,
			long qty);
	
	/**
	 * Создать лимитную заявку с условной активацией.
 	 * <p>
	 * Данный метод создает лимитную заявку с условной активацией. Новой заявке
	 * автоматически назначается очередной номер, по которому можно обращаться к
	 * заявке через терминал. В завершении генерируется событие о доступности
	 * новой заявки. Для начала отслеживания условия активации следует
	 * использовать метод {@link #placeOrder(Order)}.
	 * <p>
	 * @param account торговый счет
	 * @param dir операция (направление заявки)
	 * @param security инструмент
	 * @param qty количество
	 * @param price цена
	 * @param activator активатор заявки
	 * @return экземпляр заявки
	 */
	public Order createOrder(Account account, Direction dir, Security security,
			long qty, double price, OrderActivator activator);
	
	/**
	 * Создать рыночную заявку с условной активацией.
 	 * <p>
	 * Данный метод создает рыночную заявку с условной активацией. Новой заявке
	 * автоматически назначается очередной номер, по которому можно обращаться к
	 * заявке через терминал. В завершении генерируется событие о доступности
	 * новой заявки. Для начала отслеживания условия активации следует
	 * использовать метод {@link #placeOrder(Order)}.
	 * <p>
	 * @param account торговый счет
	 * @param dir операция (направление заявки)
	 * @param security инструмент
	 * @param qty количество
	 * @param activator активатор заявки
	 * @return экземпляр заявки
	 */
	public Order createOrder(Account account, Direction dir, Security security,
			long qty, OrderActivator activator);
	
	/**
	 * Инициировать использование инструмента.
	 * <p>
	 * Данный метод должен использоваться торговыми стратегиями для декларации
	 * инструментов, необходимых для работы.
	 * <p>
	 * Разные терминалы по-разному предоставляют данные об инструментах. В
	 * некоторых терминалах для того, что бы существующий в торговой системе
	 * инструмент получил отражение в локальном терминале, необходимо 
	 * предварительно отправить в удаленную систему запрос. В других
	 * реализациях, где набор доступных инструментов определяется удаленной
	 * стороной, этот метод может реализовывать проверку доступности инструмента
	 * по истечении определенного времени, которого должно быть достаточно для
	 * получения от удаленной системы полного списка инструментов.
	 * <p>
	 * В базовой реализации представляет собой метод-заглушку.
	 * <p>
	 * @param descr дескриптор инструмента
	 */
	public void requestSecurity(SecurityDescriptor descr);
	
	/**
	 * Тип события: Ошибка загрузки инструмента.
	 * <p>
	 * Данный тип события позволяет реагировать на возможные отклонения
	 * запросов, выполненных посредством вызова метода
	 * {@link #requestSecurity(SecurityDescriptor)}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnRequestSecurityError();
	

	/**
	 * Проверить наличие заявки.
	 * <p>
	 * @param id идентификатор заявки
	 * @return true - есть заявка с таким идентификатором
	 */
	public boolean isOrderExists(int id);
	
	/**
	 * Получить список заявок.
	 * <p>
	 * @return список заявок
	 */
	public List<Order> getOrders();
	
	/**
	 * Получить количество заявок.
	 * <p>
	 * @return количество заявок
	 */
	public int getOrdersCount();
	
	/**
	 * Получить заявку по идентификатору.
	 * <p>
	 * @param id идентификатор заявки
	 * @return заявка
	 * @throws OrderNotExistsException - TODO:
	 */
	public Order getOrder(int id) throws OrderException;
	
	/**
	 * Получить тип события: при поступлении информации о новой заявке.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderAvailable();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderCancelFailed();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderCancelled();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderChanged();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderDone();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderFailed();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderFilled();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderPartiallyFilled();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderRegistered();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderRegisterFailed();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderTrade();

	
	/**
	 * Проверить доступность информации о портфеле.
	 * <p>
	 * @param account идентификатор портфеля
	 * @return true если информация доступна, иначе - false
	 */
	public boolean isPortfolioAvailable(Account account);
	
	/**
	 * Получить тип события: при доступности информации по портфелю.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPortfolioAvailable();
	
	/**
	 * Получить список доступных портфелей.
	 * <p>
	 * @return список портфелей
	 */
	public List<Portfolio> getPortfolios();
	
	/**
	 * Получить портфель по идентификатору.
	 * <p>
	 * @param account счет портфеля
	 * @return экземпляр портфеля
	 * @throws PortfolioNotExistsException - TODO:
	 */
	public Portfolio getPortfolio(Account account)
		throws PortfolioException;
	
	/**
	 * Получить портфель по-умолчанию.
	 * <p>
	 * Метод возвращает портфель в зависимости от реализации терминала. Это
	 * может быть единственный доступный портфель или первый попавшийся портфель
	 * из набора доступных.
	 * <p>
	 * @throws PortfolioException - TODO:
	 * @return портфель по-умолчанию
	 */
	public Portfolio getDefaultPortfolio() throws PortfolioException;
	
	/**
	 * Перехватчик событий соответствующего типа от всех портфелей.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPortfolioChanged();
	
	/**
	 * Перехватчик событий соответствующего типа от всех портфелей.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPositionAvailable();
	
	/**
	 * Перехватчик событий соответствующего типа от всех портфелей.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPositionChanged();
	
	/**
	 * Получить количество доступных портфелей.
	 * <p>
	 * @return количество портфелей
	 */
	public int getPortfoliosCount();


	/**
	 * Получить список доступных инструментов
	 * <p>
	 * @return список инструментов
	 */
	public List<Security> getSecurities();
	
	/**
	 * Получить инструмент по дескриптору
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return инструмент
	 * @throws SecurityNotExistsException - TODO:
	 */
	public Security getSecurity(SecurityDescriptor descr)
			throws SecurityException;
	
	/**
	 * Проверить наличие инструмента по дескриптору.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return наличие инструмента
	 */
	public boolean isSecurityExists(SecurityDescriptor descr);

	/**
	 * Получить тип события: при появлении информации о новом инструменте.
	 * <p>
	 * Генерируется событие {@link SecurityEvent}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnSecurityAvailable();
	
	/**
	 * Перехватчик событий соответствующего типа от всех инструментов.
	 * <p>
	 * @return тип события
	 */
	public EventType OnSecurityChanged();
	
	/**
	 * Перехватчик событий соответствующего типа от всех инструментов.
	 * <p>
	 * @return тип события
	 */
	public EventType OnSecurityTrade();
	
	/**
	 * Получить количество доступных инструментов.
	 * <p>
	 * @return количество инструментов
	 */
	public int getSecuritiesCount();

}
