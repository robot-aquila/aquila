package ru.prolib.aquila.web.utils.moex;

import java.util.ArrayList;
import java.util.HashMap;
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
			
			String contractFieldString = stripHtml(cols.get(0).getText());
			try {
				int contractField = formUtils.toContractField(contractFieldString);
				Object value = toContractValue(contractField, stripHtml(cols.get(1).getText()));
				tokens.put(contractField, value);
			} catch ( DataExportException e ) {
				throw errForm("Error obtaining contract info: " + contractCode
						+ " (field: " + contractFieldString + ")", e);
			}
		}
		return tokens;
	}
	
	/**
	 * Get list of active futures.
	 * <p>
	 * @return list of symbols
	 * @throws DataExportException - an error occurred
	 */
	public List<String> getActiveFuturesList() throws DataExportException {
		openDerivativesSearchPage();
		List<String> list = new ArrayList<>();
		int currentPage = 1;
		for (;;) {
			list.addAll(scanFuturesTableForSymbols());
			currentPage ++;
			Map<Integer, WebElement> pageLinks = getPageLinks(true);
			if ( ! pageLinks.containsKey(currentPage) ) {
				break;
			} else {
				pageLinks.get(currentPage).click();
			}
		}
		return list;
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
	
	private MoexContractForm openContractPage(String contractCode) throws DataExportException {
		String uri = "http://moex.com/en/contract.aspx?code=" + contractCode;
		if ( uri.equals(webDriver.getCurrentUrl()) ) {
			// We're on the same page
			return this;
		}
		try {
			webDriver.get(uri);
		} catch ( WebDriverException e ) {
			throw errWebDriver("Contract page request failed", e);
		}

		// Additional test for the contract existence.
		if ( ! contractCode.equals(new SearchWebElement(webDriver)
				.find(By.xpath("//*[@id='contract']/div[2]/div[1]/table/tbody/tr/td[1]/b"))
				.get()
				.getText()
				.trim()) )
		{
			throw new DataExportException(ErrorClass.POSSIBLE_LOGIC,
				"Contract not exists or page has changed its structure: " +
				contractCode);
		}
		closeUserAgreement();
		return this;		
	}
	
	private MoexContractForm openDerivativesSearchPage() throws DataExportException {
		String uri = "http://moex.com/en/derivatives/contracts.aspx?p=act";
		if ( uri.equals(webDriver.getCurrentUrl()) ) {
			// We're on the same page
			return this;
		}
		try {
			webDriver.get(uri);
		} catch ( WebDriverException e ) {
			throw errWebDriver("Search page request failed.", e);
		}
		// Additional check that the page is open
		new SearchWebElement(webDriver)
			.findWithText(By.className("h1header"), "Search by contracts");
		closeUserAgreement();
		return this;
	}
	
	private String stripHtml(String html) {
		return StringEscapeUtils.unescapeHtml4(html);
	}
	
	private List<String> scanFuturesTableForSymbols() throws DataExportException {
		List<String> list = new ArrayList<>();
		WebElement table = new SearchWebElement(webDriver)
			.find(By.xpath("//div[@id='root']/table/tbody/tr[1]/td[2]/table/tbody/tr[2]/td/table/tbody/tr/td[2]/table/tbody/tr[2]/td[2]/table[7]/tbody"))
			.get();
		boolean skipHeader = true;
		for ( WebElement row : new SearchWebElement(table).findAll(By.tagName("tr")) ) {
			if ( skipHeader ) {
				skipHeader = false;
			} else {
				try {
					list.add(new SearchWebElement(row)
						.find(By.tagName("td"), 1)
						.find(By.tagName("a"))
						.get()
						.getText()
						.trim());
					
				} catch ( DataExportException e ) {
					System.out.println("Error element: " + row.getText());
					throw e;
				}
			}
		}
		return list;
	}
	
	private WebElement findPaginatorTable(boolean futures) throws DataExportException {
		String marker = futures ? "Futures" : "Options";
		for ( WebElement table : new SearchWebElement(webDriver)
			.findAll(By.tagName("table")) )
		{
			List<WebElement> cells = new SearchWebElement(table).findAll(By.tagName("td"));
			if ( cells.size() == 2 && marker.equals(cells.get(0).getText().trim())
					&& cells.get(1).getText().startsWith("Number of records: ") )
			{
				WebElement pTable = new SearchWebElement(table)
					.find(By.xpath("following-sibling::table[1]"))
					.get();
				// Additional test of the pagination table structure.
				new SearchWebElement(pTable)
					.findWithText(By.tagName("span"), "Pages:");
				return pTable;
			}
		}
		throw new DataExportException(ErrorClass.POSSIBLE_LOGIC, "Paginator table was not found");
	}
	
	private Map<Integer, WebElement> getPageLinks(boolean futures) throws DataExportException {
		List<WebElement> children = new SearchWebElement(findPaginatorTable(futures))
			.find(By.tagName("tr"), 1)
			.find(By.tagName("td"))
			.findAll(By.xpath("*"));
		Map<Integer, WebElement> pageLinks = new HashMap<>();
		for ( int i = 1; i < children.size(); i ++ ) {
			WebElement child = children.get(i);
			//boolean isCurrentPage = "span".equals(child.getTagName()) ? true : false;
			try {
				int pageNumber = Integer.valueOf(child.getText().trim());
				pageLinks.put(pageNumber, child);
			} catch ( NumberFormatException e ) {
				throw errForm("Bad pagination link detected: ", e);
			}
		}
		return pageLinks;
	}
	
	private void closeUserAgreement() {
		try {
			// Check if the Exchange User Agreement window is popped up
			// and click on the I Agree button if so
			SearchWebElement search = new SearchWebElement(webDriver);
			if ( search.find(By.className("ui-dialog-buttonset")).get().isDisplayed() ) {
				search.findWithText(By.tagName("button"), "I Agree").click();
			}
		} catch ( DataExportException e ) { }
	}

}
