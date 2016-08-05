package ru.prolib.aquila.utils.finexp.quotelist;

import java.util.Iterator;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.openqa.selenium.io.IOUtils;

import ru.prolib.aquila.web.utils.finam.Fidexp;

/**
 * List of current futures.
 * <p>
 * This class produces a CSV-data with list of futures which are currently
 * available for download thru FINAM web-interface.
 */
public class CurrentFuturesList {
	private static final String HEADER = "TICKER,CODE,FINAM_WEB_ID";
	
	public static void main(String[] args) {
		Fidexp facade = new Fidexp();
		try {
			Map<Integer, String> map = facade.getTrueFuturesQuotes(false);
			System.out.println(HEADER);
			Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();
			while ( it.hasNext() ) {
				Map.Entry<Integer, String> dummy = it.next();
				processOption(dummy.getKey(), facade.splitFuturesCode(dummy.getValue()));
			}
			System.exit(0);			
		} catch ( Exception e ) {
			e.printStackTrace(System.err);
			System.exit(1);
		} finally {
			IOUtils.closeQuietly(facade);
		}
	}
	
	private static void processOption(int id, NameValuePair parts) {
		if ( parts == null ) {
			return;
		}
		System.out.print(parts.getName() + ",");
		System.out.print(parts.getValue() + ",");
		System.out.println(id);
	}

}
