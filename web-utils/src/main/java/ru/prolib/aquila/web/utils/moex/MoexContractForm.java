package ru.prolib.aquila.web.utils.moex;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import ru.prolib.aquila.web.utils.DataExportException;
import ru.prolib.aquila.web.utils.ErrorClass;
import ru.prolib.aquila.web.utils.SearchWebElement;

/**
 * Accessing MOEX contract details.
 * <p>
 * @see http://moex.com/en/contract.aspx?code=S where S is a contract name
 */
public class MoexContractForm {
	private final WebDriver webDriver;
	private final MoexContractFormUtils formUtils = new MoexContractFormUtils();
	
	public MoexContractForm(WebDriver webDriver) {
		this.webDriver = webDriver;
	}
	
	public MoexContractForm openContractPage(String contractCode) throws DataExportException {
		String uri = "http://moex.com/en/contract.aspx?code=" + contractCode;
		if ( uri.equals(webDriver.getCurrentUrl()) ) {
			// We're on the same page
			return this;
		}
		try {
			webDriver.get(uri);
		} catch ( WebDriverException e ) {
			throw errWebDriver("Initial request failed", e);
		}		
		try {
			// Check if the Exchange User Agreement window is popped up
			// and click on the I Agree button if so
			SearchWebElement search = new SearchWebElement(webDriver);
			if ( search.find(By.className("ui-dialog-buttonset")).get().isDisplayed() ) {
				search.findWithText(By.tagName("button"), "I Agree").click();
			}
		} catch ( DataExportException e ) { }
		return this;		
	}
	
	/**
	 * Get description of the current contract.
	 * <p>
	 * Note that the set of fields are different for existing and expired contracts!
	 * <p>
	 * @param contractCode - the contract code
	 * @return set of fields
	 * @throws DataExportException - an error occurred
	 */
	public Map<Integer, Object> getInstrumentDescription(String contractCode) throws DataExportException {
		openContractPage(contractCode);
		List<WebElement> rows = new SearchWebElement(webDriver)
			.find(By.className("tool_options_table_forts"))
			.findAll(By.tagName("tr"));
		Map<Integer, Object> tokens = new LinkedHashMap<>();
		for ( int i = 0; i < rows.size(); i ++ ) {
			WebElement row = rows.get(i);
			List<WebElement> cols = new SearchWebElement(row)
				.findAll(By.tagName("td"));
			if ( cols.size() != 2 ) {
				throw errForm("Wrong number of elements of row #" + i + ":" + cols.size());
			}
			
			String contractFieldString = StringEscapeUtils.unescapeHtml4(cols.get(0).getText());
			try {
				int contractField = formUtils.toContractField(contractFieldString);
				Object value = toContractValue(contractField, StringEscapeUtils.unescapeHtml4(cols.get(1).getText()));
				tokens.put(contractField, value);
			} catch ( DataExportException e ) {
				throw errForm("Error obtaining contract info: " + contractCode
						+ " (field: " + contractFieldString + ")", e);
			}
		}
		return tokens;
	}
	
	private Object toContractValue(int contractField, String stringValue) throws DataExportException {
		switch ( contractField ) {
		case MoexContractField.TYPE:
			return formUtils.toContractType(stringValue);
		case MoexContractField.SETTLEMENT:
			return formUtils.toSettlementType(stringValue);
		case MoexContractField.LOT_SIZE:
			return formUtils.toInteger(stringValue);
		case MoexContractField.QUOTATION:
			return formUtils.toQuotationType(stringValue);
		case MoexContractField.FIRST_TRADING_DAY:
		case MoexContractField.LAST_TRADING_DAY:
		case MoexContractField.DELIVERY:
		case MoexContractField.INITIAL_MARGIN_DATE:
			return formUtils.toLocalDate(stringValue);
		case MoexContractField.TICK_SIZE:
		case MoexContractField.TICK_VALUE:
		case MoexContractField.LOWER_PRICE_LIMIT:
		case MoexContractField.UPPER_PRICE_LIMIT:
		case MoexContractField.SETTLEMENT_PRICE:
		case MoexContractField.FEE:
		case MoexContractField.INTRADAY_FEE:
		case MoexContractField.NEGOTIATION_FEE:
		case MoexContractField.EXERCISE_FEE:
		case MoexContractField.INITIAL_MARGIN:
			return formUtils.toDouble(stringValue);
		case MoexContractField.FX_INTRADAY_CLEARING:
		case MoexContractField.FX_EVENING_CLEARING:
			return formUtils.toClearingTime(stringValue);
		case MoexContractField.SETTLEMENT_PROC_DESCR:
		case MoexContractField.SYMBOL:
		case MoexContractField.SYMBOL_CODE:
		case MoexContractField.CONTRACT_DESCR:
		default:
			return stringValue;
		}
	}
	
	protected DataExportException errWebDriver(String msg, Throwable t) {
		return new DataExportException(ErrorClass.WEB_DRIVER, msg, t);
	}
	
	protected DataExportException errWebDriver(Throwable t) {
		return errWebDriver("WebDriver exception", t);
	}
	
	protected DataExportException errForm(String msg, Throwable t) {
		return new DataExportException(ErrorClass.WEB_FORM, msg, t);
	}
	
	protected DataExportException errForm(String msg) {
		return new DataExportException(ErrorClass.WEB_FORM, msg);
	}

}
