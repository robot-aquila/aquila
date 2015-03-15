package ru.prolib.aquila.quik.assembler.cache;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.assembler.cache.dde.TradesGateway;

/**
 * Кэш-запись области таблицы всех сделок.
 * <p>
 * Данный класс используется для представления набора рядов, полученных в
 * процессе экспорта таблицы всех сделок по DDE.
 */
public class TradesEntry extends CacheEntry {
	private final TradesGateway gateway;
	private final RowSet rs;
	private final int count;
	private int position = -1, accessCount = 0;
	
	public TradesEntry(TradesGateway gateway, RowSet rs, int count) {
		super();
		this.gateway = gateway;
		this.rs = rs;
		this.count = count;
	}
	
	TradesGateway getGateway() {
		return gateway;
	}
	
	RowSet getRowSet() {
		return rs;
	}
	
	/**
	 * Переместиться на следующий ряд.
	 * <p>
	 * Выполняет перемещение курсора набора на следующий ряд, позволяя таким
	 * образом перебрать все сделки, представленные набором. Сбрасывает
	 * счетчик обращений.
	 * <p>
	 * @return true - доступна очередная сделка из набора, false - конец данных
	 * @throws RowSetException ошибка позицирования набора
	 */
	public synchronized boolean next() throws RowSetException {
		accessCount = 0;
		if ( rs.next() ) {
			position ++;
			return true;
		} else {
			position = -1;
			return false;
		}
	}
	
	/**
	 * Получить количество сделок в наборе.
	 * <p>
	 * Данный метод введен исключительно в информационных целях. Позволяет
	 * выполнять сравнительный анализ объема кэша сделок.
	 * <p>
	 * @return количество сделок (рядов), представленных набором
	 */
	public int count() {
		return count;
	}
	
	/**
	 * Получить позицию курсора.
	 * <p>
	 * Данный метод введен исключительно в информационных целях. Позволяет
	 * определить порядковый номер сделки в наборе, на которую в данный момент
	 * настроен доступ. 
	 * <p>
	 * @return позиция курсора в наборе рядов
	 */
	public synchronized int position() {
		return position;
	}
	
	/**
	 * Сформировать сделку под курсором.
	 * <p>
	 * Данный метод позволяет на основании данных ряда под текущей позицией
	 * курсора сформировать сделку, соответствующую ряду. Метод увеличивает
	 * счетчик обращений к ряду.
	 * <p>
	 * @param terminal терминал-владелец сделки
	 * @return сделка или null, если не удалось сформировать сделку
	 * @throws RowException ошибка доступа к данным ряда
	 */
	public synchronized Trade access(QUIKTerminal terminal)
		throws RowException
	{
		Trade trade = gateway.makeTrade(terminal, rs);
		accessCount ++;
		return trade;
	}
	
	/**
	 * Получить количество обращений.
	 * <p>
	 * Данный счетчик позволяет использовать количество обращений для
	 * контроля валидности передаваемых данных. Каждая неудачная попытка
	 * создать сделку под курсором будет увеличивать этот счетчик. Большое
	 * значение этого счетчика может свидетельствовать о том, что система
	 * не получает необходимые данные (по инструментам) из-за некорректной
	 * настройки экспорта в торговом терминале. В итоге, накопление данных по
	 * сделкам будет происходить бесконечно, что может рассматриваться как
	 * критическая ситуация. Данный счетчик сбрасывается в ноль при
	 * позицировании на следующую сделку.
	 * <p>
	 * @return количество обращений к сделке под курсором
	 */
	public synchronized int accessCount() {
		return accessCount;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TradesEntry.class ) {
			return false;
		}
		synchronized ( other ) {
			TradesEntry o = (TradesEntry) other;
			return new EqualsBuilder()
				.append(o.gateway, gateway)
				.append(o.count, count)
				.append(o.rs, rs)
				.isEquals();
		}
	}

}
