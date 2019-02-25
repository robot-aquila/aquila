package ru.prolib.aquila.web.utils.moex;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.data.DataFormatException;
import ru.prolib.aquila.web.utils.SearchWebElement;
import ru.prolib.aquila.web.utils.WUException;

/**
 * Accessing MOEX contract details.
 * <p>
 * @see http://moex.com/en/contract.aspx?code=S where S is a contract name
 */
public class MoexContractForm {
	private static final Logger logger;
	private static final long WEB_DRIVER_WAIT_SECONDS = 60;
	
	static {
		logger = LoggerFactory.getLogger(MoexContractForm.class);
	}
	
	private final WebDriver webDriver;
	private final MoexContractFormUtils formUtils = new MoexContractFormUtils();
	private final MoexContractPtmlConverter ptmlConverter = new MoexContractPtmlConverter();
	
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
	 * @throws WUWebPageException - an error occurred
	 */
	public Map<Integer, Object> getInstrumentDescription(String contractCode) throws WUWebPageException {
		openContractPage(contractCode);
		try {
			new WebDriverWait(webDriver, WEB_DRIVER_WAIT_SECONDS).until(ExpectedConditions.and(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[. = 'Instrument description']")),
				ExpectedConditions.visibilityOfElementLocated(By.className("tool_options_table_forts"))
			));
		} catch ( TimeoutException e ) {
			throw new WUWebPageException("Timeout: ", e);
		}
		
		List<WebElement> rows = newSearch()
			.find(By.className("tool_options_table_forts"))
			.findAll(By.tagName("tr"));
		Map<Integer, Object> tokens = new LinkedHashMap<>();
		for ( int i = 0; i < rows.size(); i ++ ) {
			WebElement row = rows.get(i);

			List<WebElement> cols = newSearchStartingFrom(row)
				.findAll(By.tagName("td"));
			if ( cols.size() != 2 ) {
				throw new WUWebPageException("Wrong number of elements of row #" + i + ":" + cols.size());
			}
			
			String contractFieldString = stripHtml(cols.get(0).getText());
			try {
				int contractField = formUtils.toContractField(contractFieldString);
				Object value = toContractValue(contractField, stripHtml(cols.get(1).getText()));
				tokens.put(contractField, value);
			} catch ( WUException e ) {
				throw new WUWebPageException("Error obtaining contract info: " +
						contractCode + " (field: " + contractFieldString
							+ ", row: " + i
							+ ", rowText: " + row.getText()
							+ ")", e);
			}
		}
		return tokens;
	}
	
	/**
	 * Get list of active futures.
	 * <p>
	 * @return list of symbols
	 * @throws WUWebPageException - an error occurred
	 */
	public List<String> getActiveFuturesList() throws WUWebPageException {
		// TODO: 1. Здесь бы оптимизировать, что бы сократить количество
		// повторений в случае ошибок. Слишком медленно список составляет.
		List<String> list = new ArrayList<>();
		openDerivativesSearchPage();
		list.addAll(dump(scanFuturesTableForSymbols()));
		Paginator paginator = getFuturesTablePaginator();
		int totalPages = paginator.getNumberOfPages();
		for ( int i = 1; i < totalPages; i ++ ) {
			//openDerivativesSearchPage(); // TODO: 2. Из-за этого значительно тормозило.
			paginator = getFuturesTablePaginator();
			paginator.click(i);
			paginator.waitForStale();
			list.addAll(dump(scanFuturesTableForSymbols()));
		}
		return list;
	}
	
	private List<String> dump(List<String> list) {
		//System.out.println("MoexContractForm#dump");
		//for ( String x : list ) {
		//	System.out.println("Found symbol: " + x);
		//}
		return list;
	}
	
	private Object toContractValue(int contractField, String stringValue) throws WUWebPageException {
		try {
			return ptmlConverter.toObject(contractField, stringValue);
		} catch ( DataFormatException e ) {
			throw new WUWebPageException("Convertation failed: ", e);
		}
		/*
		switch ( contractField ) {
		case MoexContractField.FIRST_TRADING_DAY:
		case MoexContractField.LAST_TRADING_DAY:
		case MoexContractField.DELIVERY:
		case MoexContractField.INITIAL_MARGIN_DATE:
			return formUtils.toLocalDate(stringValue);
		case MoexContractField.TICK_SIZE:
		case MoexContractField.LOWER_PRICE_LIMIT:
		case MoexContractField.UPPER_PRICE_LIMIT:
		case MoexContractField.SETTLEMENT_PRICE:
		case MoexContractField.LOT_SIZE:
			return formUtils.toDecimal(stringValue);
		case MoexContractField.TICK_VALUE:
		case MoexContractField.FEE:
		case MoexContractField.INTRADAY_FEE:
		case MoexContractField.NEGOTIATION_FEE:
		case MoexContractField.EXERCISE_FEE:
		case MoexContractField.INITIAL_MARGIN:
			return formUtils.toMoney(stringValue);
		case MoexContractField.FX_INTRADAY_CLEARING:
		case MoexContractField.FX_EVENING_CLEARING:
			return formUtils.toClearingTime(stringValue);
		case MoexContractField.SETTLEMENT_PROC_DESCR:
		case MoexContractField.SYMBOL:
		case MoexContractField.SYMBOL_CODE:
		case MoexContractField.CONTRACT_DESCR:
		case MoexContractField.SETTLEMENT:
		case MoexContractField.QUOTATION:
		case MoexContractField.TYPE:
		default:
			return stringValue;
		}
		*/
	}
	
	private MoexContractForm openContractPage(String contractCode) throws WUWebPageException {
		String uri = "http://moex.com/en/contract.aspx?code=" + contractCode;
		if ( uri.equals(webDriver.getCurrentUrl()) ) {
			// We're on the same page. BUT! It may be a refresh request!
		}
		try {
			webDriver.get(uri);
		} catch ( WebDriverException e ) {
			throw new WUWebPageException("Contract page request failed", e);
		}

		// Additional test for the contract existence.
		if ( ! contractCode.equals(newSearch()
				.find(By.xpath("//*[@id='contract']/div[2]/div[1]/table/tbody/tr/td[1]/b"))
				.get()
				.getText()
				.trim()) )
		{
			throw new WUWebPageException("Contract not exists or page has changed its structure: " + contractCode);
		}
		closeUserAgreement();
		return this;		
	}
	
	private MoexContractForm openDerivativesSearchPage() throws WUWebPageException {
		String uri = "http://moex.com/en/derivatives/contracts.aspx?p=act";
		if ( uri.equals(webDriver.getCurrentUrl()) ) {
			// We're on the same page. BUT! It may be a refresh request!
		}
		try {
			webDriver.get(uri);
		} catch ( WebDriverException e ) {
			throw new WUWebPageException("Search page request failed.", e);
		}
		// Additional check that the page is open
		WebElement x = newSearch()
			.findWithText(By.tagName("h1"), "Search by contracts")
			.get();
		if ( ! x.isDisplayed() ) {
			
		}
		closeUserAgreement();
		return this;
	}
	
	private String stripHtml(String html) {
		return StringEscapeUtils.unescapeHtml4(html);
	}
	
	private List<String> scanFuturesTableForSymbols() throws WUWebPageException {
		WebElement table = findFuturesTable();
		List<String> list = new ArrayList<>();
		boolean skipHeader = true;
		for ( WebElement row : newSearchStartingFrom(table).findAll(By.tagName("tr")) ) {
			if ( skipHeader ) {
				skipHeader = false;
			} else {
				try {
					list.add(newSearchStartingFrom(row)
						.find(By.tagName("td"), 1)
						.find(By.tagName("a"))
						.get()
						.getText()
						.trim());
					
				} catch ( WUWebPageException e ) {
					System.out.println("Error element: " + row.getText());
					throw e;
				}
			}
		}
		return list;
	}
	
	private void closeUserAgreement() {
		try {
			//String searchText = "BEFORE YOU START USING THE WEBSITE, PLEASE READ THIS AGREEMENT CAREFULLY";
			List<WebElement> list_disclaimer_buttons = webDriver.findElements(By.className("disclaimer__buttons"));
			if ( list_disclaimer_buttons.size() == 0 ) {
				logger.debug("Disclaimer buttons not found");
				return;
			}
			WebElement disclaimer_buttons = list_disclaimer_buttons.get(0);
			if ( ! disclaimer_buttons.isDisplayed() ) {
				logger.debug("Disclaimer buttons not visible");
				return;
			}
			logger.debug("Closing disclaimer window...");
			newSearchStartingFrom(disclaimer_buttons)
				.find(By.xpath("//*[. = 'I Agree']"))
				.get()
				.click();
		} catch ( Exception e ) {
			// timeout, do nothing
			logger.error("Timeout exception: ", e);
		}
	}
	
	/**
	 * Найти все пагинаторы на странице.
	 * <p>
	 * Что бы максимально отвязаться от возможных изменений, следует привязываться
	 * к таким признакам, вероятность изменения которых крайне мала. Про пагинатор
	 * известно, что это набор элементов, который начинается с элемента, содержащего
	 * строку "Pages:" и за которым следуют несколько элементов типа span, div, a,
	 * содержимое которых увеличивающиеся номера страниц начиная с 1. Таким образом,
	 * можно найти элемент с текстом "Pages:" и от его родителя просмотреть всех
	 * детей, пропуская самый первый элемент. Если в результате этого перебора
	 * получим последовательность увеличивающихся на 1 целочисленных значений от 1
	 * до N, значит родительский элемент это пагинатор.
	 * <p>
	 * <b>Примечание:</b> Вышеописанный вариант сработает, только если Pages
	 * находится на одном уровне с номерами страниц. В прошлой версии оно было
	 * в таблице - ее еще как то можно разобрать. Если надабовляют других спанов
	 * и дивов с разными уровнями вложенности, то сложнее.
	 * <p>
	 * @return список всех найденных пагинаторов
	 * @throws WUWebPageException произошла ошибка при поиске
	 */
	private List<Paginator> findPaginators() throws WUWebPageException {
		List<Paginator> paginators = new ArrayList<>();
		for ( WebElement parent : newSearch()
				.findAll(By.xpath("//*[. = 'Pages:']/parent::*")) )
		{
			boolean containsNonNumericReference = false;
			List<WebElement> children = newSearchStartingFrom(parent).findAll(By.xpath(".//*"));
			List<PageReference> pages = new ArrayList<>();
			for ( int i = 1; i < children.size(); i ++ ) {
				WebElement child = children.get(i);
				String t = child.getText();
				if ( t.length() == 0 || ! StringUtils.isNumeric(t) ) {
					containsNonNumericReference = true;
					break;
				}
				pages.add(new PageReference(Integer.parseInt(t), child));
			}
			if ( containsNonNumericReference ) {
				continue;
			}
			int numberOfInactiveLinks = 0;
			boolean incorrectPageOrder = false;
			for ( int i = 0; i < pages.size(); i ++ ) {
				PageReference p = pages.get(i);
				if ( p.getPageNumber() != i + 1 ) {
					incorrectPageOrder = true;
					break;
				}
				if ( ! p.isLink() ) {
					numberOfInactiveLinks ++;
				}
			}
			if ( incorrectPageOrder || numberOfInactiveLinks != 1 ) {
				continue;
			}
			paginators.add(new Paginator(pages));
		}
		return paginators;
	}
	
	/**
	 * Ярлык для получения пагинатора страниц таблицы фьючерсов.
	 * <p>
	 * Известно, что на первой странице всего 4 пагинатора: по два на фьючерсы и опционы.
	 * Просто возвращаем первый найденный пагинатор. А если не найдено, то кидаем исключение.
	 * <p>
	 * @return пагинатор таблицы фьючерсов
	 * @throws WUWebPageException не удалось найти пагинатор или другая ошибка
	 */
	private Paginator getFuturesTablePaginator() throws WUWebPageException {
		List<Paginator> paginators = findPaginators();
		if ( paginators.size() == 0 ) {
			throw new WUWebPageException("Futures table paginator not found");
		}
		return paginators.get(0);
	}
	
	private SearchWebElement newSearch() {
		return new SearchWebElement(webDriver);
	}
	
	private SearchWebElement newSearchStartingFrom(WebElement element) {
		return new SearchWebElement(webDriver, element);
	}

	/**
	 * Найти таблицу фьючерсов.
	 * <p>
	 * Что бы максимально отвязаться от возможных изменений, следует привязываться
	 * к таким элементам, вероятность изменения которых крайне мала. Это можно
	 * сказать про заголовки таблицы фьючерсов. Заголовки таблиц фтючерсов и опционов
	 * различаются так, что перепутать их невозможно. Исходя из вышесказанного,
	 * ищем таблицу, в которой первая строка содержит следующие заголовки:
	 * <li>0 - игнорировать</li>
     * <li>1 - Symbol</li>
	 * <li>2 - Description</li>
	 * <li>3 - Last trading day</li>
	 * <li>4 - Delivery</li>
	 * <li>5 - Trade results</li>
	 * В итоге требования минимальны: это должна быть таблица, ее первая строка должна
	 * содержать указанные заголовки.
	 * @return веб-элемент таблицы фьючерсов
	 * @throws WUWebPageException - не удалось найти таблицу фьючерсов или другая ошибка
	 */
	private WebElement findFuturesTable() throws WUWebPageException {
		for ( WebElement table : newSearch().findAll(By.tagName("table")) ) {
			try {
				WebElement row = newSearchStartingFrom(table).find(By.tagName("tr"), 0).get();
				List<WebElement> cols = newSearchStartingFrom(row).findAll(By.tagName("th"));
				if ( cols.size() == 6
					&& "Symbol".equals(cols.get(1).getText())
					&& "Description".equals(cols.get(2).getText())
					&& "Last trading day".equals(cols.get(3).getText())
					&& "Delivery".equals(cols.get(4).getText())
					&& "Trade results".equals(cols.get(5).getText()) )
				{
					return table;
				}
			} catch ( WUWebPageException e ) {
				// the table without rows. just ignore this table
			}
		}
		throw new WUWebPageException("Futures table was not found");
	}
	
	class PageReference {
		private final int pageNumber;
		private final WebElement element;
		
		PageReference(int pageNumber, WebElement element) {
			this.pageNumber = pageNumber;
			this.element = element;
		}
		
		public int getPageNumber() {
			return pageNumber;
		}
		
		public boolean isLink() {
			return "a".equals(element.getTagName());
		}
		
		public void click() throws WUWebPageException {
			if ( ! isLink() ) {
				throw new WUWebPageException("Cannot click on inactive page: " + pageNumber);
			}
			element.click();
		}
		
	}
	
	class Paginator {
		private final List<PageReference> pages;
		
		Paginator(List<PageReference> pages) {
			this.pages = new ArrayList<>(pages);
		}
		
		public void click(int pageNumber) throws WUWebPageException {
			pages.get(pageNumber).click();
		}
		
		public int getNumberOfPages() {
			return pages.size();
		}
		
		public void waitForStale() {
			ExpectedCondition<?> cond[] = new ExpectedCondition[pages.size()];
			for ( int i = 0; i < pages.size(); i ++ ) {
				PageReference p = pages.get(i);
				cond[i] = ExpectedConditions.stalenessOf(p.element);
			}
			try {
				new WebDriverWait(webDriver, WEB_DRIVER_WAIT_SECONDS).until(ExpectedConditions.or(cond));
			} catch ( TimeoutException e ) {
				logger.error("Timeout exception: ", e);
			}
		}
		
	}

}
