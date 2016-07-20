package ru.prolib.aquila.utils.finexp.quotelist;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import ru.prolib.aquila.finam.tools.web.DataExportForm;
import ru.prolib.aquila.finam.tools.web.DataExportForm.SelectorOption;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

/**
 * List of current futures.
 * <p>
 * This class produces a CSV-data with list of futures which are currently
 * available for download thru FINAM web-interface.
 */
public class CurrentFuturesList {
	private static final String MARKET = "МосБиржа фьючерсы";
	private static final String HEADER = "TICKER,CODE,FINAM_WEB_ID";
	
	public static void main(String[] args) {
		try {
			try ( DataExportForm form = new DataExportForm(createDriver()) ) {
				List<SelectorOption> list = form.selectMarket(MARKET) .getQuoteOptions();
				System.out.println(HEADER);
				for ( SelectorOption o : list ) {
					processOption(o);
				}
			}
			System.exit(0);
		} catch ( Exception e ) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
	
	private static void processOption(SelectorOption o) {
		String text = o.getText();
		if ( ! text.endsWith(")") ) {
			// Not a futures, combined data or the ticker without a code (but we need it).
			// Skip such option.
			return;
		}
		String tokens[] = StringUtils.split(text, '(');
		if ( tokens.length != 2 ) {
			// The opening brace was not found. Cannot determine the ticker code.
			// Skip such option.
			return;
		}
		System.out.print(tokens[0] + ",");
		System.out.print(tokens[1].substring(0, tokens[1].length() - 1) + ",");
		System.out.println(o.getID());
	}
	
	private static WebDriver createDriver() {
		return new JBrowserDriver(Settings.builder()
				.timezone(Timezone.EUROPE_MOSCOW)
				.ssl("compatible")
				.build());
	}

}
