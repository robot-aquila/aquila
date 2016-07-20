package ru.prolib.aquila.finam.tools.web;

import java.io.Closeable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DataExportForm implements Closeable {
	
	public static class SelectorOption {
		private final int id;
		private final int index;
		private final String text;
		
		public SelectorOption(int id, int index, String text) {
			this.id = id;
			this.index = index;
			this.text = text;
		}
		
		public int getID() {
			return id;
		}
		
		public int getIndex() {
			return index;
		}
		
		public String getText() {
			return text;
		}
		
	}
	
	private final WebDriver driver;
	private boolean initialRequestIsMade = false;
		
	public DataExportForm(WebDriver driver) {
		super();
		this.driver = driver;
	}
	
	public DataExportForm initialRequest() {
		if ( initialRequestIsMade == false ) {
			driver.get("http://www.finam.ru/profile/moex-akcii/gazprom/export/");
			initialRequestIsMade = true;
		}
		return this;
	}
	
	public WebElement getSubmitButton() {
		return driver.findElement(By.xpath(getSubmitButtonXPath()));
	}
	
	public String getSubmitButtonXPath() {
		return "//div[@id=\"issuer-profile-export-button\"]/button";
	}
	
	public DataExportForm selectDateFrom(LocalDate date) {
		initialRequest();
		setDate("issuer-profile-export-from", date);
		return this;
	}
	
	public DataExportForm selectDateTo(LocalDate date) {
		initialRequest();
		setDate("issuer-profile-export-to", date);
		return this;
	}
	
	public DataExportForm selectDate(LocalDate date) {
		return selectDateFrom(date)
				.selectDateTo(date);
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
	 */
	private DataExportForm openQuoteSelector() {
		initialRequest();
		driver.findElement(By.id("issuer-profile-header"))
			.findElement(By.className("finam-ui-quote-selector-quote"))
			.findElement(By.className("finam-ui-quote-selector-arrow"))
			.click();
		return this;
	}
	
	/**
	 * Получить текущие опции списка выбора инструмента.
	 * <p>
	 * Инициирует раскрытие списка и выборку всех доступных инструментов.
	 * После получения данных инициирует выбор первого элемента списка.
	 * <p>
	 * @return список опций селектора
	 * @throws DataExportFormException
	 */
	public List<SelectorOption> getQuoteOptions() throws DataExportFormException {
		openQuoteSelector();
		List<WebElement> elements = getLinks(getQuoteSelectorOwner());
		if ( elements.size() == 0 ) {
			throw new DataExportFormException("No quote options were found");
		}
		List<SelectorOption> list = linksToOptions(elements);
		elements.get(0).click();
		return list;
	}
	
	public DataExportForm selectQuote(String quoteName) {
		openQuoteSelector();
		try {
			selectLink(getQuoteSelectorOwner(), quoteName);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set quote type: " + quoteName, e);
		}
		return this;
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
	 */
	private DataExportForm openMarketSelector() {
		initialRequest();
		driver.findElement(By.id("issuer-profile-header"))
			.findElement(By.className("finam-ui-quote-selector-market"))
			.findElement(By.className("finam-ui-quote-selector-arrow"))
			.click();
		return this;
	}

	/**
	 * Получить опции списка выбора группы инструментов.
	 * <p>
	 * Инициирует раскрытие списка и выборку всех доступных групп данных.
	 * После получения данных инициирует выбор первого элемента списка.
	 * <p>
	 * @return список опций селектора
	 * @throws DataExportFormException
	 */
	public List<SelectorOption> getMarketOptions() throws DataExportFormException {
		openMarketSelector();
		List<WebElement> elements = getLinks(getMarketSelectorOwner());
		if ( elements.size() == 0 ) {
			throw new DataExportFormException("No market options were found");
		}
		List<SelectorOption> list = linksToOptions(elements);
		elements.get(0).click();
		return list;
	}
	
	public DataExportForm selectMarket(String marketName) {
		openMarketSelector();
		try {
			selectLink(getMarketSelectorOwner(), marketName);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set market type: " + marketName, e);
		}
		return this;
	}
	
	private DataExportForm selectPeriod(String periodName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-export-first-row"))
			.findElement(By.className("finam-ui-controls-select"))
			.findElement(By.className("finam-ui-controls-select-arrow"))
			.click();
		try {
			selectLink(getPeriodSelectorOwner(), periodName);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set period: " + periodName, e);
		}
		return this;
	}
	
	public DataExportForm selectPeriod_Ticks() {
		return selectPeriod("тики");
	}

	private DataExportForm selectFileFormat(String formatName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-export-fileformat-row"))
			.findElement(By.className("finam-ui-controls-select"))
			.findElement(By.className("finam-ui-controls-select-arrow"))
			.click();
		try {
			selectLink(getFileFormatSelectorOwner(), formatName);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set file format: " + formatName, e);
		}
		return this;
	}

	/**
	 * Select output file format.
	 * <p>
	 * Select "DATE, TIME, LAST, VOL, ID" file format.
	 * This preset will work only for tick data interval which should be
	 * currently selected on the page.
	 * <p>
	 * @return this object
	 */
	public DataExportForm selectFileFormat_TimePriceVolId() {
		return selectFileFormat("DATE, TIME, LAST, VOL, ID");
	}
	
	/**
	 * Select output file format.
	 * <p>
	 * Select "DATE, TIME, LAST, VOL" file format.
	 * This preset will work only for tick data interval which should be
	 * currently selected on the page.
	 * <p>
	 * @return this object
	 */
	public DataExportForm selectFileFormat_TimePriceVol() {
		return selectFileFormat("DATE, TIME, LAST, VOL");
	}
	
	private DataExportForm selectFileExt(String extName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-export-second-row"))
			.findElement(By.className("finam-ui-controls-select"))
			.findElement(By.className("finam-ui-controls-select-arrow"))
			.click();
		try {
			selectLink(getFileExtSelectorOwner(), extName);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set file extension: " + extName, e);
		}
		return this;
	}
	
	public DataExportForm selectFileExt_Csv() {
		return selectFileExt(".csv");
	}
	
	public DataExportForm selectFileExt_Txt() {
		return selectFileExt(".txt");
	}
	
	private DataExportForm selectDateFormat(String formatName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-export-date-row"))
			.findElement(By.className("finam-ui-controls-select"))
			.findElement(By.className("finam-ui-controls-select-arrow"))
			.click();
		try {
			selectLink(getDateFormatSelectorOwner(), formatName);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set date format: " + formatName, e);
		}
		return this;
	}
	
	public DataExportForm selectDateFormat_YYYYMMDD() {
		return selectDateFormat("ггггммдд");
	}
	
	public DataExportForm selectDateFormat_DDMMYY() {
		return selectDateFormat("ддммгг");
	}
	
	private DataExportForm selectTimeFormat(String fmtName) {
		initialRequest();
		WebElement rowElement = driver.findElement(By.id("issuer-profile-export-date-row"));
		List<WebElement> elements = rowElement.findElements(By.className("finam-ui-controls-select"));
		if ( elements.size() < 2 ) {
			throw new NoSuchElementException("Cannot set time format (selector was not located): " + fmtName);
		}
		elements.get(1).findElement(By.className("finam-ui-controls-select-arrow")).click();
		try {
			selectLink(getTimeFormatSelectorOwner(), fmtName);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set time format: " + fmtName, e);
		}
		return this;
	}
	
	public DataExportForm selectTimeFormat_HHMMSS() {
		return selectTimeFormat("ччммсс");
	}
	
	public DataExportForm selectTimeFormat_HHcolMM() {
		return selectTimeFormat("чч:мм");
	}
	
	private DataExportForm selectFieldSeparator(String sepName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-export-separator-row"))
			.findElement(By.className("finam-ui-controls-select"))
			.findElement(By.className("finam-ui-controls-select-arrow"))
			.click();
		try {
			selectLink(getFieldSeparatorSelectorOwner(), sepName);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set field separator: " + sepName, e);
		}
		return this;
	}
	
	public DataExportForm selectFieldSeparator_Comma() {
		return selectFieldSeparator("запятая (,)");
	}
	
	public DataExportForm selectFieldSeparator_Dot() {
		return selectFieldSeparator("точка (.)");
	}
	
	private DataExportForm selectDigitSeparator(String sepName) {
		initialRequest();
		WebElement rowElement = driver.findElement(By.id("issuer-profile-export-separator-row"));
		List<WebElement> elements = rowElement.findElements(By.className("finam-ui-controls-select"));
		if ( elements.size() < 2 ) {
			throw new NoSuchElementException("Cannot set digit separator (selector was not located): " + sepName);
		}
		elements.get(1).findElement(By.className("finam-ui-controls-select-arrow")).click();
		try {
			selectLink(getDigitSeparatorSelectorOwner(), sepName);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set digit separator: " + sepName, e);
		}
		return this;

	}
	
	public DataExportForm selectDigitSeparator_None() {
		return selectDigitSeparator("нет");
	}
	
	public DataExportForm selectDigitSeparator_Dot() {
		return selectDigitSeparator("точка (.)");
	}
	
	public DataExportForm selectDigitSeparator_Comma() {
		return selectDigitSeparator("запятая (,)");
	}
	
	private DataExportForm fillTextbox(WebElement element, String newText) {
		initialRequest();
		String oldText = element.getAttribute("value");
		element.sendKeys(StringUtils.repeat('\b', oldText.length()));
		element.sendKeys(newText);
		return this;
	}
	
	public DataExportForm selectFileName(String fileName) {
		return fillTextbox(driver.findElement(By.id("issuer-profile-export-file-name")), fileName);
	}
	
	/**
	 * Select contract name.
	 * <p>
	 * Note: the filename will be reset by the form after contract name change.
	 * Set the contract name before a filename to keep filename unchanged.
	 * Note2: there is an unknown case when the filename still reset by the form.
	 * <p>
	 * @param name - contact name
	 * @return this
	 */
	public DataExportForm selectContractName(String name) {
		return fillTextbox(driver.findElement(By.id("issuer-profile-export-contract")), name);
	}
	
	private void setCheckboxChecked(WebElement checkBox, boolean enabled) {
		if ( enabled ) {
			if ( ! checkBox.isSelected() ) {
				checkBox.click();
			}
		} else {
			if ( checkBox.isSelected() ) {
				checkBox.click();
			}
		}
	}
	
	private DataExportForm selectCandleTime(boolean start) {
		initialRequest();
		setCheckboxChecked(driver.findElement(By.id("MSOR0")), start);
		setCheckboxChecked(driver.findElement(By.id("MSOR1")), ! start);
		return this;
	}
	
	public DataExportForm selectCandleTime_StartOfCandle() {
		return selectCandleTime(true);
	}
	
	public DataExportForm selectCandleTime_EndOfCandle() {
		return selectCandleTime(false);
	}
	
	public DataExportForm selectMoscowTime(boolean useMoscowTime) {
		initialRequest();
		setCheckboxChecked(driver.findElement(By.id("issuer-profile-export-mstime")), useMoscowTime);
		return this;
	}
	
	public DataExportForm selectAddHeader(boolean add) {
		initialRequest();
		setCheckboxChecked(driver.findElement(By.id("at")), add);
		return this;
	}
	
	public DataExportForm selectFillEmptyPeriods(boolean fill) {
		initialRequest();
		setCheckboxChecked(driver.findElement(By.id("fsp")), fill);
		return this;
	}

	private void setDate(String inputId, LocalDate date) {
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.elementToBeClickable(By.id(inputId))).click();
		
		int year = date.getYear();
		int month = date.getMonth().getValue() - 1; // Finam bugfix
		int dayOfMonth = date.getDayOfMonth();
		WebElement dummy = driver.findElement(By.className("ui-datepicker-year"));
		try {
			selectOption(dummy, year);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set year of " + inputId, e);
		}
		dummy = driver.findElement(By.className("ui-datepicker-month"));
		try {
			selectOption(dummy, month);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set month of " + inputId, e);
		}
		dummy = driver.findElement(By.xpath("//table[@class='ui-datepicker-calendar']/tbody"));
		try {
			selectLink(dummy, dayOfMonth);
		} catch ( DataExportFormException e ) {
			throw new NoSuchElementException("Cannot set day of month of " + inputId, e);
		}
	}
	
	private void selectLink(WebElement owner, int expectedValue) throws DataExportFormException {
		selectLink(owner, Integer.toString(expectedValue));
	}
	
	private void selectLink(WebElement owner, String expectedValue) throws DataExportFormException {
		for  ( WebElement e : getLinks(owner) ) {
			if ( expectedValue.equals(e.getText().trim()) ) {
				e.click();
				return;
			}
		}
		throw new DataExportFormException("Couldn't find an appropriate hyperlink: " + expectedValue);
	}
	
	private void selectOption(WebElement owner, int expectedValue) throws DataExportFormException {
		selectOption(owner, Integer.toString(expectedValue));
	}
	
	private void selectOption(WebElement owner, String expectedValue) throws DataExportFormException {
		List<WebElement> elements = owner.findElements(By.tagName("option"));
		for  ( WebElement e : elements ) {
			String actualValue = e.getAttribute("value");
			if ( expectedValue.equals(actualValue) ) {
				e.click();
				return;
			}
		}
		throw new DataExportFormException("Couldn't find an appropriate option: " + expectedValue);
	}
	
	private WebElement getFinamDropDown(int index) {
		return driver.findElements(By.className("finam-ui-dropdown-list"))
				.get(index);
	}
	
	private WebElement getMarketSelectorOwner() {
		return getFinamDropDown(0);
	}
	
	private WebElement getQuoteSelectorOwner() {
		return getFinamDropDown(1);
	}
	
	private WebElement getPeriodSelectorOwner() {
		return getFinamDropDown(2);
	}
	
	private WebElement getFileExtSelectorOwner() {
		return getFinamDropDown(3);
	}
	
	private WebElement getDateFormatSelectorOwner() {
		return getFinamDropDown(4);
	}
	
	private WebElement getTimeFormatSelectorOwner() {
		return getFinamDropDown(5);
	}

	private WebElement getFieldSeparatorSelectorOwner() {
		return getFinamDropDown(6);
	}
	
	private WebElement getDigitSeparatorSelectorOwner() {
		return getFinamDropDown(7);
	}

	private WebElement getFileFormatSelectorOwner() {
		return getFinamDropDown(8);
	}
	
	/**
	 * Select all links owned by the specified element.
	 * <p>
	 * @param owner - owner element
	 * @return list of links
	 */
	private List<WebElement> getLinks(WebElement owner) {
		return owner.findElements(By.tagName("a"));
	}
	
	private List<SelectorOption> linksToOptions(List<WebElement> elements) {
		List<SelectorOption> list = new ArrayList<>();
		for ( WebElement e : elements ) {
			list.add(new SelectorOption(Integer.valueOf(e.getAttribute("value")),
					Integer.valueOf(e.getAttribute("index")),
					e.getText().trim()));
		}
		return list;
	}

	public void close() {
		driver.close();
	}

}
