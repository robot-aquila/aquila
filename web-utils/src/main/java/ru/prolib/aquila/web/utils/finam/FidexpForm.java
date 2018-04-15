package ru.prolib.aquila.web.utils.finam;

import java.time.LocalDate;
import java.util.List;

import org.apache.http.NameValuePair;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.web.utils.WUIOException;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.SearchWebElement;

/**
 * Wrapper of a web form of market data export provided by FINAM.
 * <p>
 * This class provides methods to interact with form of data export service using WebDriver.
 * This is a set of small operations which do not control any complex conditions related to
 * current WebDriver state (like if there is an properly opened page, etc). The class
 * contains operations to open form of export service, chose form options using universal
 * value representation (which is independent of specific data representation provided by
 * data export service) and run form processing by clicking the button.
 * <p>
 * Keep in mind this class is a non thread-safe and uses WebDriver state between calls.
 * This means that you cannot use shared instance of WebDriver for different purposes or in
 * separate threads because it may cause WebDriver state change between calls.
 */
public class FidexpForm {
	private static final long BASE_WAIT_TIMEOUT = 20L;
	private static final String FINAM_UI_DROPDOWN_LIST = "finam-ui-dropdown-list";
	@SuppressWarnings("unused")
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(FidexpForm.class);
	}
	
	private final FidexpFormUtils formUtils = new FidexpFormUtils();
	private final WebDriver driver;

	public FidexpForm(WebDriver driver) {
		super();
		this.driver = driver;
	}

	/**
	 * Open export form page.
	 * <p>
	 * This method should be called prior to all others
	 * at least once to get the form to initial state.
	 * <p>
	 * @return this
	 * @throws WUIOException - unable to open form. This error is not related to any page analysis
	 * or manipulation. The error is related to transport or protocol level.
	 */
	public FidexpForm open() throws WUIOException {
		try {
			driver.get("http://www.finam.ru/profile/moex-akcii/gazprom/export/");
			return this;
		} catch ( WebDriverException e ) {
			throw new WUIOException("Request failed: ", e);
		}
	}
	
	/**
	 * Send form to a server.
	 * <p>
	 * @return this
	 * @throws WUWebPageException
	 */
	public FidexpForm send() throws WUWebPageException {
		checkDriverIsReady();
		getSubmitButton().click();
		return this;
	}
	
	public FidexpForm ensurePageLoaded() throws WUWebPageException {
		try {
			new WebDriverWait(driver, BASE_WAIT_TIMEOUT)
				.until(dummy -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
		} catch ( TimeoutException e ) {
			throw new WUWebPageException("Page still not loaded");
		}
		return this;
	}

	/**
	 * Get list of options of the market selector.
	 * <p>
	 * Initiates the market selector opening, then goes through all options and
	 * clicks on the first available option.
	 * <p>
	 * @return the market options
	 * @throws WUWebPageException - an error occurred
	 */
	public List<NameValuePair> getMarketOptions() throws WUWebPageException {
		checkDriverIsReady();
		openMarketSelector();
		return getMarketSelectorSearch()
			.transformAllAndClick(By.tagName("a"), new FidexpLinkToNameValueTransformer(), 0);
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
	 * @throws WUWebPageException - an error occurred
	 */
	public List<NameValuePair> getQuoteOptions() throws WUWebPageException {
		checkDriverIsReady();
		openQuoteSelector();
		return getQuoteSelectorSearch()
			.transformAllAndClick(By.tagName("a"), new FidexpLinkToNameValueTransformer(), 0);
	}
	
	/**
	 * Select quote by its name.
	 * <p>
	 * @param quoteName - the name of quote
	 * @return this
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectQuote(String quoteName) throws WUWebPageException {
		checkDriverIsReady();
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
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectQuote(int quoteId) throws WUWebPageException {
		checkDriverIsReady();
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
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectMarket(String marketName) throws WUWebPageException {
		checkDriverIsReady();
		openMarketSelector();
		finishMarketSelection(
			getMarketSelectorSearch()
				.findWithText(By.tagName("a"), marketName)
		);
		return this;
	}
	
	/**
	 * Select market by its FINAM id.
	 * <p>
	 * @param marketId - the market id
	 * @return this
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectMarket(int marketId) throws WUWebPageException {
		checkDriverIsReady();
		openMarketSelector();
		finishMarketSelection(
			getMarketSelectorSearch()
				.findWithAttributeValue(By.tagName("a"), "value", formUtils.toString(marketId))
		);
		return this;
	}
	
	private void finishMarketSelection(SearchWebElement clickToMe) throws WUWebPageException {
		if ( "a".equals(clickToMe.get().getTagName()) == false ) {
			throw new IllegalArgumentException("Expected tagName=a");
		}
		// После клика не всегда успевает подгрузить список инструментов.
		// Элемент селектора инструментов сам по себе не меняется и его
		// проверка ничего не дает. Но внутри него есть див, который
		// содержит список UL, элементы которого содержат ссылки. Проверяем
		// его на устаревание.
		//
		// Это работает, но иногда все равно выскакивает таймаут. В связи с этим
		// добавим проверку второго элемента списка на изменение содержимого.
		// Хотя эта проверка будет работать не для всех рынков. В некоторых
		// рынках нет ни одного инструмента. Да и вероятность совпадения не
		// нулевая.
		//
		// Здесь есть special case, когда рынок уже выбран. И если продолжать
		// работу с учетом всех нижеследующих проверок, то гарантированно
		// будет фейл. Так как в этом случае никаких перезагрузок страницы
		// не будет.
		if ( clickToMe.get().getText().equals(new SearchWebElement(driver)
			.find(By.id("issuer-profile-header"))
			.find(By.className("finam-ui-quote-selector-market"))
			.find(By.className("finam-ui-quote-selector-title"))
			.get()
			.getText()))
		{
			return;
		}

		WebElement shouldBeStale = getQuoteSelectorSearch().find(By.tagName("ul")).get();
		String shouldBeChanged = null;
		try {
			shouldBeChanged = new SearchWebElement(driver, shouldBeStale)
				.find(By.tagName("a"), 1)
				.get()
				.getText();
		} catch ( WUWebPageException e ) {
			// Это нормально. Вероятно это рынок, у которого список инструментов пуст. 
		}
		
		//logger.debug("FixexpForm FMS: click on market selector");
		clickToMe.click();
		//logger.debug("FixexpForm FMS: wait for indicator is in stale state");
		
		try {
			waitCond(ExpectedConditions.stalenessOf(shouldBeStale), 2);
		} catch ( WUWebPageException e ) {
			if ( shouldBeChanged != null ) {
				String x = null;
				try {
					x = getQuoteSelectorSearch()
						.find(By.tagName("ul"))
						.find(By.tagName("a"), 1)
						.get()
						.getText();
				} catch ( WUWebPageException z ) {
					// Вероятно, список все-таки обновился или устарел.
					// Если он таки обновился, мы могли не найти значимый
					// элемент потому, что новый список не содержит инструментов.
					// Это уже не наша проблема, так что считаем что все ОК.
				}
				if ( x != null && x.equals(shouldBeChanged) ) {
					// Значение элемента не изменилось. Значит
					// список действительно не был обновлен.
					throw new WUWebPageException("Quote list still not changed", e);
				}
			}
		}
		//logger.debug("FixexpForm FMS: done");
	}
	
	/**
	 * Select data period from.
	 * <p>
	 * @param date - the date from
	 * @return this
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectDateFrom(LocalDate date) throws WUWebPageException {
		checkDriverIsReady();
		setDate("issuer-profile-export-from", date);
		return this;
	}
	
	/**
	 * Select data period to.
	 * <p>
	 * @param date - the date to
	 * @return this
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectDateTo(LocalDate date) throws WUWebPageException {
		checkDriverIsReady();
		setDate("issuer-profile-export-to", date);
		return this;
	}
	
	/**
	 * Select same date as data period from and to.
	 * <p>
	 * @param date - the date
	 * @return this
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectDate(LocalDate date) throws WUWebPageException {
		checkDriverIsReady();
		return selectDateFrom(date)
				.selectDateTo(date);
	}

	/**
	 * Select data period.
	 * <p>
	 * @param period - the period
	 * @return this
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectPeriod(FidexpPeriod period) throws WUWebPageException {
		checkDriverIsReady();
		new SearchWebElement(driver)
			.find(By.xpath("//*[@id=\"issuer-profile-export-first-row\"]/td[3]/div"))
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
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectFilename(String fileName) throws WUWebPageException {
		checkDriverIsReady();
		return fillTextbox(By.id("issuer-profile-export-file-name"), fileName);
	}

	/**
	 * Select an output file extension.
	 * <p>
	 * @param format - format
	 * @return this
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectFileExt(FidexpFileExt format) throws WUWebPageException {
		checkDriverIsReady();
		new SearchWebElement(driver)
			.find(By.xpath("//*[@id=\"issuer-profile-export-second-row\"]/td[3]/div"))
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
	 * @throws WUWebPageException 
	 */
	public FidexpForm selectContractName(String name) throws WUWebPageException {
		checkDriverIsReady();
		return fillTextbox(By.id("issuer-profile-export-contract"), name);
	}

	/**
	 * Select a date format.
	 * <p>
	 * @param format - format
	 * @return this
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectDateFormat(FidexpDateFormat format) throws WUWebPageException {
		checkDriverIsReady();
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
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectTimeFormat(FidexpTimeFormat format) throws WUWebPageException {
		checkDriverIsReady();
		new SearchWebElement(driver)
			.find(By.xpath("//*[@id=\"issuer-profile-export-date-row\"]/td[5]/div"))
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
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm useCandleStartTime(boolean use) throws WUWebPageException {
		checkDriverIsReady();
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
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm useMoscowTime(boolean use) throws WUWebPageException {
		checkDriverIsReady();
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
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectFieldSeparator(FidexpFieldSeparator format) throws WUWebPageException {
		checkDriverIsReady();
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
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectDigitSeparator(FidexpDigitSeparator format) throws WUWebPageException {
		checkDriverIsReady();
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
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm selectDataFormat(FidexpDataFormat format) throws WUWebPageException {
		checkDriverIsReady();
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
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm useAddHeader(boolean use) throws WUWebPageException {
		checkDriverIsReady();
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
	 * @throws WUWebPageException - an error occurred
	 */
	public FidexpForm useFillEmptyPeriods(boolean use) throws WUWebPageException {
		checkDriverIsReady();
		new SearchWebElement(driver)
			.find(By.id("fsp"))
			.setChecked(use);
		return this;
	}
	
	private SearchWebElement getMarketSelectorSearch() throws WUWebPageException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 0);
	}
		
	private SearchWebElement getQuoteSelectorSearch() throws WUWebPageException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 1);
	}
	
	private SearchWebElement getPeriodSelectorSearch() throws WUWebPageException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 2);
	}
	
	private SearchWebElement getFileExtSelectorSearch() throws WUWebPageException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 3);
	}
	
	private SearchWebElement getDateFormatSelectorSearch() throws WUWebPageException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 4);
	}
	
	private SearchWebElement getTimeFormatSelectorSearch() throws WUWebPageException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 5);
	}

	private SearchWebElement getFieldSeparatorSelectorSearch() throws WUWebPageException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 6);
	}
	
	private SearchWebElement getDigitSeparatorSelectorSearch() throws WUWebPageException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 7);
	}

	private SearchWebElement getFileFormatSelectorSearch() throws WUWebPageException {
		return new SearchWebElement(driver)
			.find(By.className(FINAM_UI_DROPDOWN_LIST), 8);
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
	 * @throws WUWebPageException - an error occurred
	 */
	private FidexpForm openMarketSelector() throws WUWebPageException {
		By locMarketSelector = By.className("finam-ui-quote-selector-market");
		waitElem(ExpectedConditions.presenceOfElementLocated(locMarketSelector));
		waitElem(ExpectedConditions.visibilityOfElementLocated(locMarketSelector));
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-header"))
			.find(locMarketSelector)
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
	 * @throws WUWebPageException - an error occurred
	 */
	private FidexpForm openQuoteSelector() throws WUWebPageException {
		By locQuoteSelector = By.className("finam-ui-quote-selector-quote");
		waitElem(ExpectedConditions.presenceOfElementLocated(locQuoteSelector));
		waitElem(ExpectedConditions.visibilityOfElementLocated(locQuoteSelector));
		new SearchWebElement(driver)
			.find(By.id("issuer-profile-header"))
			.find(locQuoteSelector)
			.find(By.className("finam-ui-quote-selector-arrow"))
			.click();
		return this;
	}
	
	private FidexpForm fillTextbox(By by, String newText) throws WUWebPageException {
		WebElement element = new SearchWebElement(driver)
			.find(by)
			.get();
		String oldText = element.getAttribute("value");
		if ( oldText != null ) {
			element.clear();
			element.sendKeys(newText);
		} else {
			throw new WUWebPageException("Element attribute [value] not found: " + by);
		}
		return this;
	}
	
	private void setDate(String inputId, LocalDate date) throws WUWebPageException {
		waitElem(ExpectedConditions.elementToBeClickable(By.id(inputId))).click();
		
		int year = date.getYear();
		int month = date.getMonth().getValue() - 1; // FINAM bugfix
		int dayOfMonth = date.getDayOfMonth();
		try {
			new SearchWebElement(driver)
				.find(By.className("ui-datepicker-year"))
				.findWithAttributeValue(By.tagName("option"), "value", formUtils.toString(year))
				.click();
		} catch ( WUWebPageException e ) {
			throw new WUWebPageException("Cannot set year of " + inputId, e);
		}
		try {
			new SearchWebElement(driver)
				.find(By.className("ui-datepicker-month"))
				.findWithAttributeValue(By.tagName("option"), "value", formUtils.toString(month))
				.click();
		} catch ( WUWebPageException e ) {
			throw new WUWebPageException("Cannot set month of " + inputId, e);
		}
		try {
			new SearchWebElement(driver)
				.find(By.xpath("//table[@class='ui-datepicker-calendar']/tbody"))
				.findWithText(By.tagName("a"), formUtils.toString(dayOfMonth))
				.click();
		} catch ( WUWebPageException e ) {
			throw new WUWebPageException("Cannot set day of month of " + inputId, e);
		}
	}

	private WebElement getSubmitButton() throws WUWebPageException {
		try {
			return driver.findElement(By.xpath(getSubmitButtonXPath()));
		} catch ( WebDriverException e ) {
			throw new WUWebPageException("Error finding submit button", e);
		}
	}
	
	private String getSubmitButtonXPath() {
		return "//div[@id=\"issuer-profile-export-button\"]/button";
	}

	private WebElement waitElem(ExpectedCondition<WebElement> condition) throws WUWebPageException {
		try {
			return newWait().until(condition);
		} catch ( TimeoutException e ) {
			throw new WUWebPageException("Timeout exception: ", e);
		}
	}
	
	private boolean waitCond(ExpectedCondition<Boolean> condition, int multiplier) throws WUWebPageException {
		try {
			return newWait(multiplier).until(condition);
		} catch ( TimeoutException e ) {
			throw new WUWebPageException("Timeout exception: ", e);
		}
	}
	
	private WebDriverWait newWait() {
		return newWait(1);
	}
	
	private WebDriverWait newWait(int multiplier) {
		if ( multiplier <= 0 ) {
			throw new IllegalArgumentException("Expected multiplier > 0");
		}
		return new WebDriverWait(driver, BASE_WAIT_TIMEOUT * multiplier);
	}
	
	private void checkDriverIsReady() throws WUWebPageException {
		String url = driver.getCurrentUrl();
		if ( url == null || ! url.startsWith("http") ) {
			throw new WUWebPageException("Cannot proceed. Possible the form page is not loaded. Current URL: " + url);
		}
	}

}
