package ru.prolib.aquila.quik.subsys.row;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.row.Spec;
import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * Дополнительный обработчик рядов таблицы позиций по бумагам.
 * <p>
 * Выполняет регистрацию счетов, формируемых в процессе экспорта таблицы позиций
 * по бумагам. Как показывает практика, в этой таблице всегда есть строка,
 * для каждого из открытых счетов. Это позволяет сохранить информацию о счетах,
 * необходимую для экспорта таблиц заявок и стоп-заявок, до того, как начнут
 * поступать данные о заявках.
 * <p>
 * Реализован в виде валидатора ряда, который всегда возвращает true как
 * результат валидации. Это позволяет избежать реализации дополнительных
 * декораторов ряда и специфического конструктора ряда и использовать вместо
 * этого стандартный фильтр рядов. 
 * <p>
 * 2013-02-20<br>
 * $Id$
 */
public class ValidatePositionRow implements Validator {
	private final QUIKServiceLocator locator;
	
	public ValidatePositionRow(QUIKServiceLocator locator) {
		super();
		this.locator = locator;
	}
	
	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return сервис-локатор
	 */
	public QUIKServiceLocator getServiceLocator() {
		return locator;
	}

	@Override
	public boolean validate(Object object) {
		RowSet rs = (RowSet) object;
		Account account = (Account) rs.get(Spec.POS_ACCOUNT);
		if ( account != null ) {
			locator.getAccounts().register(account);
		}
		return true;
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == ValidatePositionRow.class ) {
			ValidatePositionRow o = (ValidatePositionRow) other;
			return new EqualsBuilder()
				.append(locator, o.locator)
				.isEquals();
		} else {
			return false;
		}
	}

}
