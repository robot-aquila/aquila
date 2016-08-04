package ru.prolib.aquila.web.utils.finam;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ru.prolib.aquila.web.utils.DataExportException;
import ru.prolib.aquila.web.utils.ErrorClass;

public class DataExportForm {
	private static final String FINAM_UI_DROPDOWN_LIST = "finam-ui-dropdown-list";
	private final DataExportFormUtils formUtils = new DataExportFormUtils();
	private final WebDriver driver;
	private boolean initialRequestIsMade = false;

	public DataExportForm(WebDriver driver) {
		super();
		this.driver = driver;
	}
	
	public DataExportForm initialRequest() throws DataExportException {
		try {
			if ( initialRequestIsMade == false ) {
				driver.get("http://www.finam.ru/profile/moex-akcii/gazprom/export/");
				initialRequestIsMade = true;
			}
			return this;
		} catch ( WebDriverException e ) {
			throw errWebDriver("Initial request failed", e);
		}
	}
	
	public WebElement getSubmitButton() throws DataExportException {
		try {
			return driver.findElement(By.xpath(getSubmitButtonXPath()));
		} catch ( WebDriverException e ) {
			throw errWebDriver("Error finding submit button", e);
		}
	}
	
	public String getSubmitButtonXPath() {
		return "//div[@id=\"issuer-profile-export-button\"]/button";
	}
	
	/**
	 * Get list of options of the market selector.
	 * <p>
	 * Initiates the market selector opening, then goes through all options and
	 * clicks on the first available option.
	 * <p>
	 * @return the market options
	 * @throws DataExportException - an error occurred
	 */
	public List<NameValuePair> getMarketOptions() throws DataExportException {
		openMarketSelector();
		return getMarketSelectorSearch()
			.transformAllAndClick(By.tagName("a"), new FinamLinkToNameValueTransformer(), 0);
	}
	
	/**
	 * Get list of options of the quote selector.
	 * <p>
	 * Initiates the quote selector opening, then goes through the all options
	 * and clicks on the first available options. Note that the result depends
	 * on the currently selected market. Be sure that you have chosen the right
	 * market.
	 * <p>
	 * @return the quote options
	 * @throws DataExportException - an error occurred
	 */
	public List<NameValuePair> getQuoteOptions() throws DataExportException {
		openQuoteSelector();
		return getQuoteSelectorSearch()
			.transformAllAndClick(By.tagName("a"), new FinamLinkToNameValueTransformer(), 0);
	}
	
	/**
	 * Select quote by its name.
	 * <p>
	 * @param quoteName - the name of quote
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectQuote(String quoteName) throws DataExportException {
		openQuoteSelector();
		getQuoteSelectorSearch()
			.findWithText(By.tagName("a"), quoteName)
			.click();
		return this;
	}
	
	/**
	 * Select quote by its FINAM id.
	 * <p>
	 * @param quoteId - the quote id
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectQuote(int quoteId) throws DataExportException {
		openQuoteSelector();
		getQuoteSelectorSearch()
			.findWithAttributeValue(By.tagName("a"), "value", formUtils.toString(quoteId))
			.click();
		return this;
	}
	
	/**
	 * Select market by its name.
	 * <p>
	 * @param marketName - the name of market
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectMarket(String marketName) throws DataExportException {
		openMarketSelector();
		getMarketSelectorSearch()
			.findWithText(By.tagName("a"), marketName)
			.click();
		return this;
	}
	
	/**
	 * Select market by its FINAM id.
	 * <p>
	 * @param marketId - the market id
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectMarket(int marketId) throws DataExportException {
		openMarketSelector();
		getMarketSelectorSearch()
			.findWithAttributeValue(By.tagName("a"), "value", formUtils.toString(marketId))
			.click();
		return this;
	}
	
	/**
	 * Select data period from.
	 * <p>
	 * @param date - the date from
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectDateFrom(LocalDate date) throws DataExportException {
		initialRequest();
		setDate("issuer-profile-export-from", date);
		return this;
	}
	
	/**
	 * Select data period to.
	 * <p>
	 * @param date - the date to
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectDateTo(LocalDate date) throws DataExportException {
		initialRequest();
		setDate("issuer-profile-export-to", date);
		return this;
	}
	
	/**
	 * Select same date as data period from and to.
	 * <p>
	 * @param date - the date
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectDate(LocalDate date) throws DataExportException {
		return selectDateFrom(date)
				.selectDateTo(date);
	}

	/**
	 * Select data period.
	 * <p>
	 * @param period - the period
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectPeriod(Period period) throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-export-first-row"))
			.find(By.className("finam-ui-controls-select"))
			.find(By.className("finam-ui-controls-select-arrow"))
			.click();
		getPeriodSelectorSearch()
			.findWithAttributeValue(By.tagName("a"), "value", formUtils.toString(period))
			.click();
		return this;
	}

	/**
	 * Select output filename.
	 * <p>
	 * @param fileName - the filename
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectFilename(String fileName) throws DataExportException {
		return fillTextbox(By.id("issuer-profile-export-file-name"), fileName);
	}

	/**
	 * Select an output file extension.
	 * <p>
	 * @param format - format
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectFileExt(FileExt format) throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-export-second-row"))
			.find(By.className("finam-ui-controls-select"))
			.find(By.className("finam-ui-controls-select-arrow"))
			.click();
		getFileExtSelectorSearch()
			.findWithAttributeValue(By.tagName("a"), "value", formUtils.toString(format))
			.click();
		return this;
	}

	/**
	 * Select the contract name.
	 * <p>
	 * Note: the filename will be reset by the form after contract name change.
	 * Set the contract name before a filename to keep filename unchanged.
	 * Note2: there is an unknown case when the filename still reset by the form.
	 * <p>
	 * @param name - contact name
	 * @return this
	 * @throws DataExportException 
	 */
	public DataExportForm selectContractName(String name) throws DataExportException {
		return fillTextbox(By.id("issuer-profile-export-contract"), name);
	}

	/**
	 * Select a date format.
	 * <p>
	 * @param format - format
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectDateFormat(DateFormat format) throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-export-date-row"))
			.find(By.className("finam-ui-controls-select"))
			.find(By.className("finam-ui-controls-select-arrow"))
			.click();
		getDateFormatSelectorSearch()
			.findWithAttributeValue(By.tagName("a"), "value", formUtils.toString(format))
			.click();
		return this;
	}
	
	/**
	 * Select a time format.
	 * <p>
	 * @param format - format
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectTimeFormat(TimeFormat format) throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-export-date-row"))
			.find(By.className("finam-ui-controls-select"), 1)
			.find(By.className("finam-ui-controls-select-arrow"))
			.click();
		getTimeFormatSelectorSearch()
			.findWithAttributeValue(By.tagName("a"), "value", formUtils.toString(format))
			.click();
		return this;
	}

	/**
	 * Use candle start time option.
	 * <p>
	 * @param use - if true then select start of candle time, otherwise - end of candle
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm useCandleStartTime(boolean use) throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("MSOR0"))
			.setChecked(use);
		new SearchWebElement(driver)
			.find(By.id("MSOR1"))
			.setChecked(!use);
		return this;
	}

	/**
	 * Use Moscow time option.
	 * <p>
	 * @param use - if true then use that option, otherwise - not use
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm useMoscowTime(boolean use) throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-export-mstime"))
			.setChecked(use);
		return this;
	}
	
	/**
	 * Select a field separator.
	 * <p>
	 * @param format - format
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectFieldSeparator(FieldSeparator format) throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-export-separator-row"))
			.find(By.className("finam-ui-controls-select"))
			.find(By.className("finam-ui-controls-select-arrow"))
			.click();
		getFieldSeparatorSelectorSearch()
			.findWithAttributeValue(By.tagName("a"), "value", formUtils.toString(format))
			.click();
		return this;
	}

	/**
	 * Select a digit separator.
	 * <p>
	 * @param format - format
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectDigitSeparator(DigitSeparator format) throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-export-separator-row"))
			.find(By.className("finam-ui-controls-select"), 1)
			.find(By.className("finam-ui-controls-select-arrow"))
			.click();
		getDigitSeparatorSelectorSearch()
			.findWithAttributeValue(By.tagName("a"), "value", formUtils.toString(format))
			.click();
		return this;
	}
	
	/**
	 * Select the data format.
	 * <p>
	 * The available data formats depends on selected period.
	 * Be sure that the right period is selected.
	 * <p>
	 * @param format - data format
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm selectDataFormat(DataFormat format) throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-export-fileformat-row"))
			.find(By.className("finam-ui-controls-select"))
			.find(By.className("finam-ui-controls-select-arrow"))
			.click();
		getFileFormatSelectorSearch()
			.findWithAttributeValue(By.tagName("a"), "value", formUtils.toString(format))
			.click();
		return this;
	}

	/**
	 * Use add header option.
	 * <p>
	 * @param use - if true then use that option, otherwise - not use
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm useAddHeader(boolean use) throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("at"))
			.setChecked(use);
		return this;
	}

	/**
	 * Use fill empty periods option.
	 * <p>
	 * @param use - if true then use that option, otherwise - not use
	 * @return this
	 * @throws DataExportException - an error occurred
	 */
	public DataExportForm useFillEmptyPeriods(boolean use) throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("fsp"))
			.setChecked(use);
		return this;
	}
	
	/**
	 * Get the form action URI.
	 * <p>
	 * @return the form action URI
	 * @throws DataExportException - an error occurred
	 */
	public URI getFormActionURI() throws DataExportException {
		initialRequest();
		String action = new SearchWebElement(driver)
			.find(By.id("chartform"))
			.get()
			.getAttribute("action");
		if ( action == null ) {
			throw errForm("Form action not found");
		}
		try {
			return new URI(action);
		} catch ( URISyntaxException e ) {
			throw errForm("Malformed form action", e);
		}
	}
	
	protected SearchWebElement getMarketSelectorSearch() throws DataExportException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 0);
	}
		
	protected SearchWebElement getQuoteSelectorSearch() throws DataExportException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 1);
	}
	
	protected SearchWebElement getPeriodSelectorSearch() throws DataExportException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 2);
	}
	
	protected SearchWebElement getFileExtSelectorSearch() throws DataExportException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 3);
	}
	
	protected SearchWebElement getDateFormatSelectorSearch() throws DataExportException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 4);
	}
	
	protected SearchWebElement getTimeFormatSelectorSearch() throws DataExportException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 5);
	}

	protected SearchWebElement getFieldSeparatorSelectorSearch() throws DataExportException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 6);
	}
	
	protected SearchWebElement getDigitSeparatorSelectorSearch() throws DataExportException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 7);
	}

	protected SearchWebElement getFileFormatSelectorSearch() throws DataExportException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 8);
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
	
	/**
	 * Раскрыть список выбора групп инструментов.
	 * <p>
	 * Данный список, как и список выбора инструмента, формируется динамически.
	 * Для того, что бы получить доступ к элементам списка, необходимо
	 * инициировать его раскрытие. Данный метод выполняет раскрытие списка для
	 * выбора группы инструментов.
	 * <p>
	 * @return this
	 * @throws DataExportException
	 */
	protected DataExportForm openMarketSelector() throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-header"))
			.find(By.className("finam-ui-quote-selector-market"))
			.find(By.className("finam-ui-quote-selector-arrow"))
			.get()
			.click();
		return this;
	}
	
	/**
	 * Раскрыть список выбора инструмента.
	 * <p>
	 * Данный список, как и список групп инструментов (секции, архив, etc...),
	 * формируется динамически. Для того, что бы получить доступ к элементам
	 * списка, необходимо инициировать его раскрытие. Данный метод выполняет
	 * раскрытие списка для выбора инструмента.
	 * <p>
	 * @return this
	 * @throws DataExportException
	 */
	protected DataExportForm openQuoteSelector() throws DataExportException {
		initialRequest();
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-header"))
			.find(By.className("finam-ui-quote-selector-quote"))
			.find(By.className("finam-ui-quote-selector-arrow"))
			.click();
		return this;
	}
	
	protected DataExportForm fillTextbox(By by, String newText) throws DataExportException {
		initialRequest();
		WebElement element = new SearchWebElement(driver)
			.find(by)
			.get();
		String oldText = element.getAttribute("value");
		if ( oldText != null ) {
			element.sendKeys(StringUtils.repeat('\b', oldText.length()));
			element.sendKeys(newText);
		} else {
			throw errForm("Element attribute [value] not found: " + by);
		}
		return this;
	}

	protected void setDate(String inputId, LocalDate date) throws DataExportException {
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.elementToBeClickable(By.id(inputId))).click();
		
		int year = date.getYear();
		int month = date.getMonth().getValue() - 1; // FINAM bugfix
		int dayOfMonth = date.getDayOfMonth();
		try {
			new SearchWebElement(driver)
				.find(By.className("ui-datepicker-year"))
				.findWithAttributeValue(By.tagName("option"), "value", formUtils.toString(year))
				.click();
		} catch ( DataExportException e ) {
			throw errForm("Cannot set year of " + inputId, e);
		}
		try {
			new SearchWebElement(driver)
				.find(By.className("ui-datepicker-month"))
				.findWithAttributeValue(By.tagName("option"), "value", formUtils.toString(month))
				.click();
		} catch ( DataExportException e ) {
			throw errForm("Cannot set month of " + inputId, e);
		}
		try {
			new SearchWebElement(driver)
				.find(By.xpath("//table[@class='ui-datepicker-calendar']/tbody"))
				.findWithText(By.tagName("a"), formUtils.toString(dayOfMonth))
				.click();
		} catch ( DataExportException e ) {
			throw errForm("Cannot set day of month of " + inputId, e);
		}
	}

}
