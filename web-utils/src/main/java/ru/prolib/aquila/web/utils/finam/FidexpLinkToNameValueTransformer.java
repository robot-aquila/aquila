package ru.prolib.aquila.web.utils.finam;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.WebElement;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.WebElementTransformer;

/**
 * Transforms a hyperlink from Data Export Web Form of FINAM site to a name-value pair.
 * The links MUST have the "value" attribute.
 * It MAY not work with hyperlinks from other sites!
 */
public class FidexpLinkToNameValueTransformer implements WebElementTransformer<NameValuePair> {

	@Override
	public NameValuePair transform(WebElement element) throws WUWebPageException {
		String value = element.getAttribute("value");
		String text = element.getText();
		if ( value == null ) {
			throw new WUWebPageException("The link does not have a [value] attribute: " + text);
		}
		return new BasicNameValuePair(text, value);
	}

}
