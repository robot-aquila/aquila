package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class MDUpdateImpl implements MDUpdate {
	private final MDUpdateHeader header;
	private final List<MDUpdateRecord> records;

	public MDUpdateImpl(MDUpdateHeader header, List<MDUpdateRecord> records) {
		super();
		this.header = header;
		this.records = records;
	}
	
	public MDUpdateImpl(MDUpdateHeader header) {
		this(header, new Vector<MDUpdateRecord>());
	}

	@Override
	public MDUpdateHeader getHeader() {
		return header;
	}

	@Override
	public List<MDUpdateRecord> getRecords() {
		return Collections.unmodifiableList(records);
	}

	@Override
	public MDUpdateType getType() {
		return header.getType();
	}

	@Override
	public Instant getTime() {
		return header.getTime();
	}

	@Override
	public Symbol getSymbol() {
		return header.getSymbol();
	}
	
	public MDUpdateRecord addRecord(Tick tick, MDTransactionType transactionType) {
		MDUpdateRecord record = new MDUpdateRecordImpl(tick, transactionType);
		records.add(record);
		return record;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MDUpdateImpl.class ) {
			return false;
		}
		MDUpdateImpl o = (MDUpdateImpl) other;
		return new EqualsBuilder()
			.append(header, o.header)
			.append(records, o.records)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "["
				+ header.getTime() + " "
				+ header.getType() + " "
				+ header.getSymbol()
				+ " " + records + "]";
	}

}
