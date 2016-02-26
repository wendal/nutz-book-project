package com.xiaomi.xmpush.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tracer extends HttpBase {
	protected static final Logger logger = Logger.getLogger(Tracer.class.getName());

	public Tracer(String security) {
		super((String) nonNull(security));
	}

	public String getMessageGroupStatus(String jobKey, int retries) throws IOException {
		int attempt = 0;

		int backoff = 1000;
		String result;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Attempt #" + attempt + " to get status of message group " + jobKey);
			}
			result = getMessageGroupStatusNoRetry(jobKey);
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
			throw new IOException("Could not get message group status after " + attempt + " attempts");
		}
		return result;
	}

	public String getMessageStatus(String msgId, int retries) throws IOException {
		int attempt = 0;

		int backoff = 1000;
		String result;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Attempt #" + attempt + " to get status of message " + msgId);
			}
			result = getMessageStatusNoRetry(msgId);
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
			throw new IOException("Could not get message status after " + attempt + " attempts");
		}
		return result;
	}

	public String getMessageStatus(long beginTime, long endTime, int retries) throws IOException {
		int attempt = 0;

		int backoff = 1000;
		String result;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Attempt #" + attempt + " to get messages status between " + beginTime + " and " + endTime);
			}
			result = getMessageStatusNoRetry(beginTime, endTime);
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
			throw new IOException("Could not get messages status after " + attempt + " attempts");
		}
		return result;
	}

	public String getMessageGroupStatusNoRetry(String jobKey) throws UnsupportedEncodingException, InvalidRequestException {
		StringBuilder parameter = newBody("job_key", URLEncoder.encode(jobKey, "UTF-8"));
		String parameterString = parameter.toString();
		HttpURLConnection conn;
		int status;
		try {
			logger.fine("get from: " + Constants.XMPUSH_MESSAGE_TRACE);
			conn = doGet(Constants.XMPUSH_MESSAGE_TRACE, parameterString);
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

	public String getMessageStatusNoRetry(String msgId) throws UnsupportedEncodingException, InvalidRequestException {
		StringBuilder parameter = newBody("msg_id", URLEncoder.encode(msgId, "UTF-8"));
		String parameterString = parameter.toString();
		HttpURLConnection conn;
		int status;
		try {
			logger.fine("get from: " + Constants.XMPUSH_MESSAGE_TRACE);
			conn = doGet(Constants.XMPUSH_MESSAGE_TRACE, parameterString);
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

	public String getMessageStatusNoRetry(long beginTime, long endTime) throws UnsupportedEncodingException, InvalidRequestException {
		StringBuilder parameter = newBody("begin_time", URLEncoder.encode("" + beginTime, "UTF-8"));
		addParameter(parameter, "end_time", URLEncoder.encode("" + endTime, "UTF-8"));
		String parameterString = parameter.toString();
		HttpURLConnection conn;
		int status;
		try {
			logger.fine("get from: " + Constants.XMPUSH_MESSAGES_TRACE);
			conn = doGet(Constants.XMPUSH_MESSAGES_TRACE, parameterString);
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