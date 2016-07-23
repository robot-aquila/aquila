package ru.prolib.aquila.finam.tools.web;

import java.time.LocalDate;

public class DataExportParams {
	private int finamMarketId = 14; // Фьючерсы
	private int finamQuoteId = 17455; // Склеенный фьючерс на индекс РТС
	private LocalDate dateFrom = LocalDate.now();
	private LocalDate dateTo = LocalDate.now();
	private Period period = Period.TICKS;
	private String contractName = "RTS";
	private String fileName = "RTS";
	private FileExt fileExt = FileExt.CSV;
	private DateFormat dateFormat = DateFormat.YYYYMMDD;
	private TimeFormat timeFormat = TimeFormat.HHMMSS;
	private CandleTime candleTime = CandleTime.START_OF_CANDLE;
	private boolean useMoscowTime = true;
	private FieldSeparator fieldSeparator = FieldSeparator.COMMA;
	private DigitSeparator digitSeparator = DigitSeparator.NONE;
	private DataFormat dataFormat = DataFormat.DATE_TIME_LAST_VOL;
	private boolean addHeader = true;
	private boolean fillEmptyPeriods = false;

	public int getMarketID() {
		return finamMarketId;
	}
	
	public DataExportParams setMarketId(int marketId) {
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
	
	public Period getPeriod() {
		return period;
	}
	
	public String getContractName() {
		return contractName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public FileExt getFileExt() {
		return fileExt;
	}
	
	public DateFormat getDateFormat() {
		return dateFormat;
	}
	
	public TimeFormat getTimeFormat() {
		return timeFormat;
	}
	
	public CandleTime getCandleTime() {
		return candleTime;
	}
	
	public boolean getUseMoscowTime() {
		return useMoscowTime;
	}
	
	public FieldSeparator getFieldSeparator() {
		return fieldSeparator;
	}
	
	public DigitSeparator getDigitSeparator() {
		return digitSeparator;
	}
	
	public DataFormat getDataFormat() {
		return dataFormat;
	}
	
	public boolean getAddHeader() {
		return addHeader;
	}
	
	public boolean getFillEmptyPeriods() {
		return fillEmptyPeriods;
	}

	public DataExportParams setAddHeader(boolean flag) {
		this.addHeader = flag;
		return this;
	}

	public DataExportParams setDateFormat(DateFormat format) {
		this.dateFormat = format;
		return this;
	}

	public DataExportParams setTimeFormat(TimeFormat format) {
		this.timeFormat = format;
		return this;
	}

	public DataExportParams setQuoteID(int id) {
		this.finamQuoteId = id;
		return this;
	}

	public DataExportParams setDateFrom(LocalDate date) {
		this.dateFrom = date;
		return this;
	}

	public DataExportParams setDataFormat(DataFormat format) {
		this.dataFormat = format;
		return this;
	}

	public DataExportParams setUseMoscowTime(boolean flag) {
		this.useMoscowTime = flag;
		return this;
	}

	public DataExportParams setDigitSeparator(DigitSeparator separator) {
		this.digitSeparator = separator;
		return this;
	}

	public DataExportParams setFileName(String name) {
		this.fileName = name;
		return this;
	}

	public DataExportParams setFieldSeparator(FieldSeparator separator) {
		this.fieldSeparator = separator;
		return this;
	}

	public DataExportParams setDateTo(LocalDate date) {
		this.dateTo = date;
		return this;
	}

	public DataExportParams setContractName(String name) {
		this.contractName = name;
		return this;
	}

	public DataExportParams setCandleTime(CandleTime candleTime) {
		this.candleTime = candleTime;
		return this;
	}

	public DataExportParams setPeriod(Period period) {
		this.period = period;
		return this;
	}

	public DataExportParams setFileExt(FileExt ext) {
		this.fileExt = ext;
		return this;
	}

	public DataExportParams setFillEmptyPeriods(boolean flag) {
		this.fillEmptyPeriods = flag;
		return this;
	}

}
