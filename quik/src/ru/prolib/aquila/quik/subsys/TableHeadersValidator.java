package ru.prolib.aquila.quik.subsys;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Validator;

/**
 * Валидатор заголовков таблиц.
 * <p>
 * Используется в обозревателях таблиц QUIK а качестве валидатора заголовков
 * таблицы для класса {@link
 * ru.prolib.aquila.dde.utils.table.DDETableRowSetBuilderImpl
 * DDETableRowSetBuilderImpl}. В случае выявления несоответствия заголовков
 * указанному набору помимо возврата соответствующего результата инициирует
 * генерацию события панического состояния терминала.
 * <p>
 * 2013-02-09<br>
 * $Id$
 */
public class TableHeadersValidator implements Validator {
	private static final Logger logger;
	private final FirePanicEvent firePanic;
	private final String tableId;
	private final String[] requiredHeaders;
	
	static {
		logger = LoggerFactory.getLogger(TableHeadersValidator.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param firePanic генератор события
	 * @param tableId идентификатор таблицы
	 * @param requiredHeaders список требуемых полей
	 */
	public TableHeadersValidator(FirePanicEvent firePanic, String tableId,
			String[] requiredHeaders)
	{
		super();
		this.tableId = tableId;
		this.firePanic = firePanic;
		this.requiredHeaders = requiredHeaders;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public FirePanicEvent getFirePanicEvent() {
		return firePanic;
	}
	
	/**
	 * Получить идентификатор таблицы.
	 * <p>
	 * @return идентификатор таблицы
	 */
	public String getTableId() {
		return tableId;
	}
	
	/**
	 * Получить список требуемых полей.
	 * <p>
	 * @return список полей
	 */
	public String[] getRequiredHeaders() {
		return requiredHeaders;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean validate(Object object) {
		Set<String> headers = (Set<String>) object;
		logger.debug("Start validating table headers: {}", tableId);
		boolean isValid = true;
		for ( int i = 0; i < requiredHeaders.length; i ++ ) {
			String required = requiredHeaders[i];
			if ( headers.contains(required) ) {
				debugHeaderInfo(required, true);
			} else {
				debugHeaderInfo(required, false);
				isValid = false;
			}
		}
		logger.debug("Table headers validation result: {} is {}",
				new Object[] { tableId, isValid ? "ok" : "wrong table" });
		if ( ! isValid ) {
			firePanic.firePanicEvent(1, getClass().getSimpleName()
					+ "#validate:{}", new Object[] { tableId });
		}
		return isValid;
	}
	
	/**
	 * Вывести информацию о заголовке в лог.
	 * <p>
	 * @param header заголовок
	 * @param ok результат проверки
	 */
	private void debugHeaderInfo(String header, boolean ok) {
		Object args[] = { tableId, header, ok ? "ok" : "not exists" };
		logger.debug("{}.{}: {}", args);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == TableHeadersValidator.class
			? fieldsEquals(other) : false;
	}
	
	/**
	 * Сравнить значения атрибутов.
	 * <p>
	 * @param other объект для сравнения
	 * @return результат сравнения
	 */
	protected boolean fieldsEquals(Object other) {
		TableHeadersValidator o = (TableHeadersValidator) other;
		return new EqualsBuilder()
			.append(firePanic, o.firePanic)
			.append(requiredHeaders, o.requiredHeaders)
			.append(tableId, o.tableId)
			.isEquals();
			
	}

}
