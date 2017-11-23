package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

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
	
	public MDUpdateBuilder withTypeRefresh() {
		return withType(MDUpdateType.REFRESH);
	}
	
	public MDUpdateBuilder withTypeUpdate() {
		return withType(MDUpdateType.UPDATE);
	}
	
	public MDUpdateBuilder withTypeRefreshAsk() {
		return withType(MDUpdateType.REFRESH_ASK);
	}
	
	public MDUpdateBuilder withTypeRefreshBid() {
		return withType(MDUpdateType.REFRESH_BID);
	}
	
	public MDUpdateBuilder withSymbol(Symbol symbol) {
		this.symbol = symbol;
		return this;
	}
	
	public MDUpdateBuilder withTime(Instant time) {
		this.time = time;
		return this;
	}
	
	public MDUpdateBuilder add(Tick tick) {
		records.add(new MDUpdateRecordImpl(tick, MDTransactionType.ADD));
		return this;
	}
	
	public MDUpdateBuilder addAsk(CDecimal price, CDecimal size) {
		return add(Tick.ofAsk(time, price, size));
	}
	
	public MDUpdateBuilder addAsk(String price, long size) {
		return addAsk(CDecimalBD.of(price), CDecimalBD.of(size));
	}
	
	public MDUpdateBuilder addBid(CDecimal price, CDecimal size) {
		return add(Tick.ofBid(time, price, size));
	}
	
	public MDUpdateBuilder addBid(String price, long size) {
		return addBid(CDecimalBD.of(price), CDecimalBD.of(size));
	}
	
	public MDUpdateBuilder replace(Tick tick) {
		records.add(new MDUpdateRecordImpl(tick, MDTransactionType.REPLACE));
		return this;
	}
	
	public MDUpdateBuilder replaceAsk(CDecimal price, CDecimal size) {
		return replace(Tick.ofAsk(time, price, size));
	}

	public MDUpdateBuilder replaceBid(CDecimal price, CDecimal size) {
		return replace(Tick.ofBid(time, price, size));
	}
	
	public MDUpdateBuilder delete(Tick tick) {
		records.add(new MDUpdateRecordImpl(tick, MDTransactionType.DELETE));		
		return this;
	}

	public MDUpdateBuilder deleteAsk(CDecimal price) {
		return delete(Tick.ofAsk(time, price, CDecimalBD.ZERO));
	}

	public MDUpdateBuilder deleteBid(CDecimal price) {
		return delete(Tick.ofBid(time, price, CDecimalBD.ZERO));
	}
	
	public MDUpdateBuilder replaceOrDelete(Tick tick) {
		if ( CDecimalBD.ZERO.compareTo(tick.getSize()) == 0 ) {
			return delete(tick);
		} else {
			return replace(tick);
		}
	}
	
	public MDUpdateBuilder withTime(String timeString) {
		return withTime(Instant.parse(timeString));
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
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MDUpdateBuilder.class ) {
			return false;
		}
		MDUpdateBuilder o = (MDUpdateBuilder) other;
		return new EqualsBuilder()
				.append(o.records, records)
				.append(o.symbol, symbol)
				.append(o.time, time)
				.append(o.type, type)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[symbol=" + symbol + " time=" + time
				+ " type=" + type + " " + records + "]";
	}
	
}
