package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MDUpdateBuilder {
	private MDUpdateType type = MDUpdateType.REFRESH;
	private Symbol symbol;
	private Instant time = Instant.EPOCH;
	private List<MDUpdateRecord> records = new ArrayList<>();

	public MDUpdateBuilder(Symbol symbol) {
		this.symbol = symbol;
	}

	public MDUpdateBuilder() {
		this(null);
	}
	
	public MDUpdateBuilder withType(MDUpdateType type) {
		this.type = type;
		return this;
	}
	
	public MDUpdateBuilder withSymbol(Symbol symbol) {
		this.symbol = symbol;
		return this;
	}
	
	public MDUpdateBuilder withTime(Instant time) {
		this.time = time;
		return this;
	}
	
	public MDUpdateBuilder addAsk(double price, long size) {
		records.add(new MDUpdateRecordImpl(Tick.of(TickType.ASK, time, price, size), MDTransactionType.ADD));
		return this;
	}
	
	public MDUpdateBuilder addBid(double price, long size) {
		records.add(new MDUpdateRecordImpl(Tick.of(TickType.BID, time, price, size), MDTransactionType.ADD));
		return this;
	}
	
	public MDUpdateBuilder add(Tick tick) {
		records.add(new MDUpdateRecordImpl(tick, MDTransactionType.ADD));
		return this;
	}
	
	public MDUpdate buildMDUpdate() {
		if ( symbol == null ) {
			throw new IllegalArgumentException("Symbol must be specified");
		}
		MDUpdateImpl update = new MDUpdateImpl(new MDUpdateHeaderImpl(type, time, symbol));
		for ( MDUpdateRecord r : records ) {
			update.addRecord(r.getTick(), r.getTransactionType());
		}
		return update;
	}
	
}
