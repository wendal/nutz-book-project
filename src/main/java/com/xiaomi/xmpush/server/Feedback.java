package com.xiaomi.xmpush.server;

import java.io.IOException;
import java.net.HttpURLConnection;

public class Feedback extends HttpBase {
	public Feedback(String security) {
		super((String) nonNull(security));
	}

	public String getInvalidRegIds(int retries) throws IOException {
		int attempt = 0;

		int backoff = 1000;
		String result;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to get invalid registration ids");
			}
			result = getInvalidRegIdsNoRetry();
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
			throw new IOException("Could not get invalid registration ids after " + attempt + " attempts");
		}
		return result;
	}

	protected String getInvalidRegIdsNoRetry() throws InvalidRequestException {
		HttpURLConnection conn;

		int status;

		try {
			logger.info("get from: " + Constants.XMPUSH_FETCH_INVALID_REGIDS);
			conn = doGet(Constants.XMPUSH_FETCH_INVALID_REGIDS, "");
			status = conn.getResponseCode();
		} catch (IOException e) {
			logger.warn("IOException while get from XmPush", e);
			return null;
		}
		if (status / 100 == 5) {
			logger.info("XmPush service is unavailable (status " + status + ")");
			return null;
		}

		if (status != 200) {
			String responseBody;
			try {
				responseBody = getAndClose(conn.getErrorStream());
				logger.debug("Plain get error response: " + responseBody);
			} catch (IOException e) {
				responseBody = "N/A";
				logger.warn("Exception reading response: ", e);
			}
			throw new InvalidRequestException(status, responseBody);
		}
		String responseBody;
		try {
			responseBody = getAndClose(conn.getInputStream());
		} catch (IOException e) {
			logger.warn("Exception reading response: ", e);
			return null;
		}

		return responseBody;
	}
}