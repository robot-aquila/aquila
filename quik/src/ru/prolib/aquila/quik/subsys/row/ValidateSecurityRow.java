package ru.prolib.aquila.quik.subsys.row;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.row.Spec;
import ru.prolib.aquila.core.data.row.RowException;
import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * Дополнительный обработчик рядов таблицы инструментов.
 * <p>
 * Выполняет регистрацию дескрипторов и соответствующих им кратких наименований,
 * для последующего использования в таблицах, в которых достаточная информация
 * для формирования дескриптора инструмента отсутствует. 
 * <p>
 * Реализован в виде валидатора ряда, который всегда возвращает true как
 * результат валидации. Это позволяет избежать реализации дополнительных
 * декораторов ряда и специфического конструктора ряда и использовать вместо
 * этого стандартный фильтр рядов. 
 * <p>
 * 2013-02-20<br>
 * $Id: ValidateSecurityRow.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class ValidateSecurityRow implements Validator {
	private final QUIKServiceLocator locator;
	
	public ValidateSecurityRow(QUIKServiceLocator locator) {
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
			locator.getPartiallyKnownObjects().registerSecurityDescriptor(
					(SecurityDescriptor) rs.get(Spec.SEC_DESCR),
					(String) rs.get(Spec.SEC_SHORTNAME)); 
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
		if ( other != null && other.getClass() == ValidateSecurityRow.class ) {
			ValidateSecurityRow o = (ValidateSecurityRow) other;
			return new EqualsBuilder()
				.append(locator, o.locator)
				.isEquals();
		} else {
			return false;
		}
	}

}
