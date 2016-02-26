package com.xiaomi.xmpush.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.xiaomi.push.sdk.ErrorCode;

public class Sender extends HttpBase {
	public static final int BROADCAST_TOPIC_MAX = 5;
	public static final String TOPIC_SPLITTER = ";$;";

	public Sender(String security) {
		super((String) nonNull(security));
	}

	public Result send(Message message, String registrationId, int retries) throws IOException, ParseException {
		int attempt = 0;
		Result result = null;
		int backoff = 1000;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to send message " + message + " to regIds " + registrationId);
			}
			result = sendNoRetry(message, registrationId);
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
			throw new IOException("Could not send message after " + attempt + " attempts");
		}
		return result;
	}

	public Result broadcast(Message message, String topic, int retries) throws IOException, ParseException {
		int attempt = 0;
		Result result = null;
		int backoff = 1000;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to broadcast message " + message + " to topic: " + topic);
			}
			result = broadcastNoRetry(message, topic);
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
			throw new IOException("Could not broadcast message after " + attempt + " attempts");
		}
		return result;
	}

	public static enum BROADCAST_TOPIC_OP {
		UNION, INTERSECTION, EXCEPT;

		private BROADCAST_TOPIC_OP() {
		}
	}

	public Result multiTopicBroadcast(Message message, List<String> topics, BROADCAST_TOPIC_OP topicOp, int retries) throws IOException, ParseException, IllegalArgumentException {
		if ((topics == null) || (topics.size() <= 0) || (topics.size() > 5))
			throw new IllegalArgumentException("topics size invalid");
		if (topics.size() == 1)
			return broadcast(message, (String) topics.get(0), retries);
		int attempt = 0;

		int backoff = 1000;
		boolean tryAgain = false;
		Result result;
		do {
			attempt++;
			logger.info("Attempt #" + attempt + " to broadcast message " + message + " to topic: " + (String) topics.get(0) + " op=" + topicOp.toString());

			result = multiTopicBroadcastNoRetry(message, topics, topicOp);
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
			throw new IOException("Could not broadcast message after " + attempt + " attempts");
		}
		return result;
	}

	public Result broadcastAll(Message message, int retries) throws IOException, ParseException {
		int attempt = 0;
		Result result = null;
		int backoff = 1000;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to broadcast message " + message + " to all");
			}
			result = broadcastAllNoRetry(message);
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
			throw new IOException("Could not broadcast message to all after " + attempt + " attempts");
		}
		return result;
	}

	public Result sendToAlias(Message message, String alias, int retries) throws IOException, ParseException {
		List<String> aliases = new ArrayList<String>();
		aliases.add(alias);
		return sendToAlias(message, aliases, retries);
	}

	public Result sendToAlias(Message message, List<String> aliases, int retries) throws IOException, ParseException {
		int attempt = 0;
		Result result = null;
		int backoff = 1000;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to send message " + message + " to alias " + aliases);
			}
			result = sendToAliasNoRetry(message, aliases);
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
			throw new IOException("Could not send message after " + attempt + " attempts");
		}
		return result;
	}

	public Result sendToAliasNoRetry(Message message, List<String> aliases) throws IOException, ParseException {
		StringBuilder body = newBodyWithArrayParameters("alias", aliases);
		return sendMessageNoRetry(Constants.XMPUSH_SEND_ENDPOINT_ALIAS, message, body);
	}

	public Result sendToUserAccount(Message message, String userAccount, int retries) throws IOException, ParseException {
		List<String> userAccounts = new ArrayList<String>();
		userAccounts.add(userAccount);
		return sendToUserAccount(message, userAccounts, retries);
	}

	public Result sendToUserAccount(Message message, List<String> userAccounts, int retries) throws IOException, ParseException {
		int attempt = 0;
		Result result = null;
		int backoff = 1000;
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to send message " + message + " to user account " + userAccounts);
			}
			result = sendToUserAccountNoRetry(message, userAccounts);
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
			throw new IOException("Could not send message after " + attempt + " attempts");
		}
		return result;
	}

	public Result sendToUserAccountNoRetry(Message message, List<String> userAccounts) throws IOException, ParseException {
		StringBuilder body = newBodyWithArrayParameters("user_account", userAccounts);
		return sendMessageNoRetry(Constants.XMPUSH_SEND_ENDPOINT_USER_ACCOUNT, message, body);
	}

	public Result sendNoRetry(Message message, String registrationId) throws IOException, ParseException {
		StringBuilder body = newBody("registration_id", URLEncoder.encode(registrationId, "UTF-8"));
		return sendMessageNoRetry(Constants.XMPUSH_SEND_ENDPOINT_REGID, message, body);
	}

	public Result broadcastNoRetry(Message message, String topic) throws IOException, ParseException {
		StringBuilder body = newBody("topic", URLEncoder.encode(topic, "UTF-8"));
		return sendMessageNoRetry(Constants.XMPUSH_SEND_ENDPOINT_TOPIC, message, body);
	}

	public Result multiTopicBroadcastNoRetry(Message message, List<String> topics, BROADCAST_TOPIC_OP topicOp) throws IOException, ParseException, IllegalArgumentException {
		if ((topics == null) || (topics.size() <= 0) || (topics.size() > 5))
			throw new IllegalArgumentException("topics size invalid");
		if (topics.size() == 1)
			return broadcastNoRetry(message, (String) topics.get(0));
		StringBuilder body = newBody("topic_op", topicOp.toString());
		StringBuilder topicsStr = new StringBuilder();
		for (String topic : topics) {
			if (topicsStr.length() != 0)
				topicsStr.append(";$;");
			topicsStr.append(topic);
		}
		addParameter(body, "topics", URLEncoder.encode(topicsStr.toString(), "UTF-8"));
		return sendMessageNoRetry(Constants.XMPUSH_SEND_ENDPOINT_MULTI_TOPIC, message, body);
	}

	public Result broadcastAllNoRetry(Message message) throws IOException, ParseException {
		StringBuilder body = new StringBuilder("");
		return sendMessageNoRetry(Constants.XMPUSH_SEND_ENDPOINT_ALL, message, body);
	}

	private Result sendMessageNoRetry(String url, Message message, StringBuilder target) throws IOException, ParseException {
		StringBuilder body = new StringBuilder(target);
		if (!XMStringUtils.isEmpty(message.getCollapseKey())) {
			addParameter(body, "collapse_key", URLEncoder.encode(message.getCollapseKey(), "UTF-8"));
		}
		if (!XMStringUtils.isEmpty(message.getRestrictedPackageName())) {
			addParameter(body, "restricted_package_name", URLEncoder.encode(message.getRestrictedPackageName(), "UTF-8"));
		}
		Long timeToLive = message.getTimeToLive();
		if (timeToLive != null) {
			addParameter(body, "time_to_live", Long.toString(timeToLive.longValue()));
		}
		if (!XMStringUtils.isEmpty(message.getPayload())) {
			addParameter(body, "payload", URLEncoder.encode(message.getPayload(), "UTF-8"));
		}
		if (!XMStringUtils.isEmpty(message.getTitle())) {
			addParameter(body, "title", URLEncoder.encode(message.getTitle(), "UTF-8"));
		}
		if (!XMStringUtils.isEmpty(message.getDescription())) {
			addParameter(body, "description", URLEncoder.encode(message.getDescription(), "UTF-8"));
		}
		if (message.getNotifyType() != null) {
			addParameter(body, "notify_type", Integer.toString(message.getNotifyType().intValue()));
		}
		if (message.getPassThrough() != null) {
			addParameter(body, "pass_through", Integer.toString(message.getPassThrough().intValue()));
		}
		if (message.getNotifyId() != null) {
			addParameter(body, "notify_id", Integer.toString(message.getNotifyId().intValue()));
		}
		if (message.getTimeToSend() != null) {
			addParameter(body, "time_to_send", Long.toString(message.getTimeToSend().longValue()));
		}
		Map<String, String> extraInfo = message.getExtra();
		if ((extraInfo != null) && (!extraInfo.isEmpty())) {
			for (Map.Entry<String, String> entry : extraInfo.entrySet()) {
				addParameter(body, URLEncoder.encode("extra." + (String) entry.getKey(), "UTF-8"), URLEncoder.encode((String) entry.getValue(), "UTF-8"));
			}
		}
		String bodyStr = body.toString();

		if ((!bodyStr.isEmpty()) && (bodyStr.charAt(0) == '&')) {
			bodyStr = body.toString().substring(1);
		}
		return sendMessage(url, bodyStr);
	}

	private Result execScheduleJobNoRetry(String url, StringBuilder target) throws IOException, ParseException {
		return sendMessage(url, target.toString());
	}

	public Result send(Message message, List<String> regIds, int retries) throws IOException, ParseException {
		StringBuilder sb = new StringBuilder((String) regIds.get(0));
		for (int i = 1; i < regIds.size(); i++) {
			sb.append(",").append((String) regIds.get(i));
		}
		return send(message, sb.toString(), retries);
	}

	public Result send(List<TargetedMessage> messages, int retries) throws IOException, ParseException {
		return send(messages, retries, 0L);
	}

	public Result send(List<TargetedMessage> messages, int retries, long timeToSend) throws IOException, ParseException {
		if (messages.isEmpty()) {
			logger.warn("send empty message. return");
			return new Result.Builder().errorCode(ErrorCode.Success).build();
		}
		int attempt = 0;
		Result result = null;
		int backoff = 1000;

		String url;
		if (((TargetedMessage) messages.get(0)).getTargetType() == 2) {
			url = Constants.XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_ALIAS;
		} else {
			if (((TargetedMessage) messages.get(0)).getTargetType() == 3) {
				url = Constants.XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_USER_ACCOUNT;
			} else {
				url = Constants.XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_REGID;
			}
		}
		StringBuilder body = newBody("messages", URLEncoder.encode(toString(messages), "UTF-8"));
		addParameter(body, "time_to_send", Long.toString(timeToSend));
		String message = body.toString();
		boolean tryAgain;
		do {
			attempt++;
			if (logger.isInfoEnabled()) {
				logger.info("Attempt #" + attempt + " to send messages " + messages.size());
			}

			result = sendMessage(url, message);
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
			throw new IOException("Could not broadcast message after " + attempt + " attempts");
		}
		return result;
	}

	public Result deleteScheduleJob(String jobId) throws IOException, ParseException {
		StringBuilder body = newBody("job_id", URLEncoder.encode(jobId, "UTF-8"));
		return execScheduleJobNoRetry(Constants.XMPUSH_SEND_ENDPOINT_DELETE_SCHEDULE_JOB, body);
	}

	public Result checkScheduleJobExist(String jobId) throws IOException, ParseException {
		StringBuilder body = newBody("job_id", URLEncoder.encode(jobId, "UTF-8"));
		return execScheduleJobNoRetry(Constants.XMPUSH_SEND_ENDPOINT_CHECK_SCHEDULE_JOB_EXIST, body);
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
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(responseBody);
			return new Result.Builder().fromJson(json);
		} catch (ParseException e) {
			logger.warn("Exception parsing response: ", e);
			throw new IOException("Invalid response from XmPush: " + responseBody);
		}
	}

	@SuppressWarnings("unchecked")
	private String toString(List<TargetedMessage> messages) {
		JSONArray jsonArray = new JSONArray();
		for (TargetedMessage message : messages) {
			JSONObject jsonMessage = new JSONObject();
			JSONObject msg = toJson(message.getMessage());
			tryAddJson(jsonMessage, "target", message.getTarget());
			tryAddJson(jsonMessage, "message", msg);
			jsonArray.add(jsonMessage);
		}
		return jsonArray.toString();
	}

	private JSONObject toJson(Message msg) {
		JSONObject json = new JSONObject();
		tryAddJson(json, "payload", msg.getPayload());
		tryAddJson(json, "title", msg.getTitle());
		tryAddJson(json, "description", msg.getDescription());
		tryAddJson(json, "notify_type", msg.getNotifyType());
		tryAddJson(json, "notify_id", msg.getNotifyId());
		tryAddJson(json, "pass_through", msg.getPassThrough());
		tryAddJson(json, "restricted_package_name", msg.getRestrictedPackageName());
		tryAddJson(json, "time_to_live", msg.getTimeToLive());
		tryAddJson(json, "collapse_key", msg.getCollapseKey());
		Map<String, String> extraInfo = msg.getExtra();
		if ((extraInfo != null) && (!extraInfo.isEmpty())) {
			JSONObject extraJson = new JSONObject();
			for (Map.Entry<String, String> entry : extraInfo.entrySet()) {
				tryAddJson(extraJson, (String) entry.getKey(), entry.getValue());
			}
			tryAddJson(json, "extra", extraJson);
		}
		return json;
	}

	@SuppressWarnings("unchecked")
	protected static void tryAddJson(JSONObject json, String parameterName, Object value) {
		if ((!XMStringUtils.isEmpty(parameterName)) && (value != null)) {
			json.put(parameterName, value);
		}
	}

	protected static final Map<String, String> newKeyValues(String key, String value) {
		Map<String, String> keyValues = new HashMap<String, String>(1);
		keyValues.put(nonNull(key), nonNull(value));
		return keyValues;
	}
}