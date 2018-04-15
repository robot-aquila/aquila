package ru.prolib.aquila.web.utils.finam;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;

import ru.prolib.aquila.web.utils.WUInvalidResponseException;
import ru.prolib.aquila.web.utils.httpclient.ResponseValidator;

@Deprecated
public class FidexpResponseValidator implements ResponseValidator {

	@Override
	public void validateResponse(CloseableHttpResponse response)
			throws WUInvalidResponseException
	{
		int statusCode = response.getStatusLine().getStatusCode();
		if ( statusCode != 200 ) {
			throw new WUInvalidResponseException("Unexpected HTTP status code: " + statusCode);
		}
		Header header = response.getFirstHeader("content-type");
		if ( header == null ) {
			throw new WUInvalidResponseException("Header Content-Type not exists");
		}
		String contentType = header.getValue();
		if ( ! "finam/expotfile".equals(contentType) ) {
			throw new WUInvalidResponseException("Unexpected Content-Type: " + contentType);
		}
		header = response.getFirstHeader("content-disposition");
		if ( header == null ) {
			throw new WUInvalidResponseException("Header Content-Disposition not exists");
		}
		String contentDisposition = header.getValue();
		if ( ! contentDisposition.startsWith("attachment;") ) {
			throw new WUInvalidResponseException("Unexpected Content-Disposition: " + contentDisposition);
		}
	}

}
