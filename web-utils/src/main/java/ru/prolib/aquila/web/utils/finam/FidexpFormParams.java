package ru.prolib.aquila.web.utils.finam;

import java.time.LocalDate;

public class FidexpFormParams {
	private int finamMarketId = 14; // Фьючерсы
	private int finamQuoteId = 17455; // Склеенный фьючерс на индекс РТС
	private LocalDate dateFrom = LocalDate.now();
	private LocalDate dateTo = LocalDate.now();
	private FidexpPeriod period = FidexpPeriod.TICKS;
	private String contractName = "RTS";
	private String fileName = "RTS";
	private FidexpFileExt fileExt = FidexpFileExt.CSV;
	private FidexpDateFormat dateFormat = FidexpDateFormat.YYYYMMDD;
	private FidexpTimeFormat timeFormat = FidexpTimeFormat.HHMMSS;
	private FidexpCandleTime candleTime = FidexpCandleTime.START_OF_CANDLE;
	private boolean useMoscowTime = true;
	private FidexpFieldSeparator fieldSeparator = FidexpFieldSeparator.COMMA;
	private FidexpDigitSeparator digitSeparator = FidexpDigitSeparator.NONE;
	private FidexpDataFormat dataFormat = FidexpDataFormat.DATE_TIME_LAST_VOL;
	private boolean addHeader = true;
	private boolean fillEmptyPeriods = false;

	public int getMarketID() {
		return finamMarketId;
	}
	
	public FidexpFormParams setMarketId(int marketId) {
		this.finamMarketId = marketId;
		return this;
	}
	
	public int getQuoteID() {
		return finamQuoteId;
	}
	
	public LocalDate getDateFrom() {
		return dateFrom;
	}
	
	public LocalDate getDateTo() {
		return dateTo;
	}
	
	public FidexpPeriod getPeriod() {
		return period;
	}
	
	public String getContractName() {
		return contractName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public FidexpFileExt getFileExt() {
		return fileExt;
	}
	
	public FidexpDateFormat getDateFormat() {
		return dateFormat;
	}
	
	public FidexpTimeFormat getTimeFormat() {
		return timeFormat;
	}
	
	public FidexpCandleTime getCandleTime() {
		return candleTime;
	}
	
	public boolean getUseMoscowTime() {
		return useMoscowTime;
	}
	
	public FidexpFieldSeparator getFieldSeparator() {
		return fieldSeparator;
	}
	
	public FidexpDigitSeparator getDigitSeparator() {
		return digitSeparator;
	}
	
	public FidexpDataFormat getDataFormat() {
		return dataFormat;
	}
	
	public boolean getAddHeader() {
		return addHeader;
	}
	
	public boolean getFillEmptyPeriods() {
		return fillEmptyPeriods;
	}

	public FidexpFormParams setAddHeader(boolean flag) {
		this.addHeader = flag;
		return this;
	}

	public FidexpFormParams setDateFormat(FidexpDateFormat format) {
		this.dateFormat = format;
		return this;
	}

	public FidexpFormParams setTimeFormat(FidexpTimeFormat format) {
		this.timeFormat = format;
		return this;
	}

	public FidexpFormParams setQuoteID(int id) {
		this.finamQuoteId = id;
		return this;
	}

	public FidexpFormParams setDateFrom(LocalDate date) {
		this.dateFrom = date;
		return this;
	}

	public FidexpFormParams setDataFormat(FidexpDataFormat format) {
		this.dataFormat = format;
		return this;
	}

	public FidexpFormParams setUseMoscowTime(boolean flag) {
		this.useMoscowTime = flag;
		return this;
	}

	public FidexpFormParams setDigitSeparator(FidexpDigitSeparator separator) {
		this.digitSeparator = separator;
		return this;
	}

	public FidexpFormParams setFileName(String name) {
		this.fileName = name;
		return this;
	}

	public FidexpFormParams setFieldSeparator(FidexpFieldSeparator separator) {
		this.fieldSeparator = separator;
		return this;
	}

	public FidexpFormParams setDateTo(LocalDate date) {
		this.dateTo = date;
		return this;
	}

	public FidexpFormParams setContractName(String name) {
		this.contractName = name;
		return this;
	}

	public FidexpFormParams setCandleTime(FidexpCandleTime candleTime) {
		this.candleTime = candleTime;
		return this;
	}

	public FidexpFormParams setPeriod(FidexpPeriod period) {
		this.period = period;
		return this;
	}

	public FidexpFormParams setFileExt(FidexpFileExt ext) {
		this.fileExt = ext;
		return this;
	}

	public FidexpFormParams setFillEmptyPeriods(boolean flag) {
		this.fillEmptyPeriods = flag;
		return this;
	}

}
