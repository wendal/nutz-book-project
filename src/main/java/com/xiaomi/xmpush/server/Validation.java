package com.xiaomi.xmpush.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Validation extends HttpBase {
	protected static final Logger logger = Logger.getLogger(Validation.class.getName());

	public Validation(String security) {
		super(security);
	}

	public String validateRegistrationIds(List<String> regIds, int retries) throws IOException {
		int attempt = 0;

		int backoff = 1000;
		String result;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Attempt #" + attempt + " to validate regids.");
			}
			result = validateRegistrationIdsNoRetry(regIds);
			tryAgain = (result == null) && (attempt <= retries);
			if (tryAgain) {
				int sleepTime = backoff / 2 + this.random.nextInt(backoff);
				sleep(sleepTime);
				if (2 * backoff < 1024000) {
					backoff *= 2;
				}
			}
		} while (tryAgain);
		if (result == null) {
			throw new IOException("Could not get expired regids after " + attempt + " attempts");
		}
		return result;
	}

	public String validateRegistrationIdsNoRetry(List<String> regIds) throws UnsupportedEncodingException, InvalidRequestException {
		StringBuilder parameter = newBodyWithArrayParameters("registration_ids", regIds);
		String parameterString = parameter.toString();
		HttpURLConnection conn;
		int status;
		try {
			logger.fine("get from: " + Constants.XMPUSH_VALIDATE_REGID);
			conn = doPost(Constants.XMPUSH_VALIDATE_REGID, parameterString);
			status = conn.getResponseCode();
		} catch (IOException e) {
			logger.log(Level.FINE, "IOException while validating registration ids", e);
			return null;
		}
		if (status / 100 == 5) {
			logger.fine("XmPush service is unavailable (status " + status + ")");
			return null;
		}

		if (status != 200) {
			String responseBody;
			try {
				responseBody = getAndClose(conn.getErrorStream());
				logger.finest("Plain get error response: " + responseBody);
			} catch (IOException e) {
				responseBody = "N/A";
				logger.log(Level.FINE, "Exception reading response: ", e);
			}
			throw new InvalidRequestException(status, responseBody);
		}
		String responseBody;
		try {
			responseBody = getAndClose(conn.getInputStream());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Exception reading response: ", e);
			return null;
		}

		return responseBody;
	}
}