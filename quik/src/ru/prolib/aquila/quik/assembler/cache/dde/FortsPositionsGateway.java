package ru.prolib.aquila.quik.assembler.cache.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.quik.assembler.Assembler;
import ru.prolib.aquila.quik.assembler.cache.PositionEntry;

/**
 * Шлюз к кэшу таблицы позиций по деривативам.
 */
public class FortsPositionsGateway implements TableGateway {
	private static final String ACCOUNT_CODE = "TRDACCID";
	private static final String FIRM_ID = "FIRMID";
	private static final String SEC_SHORT_NAME = "SEC_SHORT_NAME";
	private static final String OPEN = "START_NET";
	private static final String CURR = "TOTAL_NET";
	private static final String VARMARGIN = "VARMARGIN";
	private static final String[] REQUIRED_HEADERS = {
		ACCOUNT_CODE,
		FIRM_ID,
		SEC_SHORT_NAME,
		OPEN,
		CURR,
		VARMARGIN,
	};

	private final RowDataConverter converter;
	private final Assembler asm;
	
	public FortsPositionsGateway(RowDataConverter converter, Assembler asm) {
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
			asm.assemble(new PositionEntry(
				new Account(converter.getString(row, FIRM_ID), code, code),
				converter.getString(row, SEC_SHORT_NAME),
				converter.getLong(row, OPEN),
				converter.getLong(row, CURR),
				converter.getDouble(row, VARMARGIN)));
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}

	@Override
	public boolean shouldProcess(Row row) throws DDEException {
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if (other == null || other.getClass() != FortsPositionsGateway.class) {
			return false;
		}
		FortsPositionsGateway o = (FortsPositionsGateway) other;
		return new EqualsBuilder()
			.append(o.asm, asm)
			.append(o.converter, converter)
			.isEquals();
	}

}
