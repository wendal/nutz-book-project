package com.xiaomi.xmpush.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Subscription extends HttpBase {
	public Subscription(String security) {
		super((String) nonNull(security));
	}

	protected static String joinString(List<String> stringList, char sep) {
		StringBuffer sb = new StringBuffer();
		for (String s : stringList) {
			sb.append(sep).append(s);
		}
		return sb.substring(1);
	}

	public Result subscribeTopic(List<String> regIds, String topic, String category, int retries) throws IOException, ParseException {
		return topicSubscribeBase(regIds, topic, category, retries, true);
	}

	public Result subscribeTopic(List<String> regIds, String topic, String category) throws IOException, ParseException {
		return subscribeTopic(regIds, topic, category, 1);
	}

	public Result subscribeTopic(String regId, String topic, String category, int retries) throws IOException, ParseException {
		List<String> regIds = new ArrayList<String>();
		regIds.add(regId);
		return subscribeTopic(regIds, topic, category, retries);
	}

	public Result subscribeTopic(String regId, String topic, String category) throws IOException, ParseException {
		return subscribeTopic(regId, topic, category, 1);
	}

	public Result unsubscribeTopic(List<String> regIds, String topic, String category, int retries) throws IOException, ParseException {
		return topicSubscribeBase(regIds, topic, category, retries, false);
	}

	public Result unsubscribeTopic(List<String> regIds, String topic, String category) throws IOException, ParseException {
		return unsubscribeTopic(regIds, topic, category, 1);
	}

	public Result unsubscribeTopic(String regId, String topic, String category, int retries) throws IOException, ParseException {
		List<String> regIds = new ArrayList<String>();
		regIds.add(regId);
		return unsubscribeTopic(regIds, topic, category, retries);
	}

	public Result unsubscribeTopic(String regId, String topic, String category) throws IOException, ParseException {
		return unsubscribeTopic(regId, topic, category, 1);
	}

	public Result subscribeTopicByAlias(String topic, List<String> aliases, String category, int retries) throws IOException, ParseException {
		int attempt = 0;
		Result result = null;
		int backoff = 1000;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to subscribe topic " + topic + " for aliases " + aliases);
			}
			StringBuilder body = newBody("aliases", URLEncoder.encode(joinString(aliases, ','), "UTF-8"));
			addParameter(body, "topic", URLEncoder.encode(topic, "UTF-8"));
			if (category != null) {
				addParameter(body, "category", URLEncoder.encode(category, "UTF-8"));
			}
			String bodyStr = body.toString();
			result = sendMessage(Constants.XMPUSH_SUBSCRIBE_TOPIC_BY_ALIAS, bodyStr);
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
			throw new IOException("Could not subscribe topic after " + attempt + " attempts");
		}
		return result;
	}

	public Result unsubscribeTopicByAlias(String topic, List<String> aliases, String category, int retries) throws IOException, ParseException {
		int attempt = 0;
		Result result = null;
		int backoff = 1000;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to unsubscribe topic " + topic + " for aliases " + aliases);
			}
			StringBuilder body = newBody("aliases", URLEncoder.encode(joinString(aliases, ','), "UTF-8"));
			addParameter(body, "topic", URLEncoder.encode(topic, "UTF-8"));
			if (category != null) {
				addParameter(body, "category", URLEncoder.encode(category, "UTF-8"));
			}
			String bodyStr = body.toString();
			result = sendMessage(Constants.XMPUSH_UNSUBSCRIBE_TOPIC_BY_ALIAS, bodyStr);
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
			throw new IOException("Could not unsubscribe topic after " + attempt + " attempts");
		}
		return result;
	}

	protected Result topicSubscribeBase(List<String> regIds, String topic, String category, int retries, boolean isSubscribe) throws IOException, ParseException {
		int attempt = 0;
		Result result = null;
		int backoff = 1000;
		boolean tryAgain = false;
		String regIdsString = joinString(regIds, ',');

		String type = isSubscribe ? "subscribe" : "unsubscribe";
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to send " + type + " topic " + topic + " to regIds " + regIdsString);
			}

			StringBuilder body = newBody("registration_id", URLEncoder.encode(regIdsString, "UTF-8"));
			addParameter(body, "topic", URLEncoder.encode(topic, "UTF-8"));
			if (category != null) {
				addParameter(body, "category", URLEncoder.encode(category, "UTF-8"));
			}
			String bodyStr = body.toString();

			if (bodyStr.charAt(0) == '&') {
				bodyStr = body.toString().substring(1);
			}

			String url = isSubscribe ? Constants.XMPUSH_ENDPOINT_SUBSCRIPTION : Constants.XMPUSH_ENDPOINT_UNSUBSCRIPTION;
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
			throw new IOException("Could not " + type + " topic after " + attempt + " attempts");
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