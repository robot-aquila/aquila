package ru.prolib.aquila.quik.subsys.row;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.row.Spec;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;

/**
 * Обработчик входящих данных DDE-таблицы заявок.
 */
public class OrderTableHandler implements DDETableHandler {
	public static final Map<String, Integer> EMPTY_MAP;
	private Map<String, Integer> headerIndex;
	private final Map<String, G<?>> elementAdapters;
	private final Validator headerValidator;
	private final DDEUtils ddeUtils;
	private final OrderTable orderTable;
	
	static {
		EMPTY_MAP = new Hashtable<String, Integer>();
	}
	
	public OrderTableHandler(Validator headerValidator,
			Map<String, G<?>> elementAdapters,
			OrderTable orderTable, DDEUtils ddeUtils)
	{
		super();
		headerIndex = EMPTY_MAP;
		this.elementAdapters = elementAdapters;
		this.headerValidator = headerValidator;
		this.orderTable = orderTable;
		this.ddeUtils = ddeUtils;
	}
	
	public OrderTableHandler(Validator headerValidator,
			Map<String, G<?>> elementAdapters,
			OrderTable orderTable)
	{
		this(headerValidator, elementAdapters, orderTable, new DDEUtils());
	}

	
	/**
	 * Получить текущий используемый хэш-индекс заголовков.
	 * <p>
	 * @return хэш-индекс
	 */
	public synchronized Map<String, Integer> getCurrentHeaderIndex() {
		return Collections.unmodifiableMap(headerIndex);
	}
	
	/**
	 * Получить набор адаптеров элемента ряда.
	 * <p>
	 * @return набор адаптеров
	 */
	public Map<String, G<?>> getElementAdapters() {
		return Collections.unmodifiableMap(elementAdapters);
	}
	
	/**
	 * Получить валидатор заголовков.
	 * <p>
	 * @return валидатор
	 */
	public Validator getHeaderValidator() {
		return headerValidator;
	}
	
	/**
	 * Получить утилиты DDE.
	 * <p>
	 * @return экземпляр класса
	 */
	public DDEUtils getDdeUtils() {
		return ddeUtils;
	}
	
	/**
	 * Получить хранилище рядов таблицы заявок.
	 * <p>
	 * @return хранилище рядов
	 */
	public OrderTable getOrderTable() {
		return orderTable;
	}

	@Override
	public synchronized void handle(DDETable ddeTable) throws DDEException {
		try {
			handleTable(ddeTable);
		} catch ( ParseException e ) {
			throw new DDEException(e);
		} catch ( RowSetException e ) {
			throw new DDEException(e);
		}
	}
	
	/**
	 * Обработать данные таблицы.
	 * <p>
	 * @param ddeTable таблица
	 * @throws ParseException некорректное значение item таблицы
	 * @throws RowSetException ошибка позицирования ряда
	 */
	private void handleTable(DDETable ddeTable)
			throws ParseException, RowSetException
	{
		DDETableRange range = ddeUtils.parseXltRange(ddeTable.getItem());
		if ( range.getFirstRow() == 1 ) {
			orderTable.clear();
			headerIndex = ddeUtils.makeHeadersMap(ddeTable);
			if ( ! headerValidator.validate(headerIndex.keySet()) ) {
				headerIndex = EMPTY_MAP;
				return;
			}
		}
		RowSet rs = new RowSetAdapter(new DDETableRowSet(ddeTable, headerIndex,
				range.getFirstRow() == 1 ? 0 : 1), elementAdapters);
		while ( rs.next() ) {
			orderTable.setRow(new OrderTableRow(
					(Long) rs.get(Spec.ORD_ID),
					(Long) rs.get(Spec.ORD_TRANSID),
					(Account) rs.get(Spec.ORD_ACCOUNT),
					(Date) rs.get(Spec.ORD_TIME),
					(OrderDirection) rs.get(Spec.ORD_DIR),
					(SecurityDescriptor) rs.get(Spec.ORD_SECDESCR),
					(Long) rs.get(Spec.ORD_QTY),
					(Double) rs.get(Spec.ORD_PRICE),
					(Long) rs.get(Spec.ORD_QTYREST),
					(OrderStatus) rs.get(Spec.ORD_STATUS),
					(OrderType) rs.get(Spec.ORD_TYPE)));
		}
		orderTable.fireChangedEvent();
	}

}
