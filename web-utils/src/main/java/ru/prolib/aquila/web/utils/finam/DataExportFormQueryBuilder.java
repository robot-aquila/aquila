package ru.prolib.aquila.web.utils.finam;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.http.client.utils.URIBuilder;

public class DataExportFormQueryBuilder {
	private static final DateTimeFormatter dateFormat;
	
	static {
		dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	}
	
	private final DataExportFormUtils formUtils;
	
	public DataExportFormQueryBuilder(DataExportFormUtils formUtils) {
		this.formUtils = formUtils;
	}
	
	public DataExportFormQueryBuilder() {
		this(new DataExportFormUtils());
	}
	
	public URI buildQuery(URI initialURI, DataExportParams params) throws URISyntaxException {
		LocalDate dateFrom = params.getDateFrom(), dateTo = params.getDateTo();
		URIBuilder builder = new URIBuilder(initialURI)
			.clearParameters()
			.addParameter("market", formUtils.toString(params.getMarketID()))
			.addParameter("em", formUtils.toString(params.getQuoteID()))
			.addParameter("code", params.getContractName())
			.addParameter("apply", "0")
			.addParameter("df", formUtils.toString(dateFrom.getDayOfMonth()))
			.addParameter("mf", formUtils.toString(dateFrom.getMonthValue() - 1))
			.addParameter("yf", formUtils.toString(dateFrom.getYear()))
			.addParameter("from", dateFormat.format(dateFrom))
			.addParameter("dt", formUtils.toString(dateTo.getDayOfMonth()))
			.addParameter("mt", formUtils.toString(dateTo.getMonthValue() - 1))
			.addParameter("yt", formUtils.toString(dateTo.getYear()))
			.addParameter("to", dateFormat.format(dateTo))
			.addParameter("p", formUtils.toString(params.getPeriod()))
			.addParameter("f", params.getFileName())
			.addParameter("e", formUtils.toString(params.getFileExt()))
			.addParameter("cn", params.getContractName())
			.addParameter("dtf", formUtils.toString(params.getDateFormat()))
			.addParameter("tmf", formUtils.toString(params.getTimeFormat()))
			.addParameter("MSOR", formUtils.toString(params.getCandleTime()));
		if ( params.getUseMoscowTime() ) {
			builder.addParameter("mstime", "on")
				.addParameter("mstimever", "1");
		} else {
			builder.addParameter("mstimever", "0");
		}
		builder.addParameter("sep", formUtils.toString(params.getFieldSeparator()))
			.addParameter("sep2", formUtils.toString(params.getDigitSeparator()))
			.addParameter("datf", formUtils.toString(params.getDataFormat()));
		if ( params.getAddHeader() ) {
			builder.addParameter("at", "1");
		}
		if ( params.getFillEmptyPeriods() ) {
			builder.addParameter("fsp", "1");
		}
		String path = builder.getPath();
		int index = path.lastIndexOf('/');
		if ( index >= 0 ) {
			path = path.substring(0, index + 1);
		} else {
			path = "/";
		}
		path += params.getFileName() + formUtils.toString(params.getFileExt());
		return builder.setPath(path).build();
	}

}
