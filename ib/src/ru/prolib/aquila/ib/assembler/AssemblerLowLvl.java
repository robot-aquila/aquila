package ru.prolib.aquila.ib.assembler;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.ib.*;
import ru.prolib.aquila.ib.api.ContractHandler;
import ru.prolib.aquila.ib.assembler.cache.*;

/**
 * Низкоуровневые функции сборки.
 */
public class AssemblerLowLvl {
	private static final String SEP = ".";
	private static final String CUR = "BASE";
	private static final String CASH = "TotalCashBalance";
	private static final String BALANCE = "NetLiquidationByCurrency";
	private static final Map<String, S<EditablePortfolio>> portfolioSetterMap;
	private static final Logger logger;
	
	
	static {
		portfolioSetterMap = new Hashtable<String, S<EditablePortfolio>>();
		portfolioSetterMap.put(CUR + SEP + CASH, new PortfolioSetCash());
		portfolioSetterMap.put(CUR + SEP + BALANCE, new PortfolioSetBalance());
		logger = LoggerFactory.getLogger(AssemblerLowLvl.class);
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
	 * Обновить атрибут портфеля.
	 * <p>
	 * @param portfolio портфель атрибут которого обновляется
	 * @param entry кэш-запись значения атрибута (торговый счет игнорируется)
	 */
	public void update(EditablePortfolio portfolio, PortfolioValueEntry entry) {
		String key = entry.getCurrency() + SEP + entry.getKey();
		S<EditablePortfolio> setter = portfolioSetterMap.get(key);
		if ( setter != null ) {
			try {
				setter.set(portfolio, entry.getDouble());
			} catch ( Exception e ) {
				logger.error("Unexpected exception: ", e);
			}
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
	 * Начать получение и обработку котировок по инструменту.
	 * <p>
	 * @param security инструмент
	 * @param entry кэш-запись деталей контракта
	 */
	public void startMktData(EditableSecurity security, ContractEntry entry) {
		IBEditableTerminal terminal = getTerminal();
		int reqId = terminal.getOrderNumerator().incrementAndGet();
		ContractHandler handler =
			new IBRequestMarketDataHandler(terminal, security, reqId, entry);
		terminal.getClient().setContractHandler(reqId, handler);
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

}
