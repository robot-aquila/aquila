package ru.prolib.aquila.quik.subsys.row;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.row.Spec;
import ru.prolib.aquila.core.data.row.RowException;
import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * Дополнительный обработчик рядов таблицы портфелей по деривативам.
 * <p>
 * Выполняет регистрацию счетов, формируемых в процессе экспорта таблицы
 * портфелей по деривативам. В этой таблице всегда есть строка, для каждого из
 * открытых счетов. Это позволяет сохранить информацию о счетах, необходимую
 * для экспорта таблиц заявок и стоп-заявок, до того, как начнут поступать
 * данные о заявках.
 * <p>
 * Реализован в виде валидатора ряда, который всегда возвращает true как
 * результат валидации. Это позволяет избежать реализации дополнительных
 * декораторов ряда и специфического конструктора ряда и использовать вместо
 * этого стандартный фильтр рядов. 
 * <p>
 * 2013-02-20<br>
 * $Id$
 */
public class ValidatePortfolioRow implements Validator {
	private final QUIKServiceLocator locator;
	
	public ValidatePortfolioRow(QUIKServiceLocator locator) {
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
	public boolean validate(Object object) throws ValidatorException {
		try {
			RowSet rs = (RowSet) object;
			Account account = (Account) rs.get(Spec.PORT_ACCOUNT);
			if ( account != null ) {
				locator.getAccounts().register(account);
			}
			return true;
		} catch ( RowException e ) {
			throw new ValidatorException(e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == ValidatePortfolioRow.class ) {
			ValidatePortfolioRow o = (ValidatePortfolioRow) other;
			return new EqualsBuilder()
				.append(locator, o.locator)
				.isEquals();
		} else {
			return false;
		}
	}

}
