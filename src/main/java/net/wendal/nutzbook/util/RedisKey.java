package net.wendal.nutzbook.util;


/**
 * 统一存在redis用到的key
 * @author wendal
 *
 */
public interface RedisKey {

	// 用户相关
	String RKEY_USER_ACCESSTOKEN = "u:accesstoken";
	String RKEY_USER_ACCESSTOKEN2 = "u:accesstoken2";
	String RKEY_USER_ACCESSTOKEN3 = "u:accesstoken3";
	String RKEY_USER_SCORE = "u:score";
	String RKEY_USER_LVTIME = "u:lvtime";

	// 帖子相关
	String RKEY_TOPIC_VISIT = "t:visit";
	String RKEY_TOPIC_UPDATE = "t:update:";
	String RKEY_TOPIC_UPDATE_ALL = "t:update:all";
	String RKEY_TOPIC_TOP = "t:top";
	String RKEY_TOPIC_NOREPLY = "t:noreply";
	String RKEY_REPLY_LIKE = "t:like:";
	String RKEY_REPLY_COUNT = "t:reply:count";
	String RKEY_REPLY_LAST = "t:reply:last";
	
	// 标签相关
	String RKEY_TOPIC_TAG = "t:tag:";
	String RKEY_TOPIC_TAG_COUNT = "t:tag_count";
	String RKEY_TOPIC_TAG_UPDATE = "t:tag_update";
	
	// 收藏
	String RKEY_TOPIC_MARK = "t:mark:";
	String RKEY_USER_TOPIC_MARK = "u:mark:";

	// 活跃用户统计
	String RKEY_ONLINE_DAY = "online:day:";
}
