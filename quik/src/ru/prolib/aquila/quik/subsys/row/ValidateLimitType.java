package ru.prolib.aquila.quik.subsys.row;

import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;

/**
 * Валидатор ряда таблицы портфелей по деривативам. 
 * <p>
 * В таблице портфелей по деривативам нужно отфильтровать строки, в которых
 * тип лимита не соответствует строке "Ден.средства". Данный валидатор
 * используется совместно с фильтром {@link
 * ru.prolib.aquila.core.data.row.RowSetFilter RowSetFilter}.
 * <p>
 * 2013-02-18<br>
 * $Id$
 */
public class ValidateLimitType implements Validator {
	
	public ValidateLimitType() {
		super();
	}

	@Override
	public boolean validate(Object object) throws ValidatorException {
		try {
			return RowAdapters.PORTF_TYPE_MONEY
				.equals(((Row) object).get(RowAdapters.PORTF_TYPE));
		} catch ( RowException e ) {
			throw new ValidatorException(e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == ValidateLimitType.class;
	}

}
