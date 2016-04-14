package com.xiaomi.xmpush.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class DevTools extends HttpBase {
	public DevTools(String security) {
		super(security);
	}

	public String getAliasesOf(String packageName, String regId, int retries) throws IOException {
		int attempt = 0;

		int backoff = 1000;
		String result;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled())
				logger.info("Attempt #" + attempt + " to get all aliases of the device.");
			result = getAliasesNoRetry(packageName, regId);
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
			throw new IOException("Could not get all aliases of the device after " + attempt + " attempts");
		}
		return result;
	}

	public String getTopicsOf(String packageName, String regId, int retries) throws IOException {
		int attempt = 0;

		int backoff = 1000;
		String result;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to get all topics of the device.");
			}
			result = getTopicsNoRetry(packageName, regId);
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
			throw new IOException("Could not get all topics of the device after " + attempt + " attempts");
		}
		return result;
	}

	public String getAccountsOf(String packageName, String regId, int retries) throws IOException {
		int attempt = 0;

		int backoff = 1000;
		String result;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to get all topics of the device.");
			}
			result = getAccountsNoRetry(packageName, regId);
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
			throw new IOException("Could not get all user accounts of the device after " + attempt + " attempts");
		}
		return result;
	}

	protected String getAliasesNoRetry(String packageName, String regId) throws InvalidRequestException {
		HttpURLConnection conn;

		int status;

		try {
			StringBuilder sb = newBody("restricted_package_name", URLEncoder.encode(packageName, "UTF-8"));
			addParameter(sb, "registration_id", URLEncoder.encode(regId, "UTF-8"));
			if (logger.isInfoEnabled()) {
				logger.info("get from: " + Constants.XMPUSH_GET_ALIASES_OF_DEVICE);
			}
			conn = doGet(Constants.XMPUSH_GET_ALIASES_OF_DEVICE, sb.toString());
			status = conn.getResponseCode();
		} catch (IOException e) {
			logger.warn("IOException while get from XmPush", e);
			return null;
		}
		if (status / 100 == 5) {
			if (logger.isInfoEnabled()) {
				logger.info("XmPush service is unavailable (status " + status + ")");
			}
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

	protected String getTopicsNoRetry(String packageName, String regId) throws InvalidRequestException {
		HttpURLConnection conn;

		int status;

		try {
			StringBuilder sb = newBody("restricted_package_name", URLEncoder.encode(packageName, "UTF-8"));
			addParameter(sb, "registration_id", URLEncoder.encode(regId, "UTF-8"));
			logger.info("get from: " + Constants.XMPUSH_GET_TOPICS_OF_DEVICE);
			conn = doGet(Constants.XMPUSH_GET_TOPICS_OF_DEVICE, sb.toString());
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
				logger.info("Plain get error response: " + responseBody);
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

	protected String getAccountsNoRetry(String packageName, String regId) throws InvalidRequestException {
		HttpURLConnection conn;

		int status;

		try {
			StringBuilder sb = newBody("restricted_package_name", URLEncoder.encode(packageName, "UTF-8"));
			addParameter(sb, "registration_id", URLEncoder.encode(regId, "UTF-8"));
			logger.info("get from: " + Constants.XMPUSH_GET_ACCOUNTS_OF_DEVICE);
			conn = doGet(Constants.XMPUSH_GET_ACCOUNTS_OF_DEVICE, sb.toString());
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