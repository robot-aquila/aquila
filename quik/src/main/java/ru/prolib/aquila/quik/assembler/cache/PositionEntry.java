package ru.prolib.aquila.quik.assembler.cache;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;


/**
 * Кэш строки таблицы позиций.
 * <p>
 * Данный класс используется для временного сохранения данных о позиции.
 * <p>
 * Объект позиции зависит от объектов двух других классов: портфеля и
 * инструмента. Если в отношении портфеля проблема может быть решена в момент
 * обработки данных созданием соответствующего экземпляра портфеля (все
 * необходимые идентификаторы счета доступны при получении позиции), то с
 * инструментом дело обстоит несколько сложнее.
 * <p> 
 * Данные таблицы позиций не содержат полей, позволяющих однозначно
 * идентифицировать инструмент. Поля этой таблицы позволяют связывать строку с
 * инструментом лишь косвенно: по краткому или полному наименованию инструмента.
 * В случае, если данные соответствующего инструмента еще не были загружены, то
 * данные позиции сохраняются в кэше до того момента, пока не будет получена
 * информация по инструменту. В качестве единицы хранения используется экземпляр
 * данного класса.
 */
public class PositionEntry extends CacheEntry {
	private final Account account;
	private final String secShortName;
	private final Long openQty;
	private final Long currentQty;
	private final Double varMargin;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param account торговый счет
	 * @param secShortName краткое наименование инструмента
	 * @param openQty входящее кол-во
	 * @param currentQty текущее кол-во
	 * @param varMargin вариационка
	 */
	public PositionEntry(Account account, String secShortName,
			Long openQty, Long currentQty, Double varMargin)
	{
		super();
		this.account = account;
		this.secShortName = secShortName;
		this.openQty = openQty;
		this.currentQty = currentQty;
		this.varMargin = varMargin;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public String getSecurityShortName() {
		return secShortName;
	}
	
	public Long getOpenQty() {
		return openQty;
	}
	
	public Long getCurrentQty() {
		return currentQty;
	}
	
	public Double getVarMargin() {
		return varMargin;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != PositionEntry.class ) {
			return false;
		}
		PositionEntry o = (PositionEntry) other;
		return new EqualsBuilder()
			.append(account, o.account)
			.append(secShortName, o.secShortName)
			.append(openQty, o.openQty)
			.append(currentQty, o.currentQty)
			.append(varMargin, o.varMargin)
			.isEquals();
	}

}
