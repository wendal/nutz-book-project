package com.xiaomi.xmpush.server;

public class Constants {
	public static final String SDK_VERSION = "2015.04.23";

	public static final long MAX_MESSAGE_LENGTH = 140L;

	public static final String XMPUSH_HOST_SANDBOX = "sandbox.xmpush.xiaomi.com";

	public static final String XMPUSH_HOST_PRODUCTION = "api.xmpush.xiaomi.com";

	public static final String XMPUSH_FEEDBACK_HOST_PRODUCTION = "feedback.xmpush.xiaomi.com";

	public static final String XMPUSH_SEND_ENDPOINT_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/send";

	public static final String XMPUSH_SEND_ENDPOINT_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/send";

	public static final String XMPUSH_SEND_ENDPOINT_REGID_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/message/regid";

	public static final String XMPUSH_SEND_ENDPOINT_REGID_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/message/regid";

	public static final String XMPUSH_SUBSCRIBE_TOPIC_PATH = "/v2/topic/subscribe";

	public static final String XMPUSH_UNSUBSCRIBE_TOPIC_PATH = "/v2/topic/unsubscribe";

	public static final String XMPUSH_DELETE_TOPIC_PATH = "/v2/message/delete";

	public static final String XMPUSH_SUBSCRIBE_TOPIC_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/topic/subscribe";

	public static final String XMPUSH_SUBSCRIBE_TOPIC_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/topic/subscribe";

	public static final String XMPUSH_UNSUBSCRIBE_TOPIC_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/topic/unsubscribe";

	public static final String XMPUSH_UNSUBSCRIBE_TOPIC_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/topic/unsubscribe";

	public static final String XMPUSH_DELETE_TOPIC_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/message/delete";

	public static final String XMPUSH_DELETE_TOPIC_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/message/delete";

	public static final String XMPUSH_SEND_ENDPOINT_ALIAS_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/message/alias";

	public static final String XMPUSH_SEND_ENDPOINT_ALIAS_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/message/alias";

	public static final String XMPUSH_SEND_ENDPOINT_USER_ACCOUNT_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/message/user_account";

	public static final String XMPUSH_SEND_ENDPOINT_USER_ACCOUNT_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/message/user_account";

	public static final String XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_ALIAS_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/multi_messages/aliases";

	public static final String XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_ALIAS_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/multi_messages/aliases";

	public static final String XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_USER_ACCOUNT_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/multi_messages/user_accounts";

	public static final String XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_USER_ACCOUNT_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/multi_messages/user_accounts";

	public static final String XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_REGID_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/multi_messages/regids";

	public static final String XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_REGID_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/multi_messages/regids";

	public static final String XMPUSH_SEND_ENDPOINT_TOPIC_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/message/topic";

	public static final String XMPUSH_SEND_ENDPOINT_MULTI_TOPIC_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/message/multi_topic";

	public static final String XMPUSH_SEND_ENDPOINT_ALL_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/message/all";

	public static final String XMPUSH_SEND_ENDPOINT_ALL_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/message/all";

	public static final String XMPUSH_SEND_ENDPOINT_TOPIC_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/message/topic";

	public static final String XMPUSH_SEND_ENDPOINT_MULTI_TOPIC_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/message/multi_topic";

	public static final String XMPUSH_SEND_ENDPOINT_DELETE_SCHEDULE_JOB_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/schedule_job/delete";

	public static final String XMPUSH_SEND_ENDPOINT_DELETE_SCHEDULE_JOB_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/schedule_job/delete";

	public static final String XMPUSH_SEND_ENDPOINT_CHECK_SCHEDULE_JOB_EXIST_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/schedule_job/exist";

	public static final String XMPUSH_SEND_ENDPOINT_QUERY_SCHEDULE_JOB_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/schedule_job/query";

	public static final String XMPUSH_SEND_ENDPOINT_CHECK_SCHEDULE_JOB_EXIST_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/schedule_job/exist";

	public static final String XMPUSH_SEND_ENDPOINT_QUERY_SCHEDULE_JOB_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/schedule_job/query";

	public static final String XMPUSH_STATS_PRODUCTION = "https://api.xmpush.xiaomi.com/v1/stats/message/counters";

	public static final String XMPUSH_STATS_SANBOX = "https://sandbox.xmpush.xiaomi.com/v1/stats/message/counters";

	public static final String XMPUSH_MESSAGE_TRACE_PRODUCTION = "https://api.xmpush.xiaomi.com/v1/trace/message/status";

	public static final String XMPUSH_MESSAGE_TRACE_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v1/trace/message/status";

	public static final String XMPUSH_MESSAGES_TRACE_PRODUCTION = "https://api.xmpush.xiaomi.com/v1/trace/messages/status";

	public static final String XMPUSH_MESSAGES_TRACE_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v1/trace/messages/status";

	public static final String XMPUSH_VALIDATE_REGID_PRODUCTION = "https://api.xmpush.xiaomi.com/v1/validation/regids";

	public static final String XMPUSH_VALIDATE_REGID_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v1/validation/regids";

	public static final String XMPUSH_FETCH_INVALID_REGIDS_PRODUCTION = "https://feedback.xmpush.xiaomi.com/v1/feedback/fetch_invalid_regids";

	public static final String XMPUSH_FETCH_INVALID_REGIDS_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v1/feedback/fetch_invalid_regids";

	public static final String XMPUSH_GET_ALIASES_OF_DEVICE_PRODUCTION = "https://api.xmpush.xiaomi.com/v1/alias/all";

	public static final String XMPUSH_GET_ALIASES_OF_DEVICE_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v1/alias/all";

	public static final String XMPUSH_GET_TOPICS_OF_DEVICE_PRODUCTION = "https://api.xmpush.xiaomi.com/v1/topic/all";

	public static final String XMPUSH_GET_TOPICS_OF_DEVICE_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v1/topic/all";

	public static final String XMPUSH_GET_ACCOUNTS_OF_DEVICE_PRODUCTION = "https://api.xmpush.xiaomi.com/v1/account/all";

	public static final String XMPUSH_GET_ACCOUNTS_OF_DEVICE_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v1/account/all";

	public static final String XMPUSH_SUBSCRIBE_TOPIC_BY_ALIAS_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/topic/subscribe/alias";

	public static final String XMPUSH_SUBSCRIBE_TOPIC_BY_ALIAS_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/topic/subscribe/alias";

	public static final String XMPUSH_UNSUBSCRIBE_TOPIC_BY_ALIAS_SANDBOX = "https://sandbox.xmpush.xiaomi.com/v2/topic/unsubscribe/alias";

	public static final String XMPUSH_UNSUBSCRIBE_TOPIC_BY_ALIAS_PRODUCTION = "https://api.xmpush.xiaomi.com/v2/topic/unsubscribe/alias";

	public static String XMPUSH_SUBSCRIBE_TOPIC_BY_ALIAS = "https://api.xmpush.xiaomi.com/v2/topic/subscribe/alias";

	public static String XMPUSH_UNSUBSCRIBE_TOPIC_BY_ALIAS = "https://api.xmpush.xiaomi.com/v2/topic/unsubscribe/alias";

	public static String XMPUSH_HOST = "api.xmpush.xiaomi.com";

	public static String XMPUSH_SEND_ENDPOINT = "https://api.xmpush.xiaomi.com/v2/send";

	public static String XMPUSH_SEND_ENDPOINT_REGID = "https://api.xmpush.xiaomi.com/v2/message/regid";

	public static String XMPUSH_ENDPOINT_SUBSCRIPTION = "https://api.xmpush.xiaomi.com/v2/topic/subscribe";

	public static String XMPUSH_ENDPOINT_UNSUBSCRIPTION = "https://api.xmpush.xiaomi.com/v2/topic/unsubscribe";

	public static String XMPUSH_ENDPOINT_DELETE_TOPIC = "https://api.xmpush.xiaomi.com/v2/message/delete";

	public static String XMPUSH_SEND_ENDPOINT_ALIAS = "https://api.xmpush.xiaomi.com/v2/message/alias";

	public static String XMPUSH_SEND_ENDPOINT_USER_ACCOUNT = "https://api.xmpush.xiaomi.com/v2/message/user_account";

	public static String XMPUSH_SEND_ENDPOINT_TOPIC = "https://api.xmpush.xiaomi.com/v2/message/topic";

	public static String XMPUSH_SEND_ENDPOINT_MULTI_TOPIC = "https://api.xmpush.xiaomi.com/v2/message/multi_topic";

	public static String XMPUSH_SEND_ENDPOINT_ALL = "https://api.xmpush.xiaomi.com/v2/message/all";

	public static String XMPUSH_SEND_ENDPOINT_DELETE_SCHEDULE_JOB = "https://api.xmpush.xiaomi.com/v2/schedule_job/delete";

	public static String XMPUSH_SEND_ENDPOINT_CHECK_SCHEDULE_JOB_EXIST = "https://api.xmpush.xiaomi.com/v2/schedule_job/exist";

	public static String XMPUSH_SEND_ENDPOINT_QUERY_SCHEDULE_JOB = "https://api.xmpush.xiaomi.com/v2/schedule_job/query";

	public static String XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_ALIAS = "https://api.xmpush.xiaomi.com/v2/multi_messages/aliases";

	public static String XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_USER_ACCOUNT = "https://api.xmpush.xiaomi.com/v2/multi_messages/user_accounts";

	public static String XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_REGID = "https://api.xmpush.xiaomi.com/v2/multi_messages/regids";

	public static String XMPUSH_STATS = "https://api.xmpush.xiaomi.com/v1/stats/message/counters";

	public static String XMPUSH_MESSAGE_TRACE = "https://api.xmpush.xiaomi.com/v1/trace/message/status";

	public static String XMPUSH_MESSAGES_TRACE = "https://api.xmpush.xiaomi.com/v1/trace/messages/status";

	public static String XMPUSH_VALIDATE_REGID = "https://api.xmpush.xiaomi.com/v1/validation/regids";

	public static String XMPUSH_FETCH_INVALID_REGIDS = "https://feedback.xmpush.xiaomi.com/v1/feedback/fetch_invalid_regids";

	public static String XMPUSH_GET_ALIASES_OF_DEVICE = "https://api.xmpush.xiaomi.com/v1/alias/all";

	public static String XMPUSH_GET_TOPICS_OF_DEVICE = "https://api.xmpush.xiaomi.com/v1/topic/all";

	public static String XMPUSH_GET_ACCOUNTS_OF_DEVICE = "https://api.xmpush.xiaomi.com/v1/account/all";

	public static final String PARAM_REGISTRATION_ID = "registration_id";

	public static final String PARAM_COLLAPSE_KEY = "collapse_key";

	public static final String PARAM_PAYLOAD = "payload";

	public static final String PARAM_TOPIC = "topic";

	public static final String PARAM_ALIAS = "alias";

	public static final String PARAM_ALIASES = "aliases";

	public static final String PARAM_USER_ACCOUNT = "user_account";

	public static final String PARAM_TITLE = "title";

	public static final String PARAM_DESCRIPTION = "description";

	public static final String PARAM_NOTIFY_TYPE = "notify_type";

	public static final String PARAM_NOTIFY_ID = "notify_id";

	public static final String PARAM_TIMER_TO_SEND = "time_to_send";

	public static final String PARAM_URL = "url";

	public static final String PARAM_PASS_THROUGH = "pass_through";

	public static final String PARAM_MESSAGES = "messages";

	public static final String PARAM_NAME_EXTRA_PREFIX = "extra.";

	public static final String PARAM_CATEGORY = "category";

	public static final String PARAM_JOB_ID = "job_id";

	public static final String PARAM_TOPICS = "topics";

	public static final String PARAM_TOPIC_OP = "topic_op";

	public static final String PARAM_APPID = "app_id";

	public static final String PARAM_START_TS = "start_time";

	public static final String PARAM_END_TS = "end_time";

	public static final String PARAM_JOB_TYPE = "type";

	public static final String PARAM_MAX_COUNT = "max_count";

	public static final String EXTRA_PARAM_SOUND_URI = "sound_uri";

	public static final String EXTRA_PARAM_NOTIFY_EFFECT = "notify_effect";

	public static final String NOTIFY_LAUNCHER_ACTIVITY = "1";

	public static final String NOTIFY_ACTIVITY = "2";

	public static final String NOTIFY_WEB = "3";

	public static final String EXTRA_PARAM_INTENT_URI = "intent_uri";

	public static final String EXTRA_PARAM_WEB_URI = "web_uri";

	public static final String EXTRA_PARAM_NOTIFICATION_TICKER = "ticker";

	public static final String EXTRA_PARAM_CLASS_NAME = "class_name";

	public static final String EXTRA_PARAM_INTENT_FLAG = "intent_flag";

	public static final String EXTRA_PARAM_NOTIFY_FOREGROUND = "notify_foreground";

	public static final String EXTRA_PARAM_ALERT_TITLE = "apsAlert-title";

	public static final String EXTRA_PARAM_ALERT_BODY = "apsAlert-body";

	public static final String EXTRA_PARAM_ALERT_TITLE_LOC_KEY = "apsAlert-title-loc-key";

	public static final String EXTRA_PARAM_ALERT_TITLE_LOC_ARGS = "apsAlert-title-loc-args";

	public static final String EXTRA_PARAM_ALERT_ACTION_LOC_KEY = "apsAlert-action-loc-key";

	public static final String EXTRA_PARAM_ALERT_LOC_KEY = "apsAlert-loc-key";

	public static final String EXTRA_PARAM_ALERT_LOC_ARGS = "apsAlert-loc-args";

	public static final String EXTRA_PARAM_ALERT_LAUNCH_IMAGE = "apsAlert-launch-image";

	public static final String PARAM_DELAY_WHILE_IDLE = "delay_while_idle";

	public static final String PARAM_DRY_RUN = "dry_run";

	public static final String PARAM_RESTRICTED_PACKAGE_NAME = "restricted_package_name";

	public static final String PARAM_PAYLOAD_PREFIX = "data.";

	public static final String PARAM_TIME_TO_LIVE = "time_to_live";

	public static final String ERROR_QUOTA_EXCEEDED = "QuotaExceeded";

	public static final String ERROR_DEVICE_QUOTA_EXCEEDED = "DeviceQuotaExceeded";

	public static final String ERROR_MISSING_REGISTRATION = "MissingRegistration";

	public static final String ERROR_INVALID_REGISTRATION = "InvalidRegistration";

	public static final String ERROR_MISMATCH_SENDER_ID = "MismatchSenderId";

	public static final String ERROR_NOT_REGISTERED = "NotRegistered";

	public static final String ERROR_MESSAGE_TOO_BIG = "MessageTooBig";

	public static final String ERROR_MISSING_COLLAPSE_KEY = "MissingCollapseKey";

	public static final String ERROR_UNAVAILABLE = "Unavailable";

	public static final String ERROR_INTERNAL_SERVER_ERROR = "InternalServerError";

	public static final String ERROR_INVALID_TTL = "InvalidTtl";

	public static final String TOKEN_MESSAGE_ID = "id";

	public static final String TOKEN_CANONICAL_REG_ID = "registration_id";

	public static final String TOKEN_ERROR = "Error";

	public static final String REGISTRATION_IDS = "registration_ids";

	public static final String JSON_PAYLOAD = "data";

	public static final String JSON_SUCCESS = "success";

	public static final String JSON_FAILURE = "failure";

	public static final String JSON_MULTICAST_ID = "multicast_id";

	public static final String JSON_RESULTS = "results";

	public static final String JSON_ERROR = "error";

	public static final String JSON_MESSAGE_ID = "message_id";

	public static final String PARAM_START_DATE = "start_date";

	public static final String PARAM_END_DATE = "end_date";

	public static final String TRACE_BEGIN_TIME = "begin_time";

	public static final String TRACE_END_TIME = "end_time";

	public static final String TRACE_MSG_ID = "msg_id";

	public static final String TRACE_JOB_KEY = "job_key";

	protected Constants() {
		throw new UnsupportedOperationException();
	}

	protected static void selectVersion(boolean sandbox) {
		if (sandbox) {
			XMPUSH_HOST = "sandbox.xmpush.xiaomi.com";
			XMPUSH_SEND_ENDPOINT = "https://sandbox.xmpush.xiaomi.com/v2/send";
			XMPUSH_SEND_ENDPOINT_REGID = "https://sandbox.xmpush.xiaomi.com/v2/message/regid";
			XMPUSH_SEND_ENDPOINT_ALIAS = "https://sandbox.xmpush.xiaomi.com/v2/message/alias";
			XMPUSH_SEND_ENDPOINT_USER_ACCOUNT = "https://sandbox.xmpush.xiaomi.com/v2/message/user_account";
			XMPUSH_SEND_ENDPOINT_TOPIC = "https://sandbox.xmpush.xiaomi.com/v2/message/topic";
			XMPUSH_SEND_ENDPOINT_MULTI_TOPIC = "https://sandbox.xmpush.xiaomi.com/v2/message/multi_topic";
			XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_ALIAS = "https://sandbox.xmpush.xiaomi.com/v2/multi_messages/aliases";
			XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_USER_ACCOUNT = "https://sandbox.xmpush.xiaomi.com/v2/multi_messages/user_accounts";
			XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_REGID = "https://sandbox.xmpush.xiaomi.com/v2/multi_messages/regids";
			XMPUSH_SEND_ENDPOINT_ALL = "https://sandbox.xmpush.xiaomi.com/v2/message/all";
			XMPUSH_SEND_ENDPOINT_DELETE_SCHEDULE_JOB = "https://sandbox.xmpush.xiaomi.com/v2/schedule_job/delete";
			XMPUSH_SEND_ENDPOINT_CHECK_SCHEDULE_JOB_EXIST = "https://sandbox.xmpush.xiaomi.com/v2/schedule_job/exist";
			XMPUSH_SEND_ENDPOINT_QUERY_SCHEDULE_JOB = "https://sandbox.xmpush.xiaomi.com/v2/schedule_job/query";
			XMPUSH_STATS = "https://sandbox.xmpush.xiaomi.com/v1/stats/message/counters";
			XMPUSH_MESSAGE_TRACE = "https://sandbox.xmpush.xiaomi.com/v1/trace/message/status";
			XMPUSH_MESSAGES_TRACE = "https://sandbox.xmpush.xiaomi.com/v1/trace/messages/status";
			XMPUSH_VALIDATE_REGID = "https://sandbox.xmpush.xiaomi.com/v1/validation/regids";
			XMPUSH_ENDPOINT_SUBSCRIPTION = "https://sandbox.xmpush.xiaomi.com/v2/topic/subscribe";
			XMPUSH_ENDPOINT_UNSUBSCRIPTION = "https://sandbox.xmpush.xiaomi.com/v2/topic/unsubscribe";
			XMPUSH_ENDPOINT_DELETE_TOPIC = "https://sandbox.xmpush.xiaomi.com/v2/message/delete";
			XMPUSH_FETCH_INVALID_REGIDS = "https://sandbox.xmpush.xiaomi.com/v1/feedback/fetch_invalid_regids";
			XMPUSH_SUBSCRIBE_TOPIC_BY_ALIAS = "https://sandbox.xmpush.xiaomi.com/v2/topic/subscribe/alias";
			XMPUSH_UNSUBSCRIBE_TOPIC_BY_ALIAS = "https://sandbox.xmpush.xiaomi.com/v2/topic/unsubscribe/alias";
			XMPUSH_GET_ALIASES_OF_DEVICE = "https://sandbox.xmpush.xiaomi.com/v1/alias/all";
			XMPUSH_GET_TOPICS_OF_DEVICE = "https://sandbox.xmpush.xiaomi.com/v1/topic/all";
		} else {
			XMPUSH_HOST = "api.xmpush.xiaomi.com";
			XMPUSH_SEND_ENDPOINT = "https://api.xmpush.xiaomi.com/v2/send";
			XMPUSH_SEND_ENDPOINT_REGID = "https://api.xmpush.xiaomi.com/v2/message/regid";
			XMPUSH_SEND_ENDPOINT_ALIAS = "https://api.xmpush.xiaomi.com/v2/message/alias";
			XMPUSH_SEND_ENDPOINT_USER_ACCOUNT = "https://api.xmpush.xiaomi.com/v2/message/user_account";
			XMPUSH_SEND_ENDPOINT_TOPIC = "https://api.xmpush.xiaomi.com/v2/message/topic";
			XMPUSH_SEND_ENDPOINT_MULTI_TOPIC = "https://api.xmpush.xiaomi.com/v2/message/multi_topic";
			XMPUSH_SEND_ENDPOINT_ALL = "https://api.xmpush.xiaomi.com/v2/message/all";
			XMPUSH_SEND_ENDPOINT_DELETE_SCHEDULE_JOB = "https://api.xmpush.xiaomi.com/v2/schedule_job/delete";
			XMPUSH_SEND_ENDPOINT_CHECK_SCHEDULE_JOB_EXIST = "https://api.xmpush.xiaomi.com/v2/schedule_job/exist";
			XMPUSH_SEND_ENDPOINT_QUERY_SCHEDULE_JOB = "https://api.xmpush.xiaomi.com/v2/schedule_job/query";
			XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_ALIAS = "https://api.xmpush.xiaomi.com/v2/multi_messages/aliases";
			XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_USER_ACCOUNT = "https://api.xmpush.xiaomi.com/v2/multi_messages/user_accounts";
			XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_REGID = "https://api.xmpush.xiaomi.com/v2/multi_messages/regids";
			XMPUSH_SEND_ENDPOINT_ALL = "https://api.xmpush.xiaomi.com/v2/message/all";
			XMPUSH_STATS = "https://api.xmpush.xiaomi.com/v1/stats/message/counters";
			XMPUSH_MESSAGE_TRACE = "https://api.xmpush.xiaomi.com/v1/trace/message/status";
			XMPUSH_MESSAGES_TRACE = "https://api.xmpush.xiaomi.com/v1/trace/messages/status";
			XMPUSH_VALIDATE_REGID = "https://api.xmpush.xiaomi.com/v1/validation/regids";
			XMPUSH_ENDPOINT_SUBSCRIPTION = "https://api.xmpush.xiaomi.com/v2/topic/subscribe";
			XMPUSH_ENDPOINT_UNSUBSCRIPTION = "https://api.xmpush.xiaomi.com/v2/topic/unsubscribe";
			XMPUSH_ENDPOINT_DELETE_TOPIC = "https://api.xmpush.xiaomi.com/v2/message/delete";
			XMPUSH_FETCH_INVALID_REGIDS = "https://feedback.xmpush.xiaomi.com/v1/feedback/fetch_invalid_regids";
			XMPUSH_SUBSCRIBE_TOPIC_BY_ALIAS = "https://api.xmpush.xiaomi.com/v2/topic/subscribe/alias";
			XMPUSH_UNSUBSCRIBE_TOPIC_BY_ALIAS = "https://api.xmpush.xiaomi.com/v2/topic/unsubscribe/alias";
			XMPUSH_GET_ALIASES_OF_DEVICE = "https://api.xmpush.xiaomi.com/v1/alias/all";
			XMPUSH_GET_TOPICS_OF_DEVICE = "https://api.xmpush.xiaomi.com/v1/topic/all";
		}
	}

	public static void useSandbox() {
		selectVersion(true);
	}

	public static void useInternalHost(String host) {
		XMPUSH_HOST = host;
		XMPUSH_SEND_ENDPOINT = "http://" + host + "/v2/send";
		XMPUSH_SEND_ENDPOINT_REGID = "http://" + host + "/v2/message/regid";
		XMPUSH_SEND_ENDPOINT_ALIAS = "http://" + host + "/v2/message/alias";
		XMPUSH_SEND_ENDPOINT_USER_ACCOUNT = "http://" + host + "/v2/message/user_account";
		XMPUSH_SEND_ENDPOINT_TOPIC = "http://" + host + "/v2/message/topic";
		XMPUSH_SEND_ENDPOINT_MULTI_TOPIC = "http://" + host + "/v2/message/multi_topic";
		XMPUSH_SEND_ENDPOINT_ALL = "http://" + host + "/v2/message/all";
		XMPUSH_SEND_ENDPOINT_DELETE_SCHEDULE_JOB = "http://" + host + "/v2/schedule_job/delete";
		XMPUSH_SEND_ENDPOINT_CHECK_SCHEDULE_JOB_EXIST = "http://" + host + "/v2/schedule_job/exist";
		XMPUSH_SEND_ENDPOINT_QUERY_SCHEDULE_JOB = "https://" + host + "/v2/schedule_job/query";
		XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_ALIAS = "http://" + host + "/v2/multi_messages/aliases";
		XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_USER_ACCOUNT = "http://" + host + "/v2/multi_messages/user_accounts";
		XMPUSH_SEND_ENDPOINT_MULTI_MESSAGE_REGID = "http://" + host + "/v2/multi_messages/regids";
		XMPUSH_STATS = "http://" + host + "/v1/stats/message/counters";
		XMPUSH_ENDPOINT_SUBSCRIPTION = "http://" + host + "/v2/topic/subscribe";
		XMPUSH_ENDPOINT_UNSUBSCRIPTION = "http://" + host + "/v2/topic/unsubscribe";
		XMPUSH_ENDPOINT_DELETE_TOPIC = "http://" + host + "/v2/message/delete";
		XMPUSH_MESSAGE_TRACE = "http://" + host + "/v1/trace/message/status";
		XMPUSH_MESSAGES_TRACE = "http://" + host + "/v1/trace/messages/status";
	}

	public static void useOfficial() {
		selectVersion(false);
	}
}