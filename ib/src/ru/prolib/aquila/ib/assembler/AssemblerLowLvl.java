package ru.prolib.aquila.ib.assembler;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.setter.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.ib.*;
import ru.prolib.aquila.ib.api.ContractHandler;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.cache.*;

/**
 * Низкоуровневые функции сборки.
 */
public class AssemblerLowLvl {
	private static final Logger logger;
	private static final String SEP = ".";
	private static final String CUR = "BASE";
	private static final String CASH = "TotalCashBalance";
	private static final String BALANCE = "NetLiquidationByCurrency";
	private static final Map<String, S<EditablePortfolio>> portfolioSetterMap;
	
	static {
		logger = LoggerFactory.getLogger(AssemblerLowLvl.class);
		portfolioSetterMap = new Hashtable<String, S<EditablePortfolio>>();
		portfolioSetterMap.put(CUR + SEP + CASH, new PortfolioSetCash());
		portfolioSetterMap.put(CUR + SEP + BALANCE, new PortfolioSetBalance());
	}
	
	private final IBEditableTerminal terminal;
	
	AssemblerLowLvl(IBEditableTerminal terminal) {
		super();
		this.terminal = terminal;
	}
	
	/**
	 * Получить экземпляр терминала.
	 * <p>
	 * @return терминал
	 */
	IBEditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить фасад кэша данных.
	 * <p>
	 * @return кэш данных
	 */
	Cache getCache() {
		return terminal.getCache();
	}
	
	/**
	 * Получить инструмент, соответствующий кэш-записи контракта.
	 * <p>
	 * Создает или возвращает существующий экземпляр инструмента,
	 * соответствующий указанному контракту.
	 * <p>
	 * @param entry кэш-запись деталей контракта
	 * @return инструмент
	 * @throws SecurityException 
	 */
	public EditableSecurity getSecurity(ContractEntry entry)
		throws SecurityException
	{
		SecurityDescriptor descr = entry.getSecurityDescriptor();
		if ( terminal.isSecurityExists(descr) ) {
			return terminal.getEditableSecurity(descr);
		} else {
			return terminal.createSecurity(descr);
		}
	}
	
	/**
	 * Получить инструмент, соответствующий номеру контракта.
	 * <p>
	 * Соответствующая запись деталей контракта ищется в кэше данных, после
	 * чего работа выполняется через метод {@link #getSecurity(ContractEntry)}.
	 * <p>
	 * @param conId номер контракта
	 * @return инструмент или null, если инструмент еще не доступен
	 * @throws SecurityException
	 */
	public EditableSecurity getSecurity(int conId)
		throws SecurityException
	{
		ContractEntry entry = getCache().getContract(conId);
		if ( entry == null ) {
			return null;
		}
		return getSecurity(entry);
	}
	
	/**
	 * Получить портфель по счету.
	 * <p>
	 * Создает или возвращает существующий портфель, соответствующий
	 * указанному счету.
	 * <p>
	 * @param account торговый счет
	 * @return портфель
	 * @throws PortfolioException 
	 */
	public EditablePortfolio getPortfolio(Account account)
		throws PortfolioException
	{
		if ( terminal.isPortfolioAvailable(account) ) {
			return terminal.getEditablePortfolio(account);
		} else {
			return terminal.createPortfolio(account);
		}
	}
	
	/**
	 * Генерировать события портфеля.
	 * <p>
	 * В зависимости от состояния портфеля генерирует соответствующие события.
	 * <p>
	 * @param portfolio экземпляр портфеля
	 * @throws EditableObjectException исключение портфеля
	 */
	public void fireEvents(EditablePortfolio portfolio)
		throws EditableObjectException
	{
		if ( portfolio.hasChanged() ) {
			if ( portfolio.isAvailable() ) {
				portfolio.fireChangedEvent();
			} else {
				portfolio.setAvailable(true);
				terminal.firePortfolioAvailableEvent(portfolio);
			}
			portfolio.resetChanges();
		}
	}
	
	/**
	 * Обновить атрибут портфеля.
	 * <p>
	 * @param portfolio портфель атрибут которого обновляется
	 * @param entry кэш-запись значения атрибута (торговый счет игнорируется)
	 * @throws ValueException исключение сеттера 
	 */
	public void update(EditablePortfolio portfolio, PortfolioValueEntry entry)
		throws ValueException
	{
		String key = entry.getCurrency() + SEP + entry.getKey();
		S<EditablePortfolio> setter = portfolioSetterMap.get(key);
		if ( setter != null ) {
			setter.set(portfolio, entry.getDouble());
		}
	}
	
	/**
	 * Проверить доступность портфеля по его состоянию.
	 * <p>
	 * Выполняет проверку достаточности данных, для признания портфеля
	 * доступным.
	 * <p>
	 * @param portfolio проверяемый портфель
	 * @return true - портфель доступен, false - недоступен
	 */
	public boolean isAvailable(Portfolio portfolio) {
		return portfolio.getCash() != null
			&& portfolio.getBalance() != null;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AssemblerLowLvl.class ) {
			return false;
		}
		AssemblerLowLvl o = (AssemblerLowLvl) other;
		return o.terminal == terminal;
	}
	
	/**
	 * Обновить атрибуты инструмента.
	 * <p>
	 * @param security экземпляр инструмента
	 * @param entry кэш-запись деталей контракта
	 */
	public void update(EditableSecurity security, ContractEntry entry) {
		security.setDisplayName(entry.getDisplayName());
		security.setLotSize(1);
		security.setMinStepPrice(entry.getMinStepPrice());
		security.setMinStepSize(entry.getMinStepSize());
		security.setPrecision(entry.getPrecision());
	}
	
	/**
	 * Генерировать события инструмента.
	 * <p>
	 * В зависимости от состояния генерирует соответствующие события
	 * инструмента.
	 * <p>
	 * @param security экземпляр инструмента
	 * @throws EditableObjectException
	 */
	public void fireEvents(EditableSecurity security)
		throws EditableObjectException
	{

		if ( security.hasChanged() ) {
			if ( security.isAvailable() ) {
				security.fireChangedEvent();
			} else {
				security.setAvailable(true);
				terminal.fireSecurityAvailableEvent(security);
			}
			security.resetChanges();
		}
	}
	
	/**
	 * Начать получение и обработку котировок по инструменту.
	 * <p>
	 * @param security инструмент
	 * @param entry кэш-запись деталей контракта
	 */
	public void startMktData(EditableSecurity security, ContractEntry entry) {
		IBClient client = getTerminal().getClient();
		int reqId = client.nextReqId();
		ContractHandler handler = new IBRequestMarketDataHandler(
			getTerminal(),  security, reqId, entry);
		client.setContractHandler(reqId, handler);
		handler.connectionOpened();
	}
	
	/**
	 * Обновить атрибуты позиции.
	 * <p>
	 * @param position экземпляр позиции
	 * @param entry кэш-запись данных позиции
	 */
	public void update(EditablePosition position, PositionEntry entry) {
		position.setBookValue(entry.getBookValue());
		position.setCurrQty(entry.getQty());
		position.setMarketValue(entry.getMarketValue());
		position.setVarMargin(entry.getVarMargin());
	}
	
	/**
	 * Генерировать события позиции.
	 * <p>
	 * В зависимости от состояния, генерирует соответствующие события позиции.
	 * <p>
	 * @param position позиция
	 * @throws EditableObjectException 
	 */
	public void fireEvents(EditablePosition position)
		throws EditableObjectException
	{
		if ( position.hasChanged() ) {
			if ( position.isAvailable() ) {
				position.fireChangedEvent();
			} else {
				position.setAvailable(true);
				((EditablePortfolio) position.getPortfolio())
					.firePositionAvailableEvent(position);
			}
			position.resetChanges();
		}
	}
	
	/**
	 * Генерировать события стоп-заявки.
	 * <p>
	 * Событие доступности стоп-заявки генерируется всегда, если заявка на
	 * момент вызова недоступна. События изменения генерируются только в случае
	 * наличия изменений. При этом признак изменений сбрасывается.
	 * <p>
	 * @param order экземпляр стоп-заявки
	 * @throws EditableObjectException
	 */
	public void fireStopOrderEvents(EditableOrder order)
		throws EditableObjectException
	{
		if ( ! order.isAvailable() ) {
			order.setAvailable(true);
			terminal.fireStopOrderAvailableEvent(order);
		}
		if ( order.hasChanged() ) {
			order.fireChangedEvent();
			order.resetChanges();
		}
	}
	
	/**
	 * Первичное заполнение атрибутов стоп-заявки.
	 * <p>
	 * Устанавливает атрибуты стоп-заявки на основании кэш-записи.
	 * <p>
	 * @param order экземпляр стоп-заявки
	 * @param entry кэш-запись заявки
	 */
	public void updateStopOrder(EditableOrder order, OrderEntry entry) {
		order.setAccount(entry.getAccount());
		order.setDirection(entry.getDirection());
		order.setPrice(entry.getPrice());
		order.setQty(entry.getQty());
		order.setSecurityDescriptor(getCache()
				.getContract(entry.getContractId()).getSecurityDescriptor());
		order.setStopLimitPrice(entry.getStopLimitPrice());
		order.setType(entry.getType());
		order.setTime(terminal.getCurrentTime());
	}
	
	/**
	 * Согласовать статус стоп-заявки.
	 * <p>
	 * Запрашивает в кэше данных информацию о статусе заявки и выполняет
	 * согласование. Стоп-заявки считается исполненной при соответствующем
	 * статусе и наличии в кэше информации о заявке, которая ссылается на данную
	 * заявку. 
	 * <p>
	 * @param order экземпляр стоп-заявки
	 */
	public void adjustStopOrderStatus(EditableOrder order) {
		Long id = order.getId();
		OrderStatusEntry entry = getCache().getOrderStatus(id);
		if ( entry == null ) {
			return;
		}
		OrderStatus status = entry.getStatus();
		if ( status == null ) {
			return;
		}
		if ( status == OrderStatus.FILLED ) {
			order.setLastChangeTime(terminal.getCurrentTime());
			order.setStatus(OrderStatus.FILLED);
			logger.warn("TODO: find and assign linked order ID");
		} else if ( status == OrderStatus.CANCELLED ) {
			order.setLastChangeTime(terminal.getCurrentTime());
			order.setStatus(OrderStatus.CANCELLED);
		}
	}
	
	/**
	 * Генерировать события заявки.
	 * <p>
	 * Событие доступности стоп-заявки генерируется всегда, если заявка на
	 * момент вызова недоступна. События изменения генерируются только в случае
	 * наличия изменений. При этом признак изменений сбрасывается.
	 * <p>
	 * @param order экземпляр заявки
	 * @throws EditableObjectException
	 */
	public void fireOrderEvents(EditableOrder order)
		throws EditableObjectException
	{
		if ( ! order.isAvailable() ) {
			order.setAvailable(true);
			terminal.fireOrderAvailableEvent(order);
		}
		if ( order.hasChanged() ) {
			order.fireChangedEvent();
			order.resetChanges();
		}
	}
	
	/**
	 * Первичное заполнение атрибутов заявки.
	 * <p>
	 * @param order экземпляр заявки
	 * @param entry кэш-запись заявки
	 */
	public void updateOrder(EditableOrder order, OrderEntry entry) {
		order.setAccount(entry.getAccount());
		order.setDirection(entry.getDirection());
		if ( entry.getType() != OrderType.MARKET ) {
			order.setPrice(entry.getPrice());
		}
		order.setQty(entry.getQty());
		order.setQtyRest(entry.getQty());
		order.setSecurityDescriptor(getCache()
				.getContract(entry.getContractId()).getSecurityDescriptor());
		order.setType(entry.getType());
		order.setTime(terminal.getCurrentTime());
	}
	
	/**
	 * Согласовать сделки заявки.
	 * <p>
	 * @param order экземпляр заявки
	 */
	public void adjustOrderTrades(EditableOrder order) {
		logger.warn("TODO: adjust order by trades");
	}
	
	/**
	 * Согласовать статус заявки.
	 * <p>
	 * @param order экземпляр заявки
	 */
	public void adjustOrderStatus(EditableOrder order) {
		Long id = order.getId();
		OrderStatusEntry entry = getCache().getOrderStatus(id);
		if ( entry == null ) {
			return;
		}
		OrderStatus status = entry.getStatus();
		if ( status == null ) {
			return;
		} else {
			adjustExecuted(order, entry);
			order.setStatus(status);
			if ( status == OrderStatus.FILLED
					|| status == OrderStatus.CANCELLED )
			{
				order.setLastChangeTime(terminal.getCurrentTime());
			}
		}
	}
	
	/**
	 * Скорректировать параметры исполненной части заявки.
	 * <p>
	 * Служебный метод.
	 * <p>
	 * @param order экземпляр заявки
	 * @param entry кэш-запись статуса заявки
	 */
	private void adjustExecuted(EditableOrder order, OrderStatusEntry entry) {
		order.setQtyRest(entry.getQtyRest());
		order.setAvgExecutedPrice(entry.getAvgExecutedPrice());
		order.setExecutedVolume(entry.getAvgExecutedPrice()
				* (double) (order.getQty() - entry.getQtyRest()));
	}
	
	/**
	 * Проверить доступность инструмента заявки.
	 * <p>
	 * @param entry кэш-запись заявки
	 * @return true - соответствующий инструмент доступен, false - недоступен
	 */
	public boolean isSecurityExists(OrderEntry entry) {
		ContractEntry conEntry = getCache().getContract(entry.getContractId());
		return conEntry != null
			&& terminal.isSecurityExists(conEntry.getSecurityDescriptor());
	}

}
