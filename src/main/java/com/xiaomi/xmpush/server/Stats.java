package com.xiaomi.xmpush.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Stats extends HttpBase {
	protected static final Logger logger = Logger.getLogger(Stats.class.getName());

	public Stats(String security) {
		super((String) nonNull(security));
	}

	public String getStats(String startDate, String endDate, String packageName, int retries) throws IOException {
		int attempt = 0;

		int backoff = 1000;
		String result;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Attempt #" + attempt + " to get realtime stats data between " + startDate + " and " + endDate);
			}
			result = getStatsNoRetry(startDate, endDate, packageName);
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
			throw new IOException("Could not get realtime stats data after " + attempt + " attempts");
		}
		return result;
	}

	protected String getStatsNoRetry(String startDate, String endDate, String packageName) throws UnsupportedEncodingException, InvalidRequestException {
		StringBuilder parameter = newBody("start_date", URLEncoder.encode(startDate, "UTF-8"));
		addParameter(parameter, "end_date", URLEncoder.encode(endDate, "UTF-8"));
		addParameter(parameter, "restricted_package_name", URLEncoder.encode(packageName, "UTF-8"));
		String parameterString = parameter.toString();
		HttpURLConnection conn;
		int status;
		try {
			logger.fine("get from: " + Constants.XMPUSH_STATS);
			conn = doGet(Constants.XMPUSH_STATS, parameterString);
			status = conn.getResponseCode();
		} catch (IOException e) {
			logger.log(Level.FINE, "IOException while get from XmPush", e);
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