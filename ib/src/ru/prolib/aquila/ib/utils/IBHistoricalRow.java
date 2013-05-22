package ru.prolib.aquila.ib.utils;

/**
 * $Id$
 */
public class IBHistoricalRow {

	private int id;
	private String date;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private int volume;
	private boolean hasGaps;
	
	public IBHistoricalRow(int id, String date, double open,
			double high, double low, double close, int volume, boolean hasGaps)
	{
		this.id = id;
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.hasGaps = hasGaps;
	}
	
	public String[] toValuesArray() {
		String[] row = {
				date,
				String.valueOf(open),
				String.valueOf(high),
				String.valueOf(low),
				String.valueOf(close),
				String.valueOf(volume)
		};
		return row;
	}
}
