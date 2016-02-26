package com.xiaomi.xmpush.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MessageTool extends HttpBase {
	public MessageTool(String security) {
		super((String) nonNull(security));
	}

	public Result deleteTopic(String jobId, int retries) throws IOException, ParseException {
		StringBuilder body = newBody("id", URLEncoder.encode(jobId, "UTF-8"));
		return sendMessage(Constants.XMPUSH_ENDPOINT_DELETE_TOPIC, body, retries);
	}

	public Result deleteTopic(String jobId) throws IOException, ParseException {
		return deleteTopic(jobId, 1);
	}

	protected Result sendMessage(String url, StringBuilder body, int retries) throws IOException, ParseException {
		int attempt = 0;
		Result result = null;
		int backoff = 1000;
		boolean tryAgain = false;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to send " + body + " to url " + url);
			}

			String bodyStr = body.toString();

			if (bodyStr.charAt(0) == '&') {
				bodyStr = body.toString().substring(1);
			}

			result = sendMessage(url, bodyStr);
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
			throw new IOException("Could not  send after " + attempt + " attempts");
		}
		return result;
	}

	protected Result sendMessage(String url, String message) throws IOException, ParseException {
		HttpURLConnection conn;
		int status;
		try {
			logger.info("post to: " + url);
			conn = doPost(url, message);
			status = conn.getResponseCode();
		} catch (IOException e) {
			logger.warn("IOException posting to XmPush", e);
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
				logger.debug("Plain post error response: " + responseBody);
			} catch (IOException e) {
				responseBody = "N/A";
				logger.info("Exception reading response: ", e);
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
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(responseBody);
			return new Result.Builder().fromJson(json);
		} catch (ParseException e) {
			logger.warn("Exception parsing response: ", e);
			throw new IOException("Invalid response from XmPush: " + responseBody);
		}
	}
}