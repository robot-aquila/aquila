package ru.prolib.aquila.ib.utils;

import com.ib.client.Contract;

/**
 * $Id$
 */
public class IBHistoricalRequestParams {

	private Contract contract;
	private String endDateTime;
	private String duration;
	private String barSize;
	private String whatToShow;
	private String useRTH;
	
	public IBHistoricalRequestParams(Contract contract, String date, String duration, 
			String barSize, String whatToShow, String useRTH)
	{
		this.contract = contract;
		this.endDateTime = date;
		this.duration = duration;
		this.barSize = barSize;
		this.whatToShow = whatToShow;
		this.useRTH = useRTH;
	}
	
	public Integer getUseRTH() {
		return Integer.valueOf(useRTH);
	}
	
	public String getWhatToShow() {
		return whatToShow;
	}
	
	public String getBarSize() {
		return barSize;
	}
	
	public long getDuration() {
		return Long.valueOf(duration);
	}
	
	public String getEndDateTime() {
		return endDateTime;
	}
	
	public Contract getContract() {
		return contract;
	}
}
