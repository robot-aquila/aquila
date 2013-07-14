package ru.prolib.aquila.quik.assembler.cache.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.quik.assembler.Assembler;
import ru.prolib.aquila.quik.assembler.cache.PortfolioEntry;

/**
 * Шлюз таблицы портфелей ФОРТС.
 * <p>
 * Так как портфели является независимыми от других данных сущностями,
 * кэшировать строки таблицы портфелей по деривативам смысла нет. Конвертирует
 * входные данные в приемлимую для обработки форму и передает их на обработку
 * сборщику.
 */
public class FortsPortfoliosGateway implements TableGateway {
	private static final String ACCOUNT_CODE = "TRDACCID";
	private static final String FIRM_ID = "FIRMID";
	private static final String CASH = "CBPLPLANNED";
	private static final String BALANCE = "CBPLIMIT";
	private static final String VARMARGIN = "VARMARGIN";
	private static final String TYPE = "LIMIT_TYPE";
	private static final String TYPE_MONEY = "Ден.средства";
	private static final String[] REQUIRED_HEADERS = {
		ACCOUNT_CODE,
		FIRM_ID,
		CASH,
		BALANCE,
		VARMARGIN,
		TYPE,
	};
	
	private final RowDataConverter converter;
	private final Assembler asm;
	
	public FortsPortfoliosGateway(RowDataConverter converter, Assembler asm) {
		super();
		this.converter = converter;
		this.asm = asm;
	}
	
	RowDataConverter getRowDataConverter() {
		return converter;
	}
	
	Assembler getAssembler() {
		return asm;
	}

	@Override
	public String[] getRequiredHeaders() {
		return REQUIRED_HEADERS;
	}

	@Override
	public void process(Row row) throws DDEException {
		try {
			String code = converter.getString(row, ACCOUNT_CODE);
			asm.assemble(new PortfolioEntry(
				new Account(converter.getString(row, FIRM_ID), code, code),
				converter.getDouble(row, BALANCE),
				converter.getDouble(row, CASH),
				converter.getDouble(row, VARMARGIN)));
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}

	@Override
	public boolean shouldProcess(Row row) throws DDEException {
		try {
			return converter.getString(row, TYPE).equals(TYPE_MONEY);
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if (other == null || other.getClass() != FortsPortfoliosGateway.class) {
			return false;
		}
		FortsPortfoliosGateway o = (FortsPortfoliosGateway) other;
		return new EqualsBuilder()
			.append(o.asm, asm)
			.append(o.converter, converter)
			.isEquals();
	}

}
