package com.xiaomi.push.sdk;

import java.util.Collection;
import java.util.HashMap;

public class ErrorCode {
	private int value;
	private String description;
	private static HashMap<Integer, ErrorCode> intErrorCodeMap = new HashMap<>();

	public static Collection<ErrorCode> getAllErrorCodes() {
		return intErrorCodeMap.values();
	}

	private ErrorCode(int value) {
		this.value = value;
	}

	private ErrorCode(int value, String description) {
		this.value = value;
		this.description = description;
	}

	public String toString() {
		return super.toString();
	}

	public String getFullDescription() {
		return getName() + "," + this.value + "," + this.description;
	}

	public String getName() {
		return this.description;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static ErrorCode valueOf(int value) {
		return (ErrorCode) intErrorCodeMap.get(Integer.valueOf(value));
	}

	public static ErrorCode valueOf(int value, ErrorCode defaultIfMissing) {
		ErrorCode code = (ErrorCode) intErrorCodeMap.get(Integer.valueOf(value));
		if (code == null) {
			return defaultIfMissing;
		}
		return code;
	}

	public static ErrorCode valueOf(Integer code, String reason) {
		ErrorCode result = (ErrorCode) intErrorCodeMap.get(code);
		if (result == null) {
			result = new ErrorCode(code.intValue(), reason);
			intErrorCodeMap.put(code, result);
		}
		return result;
	}

	public static ErrorCode UnknowError = valueOf(Integer.valueOf(-1), "未知错误");
	public static ErrorCode Success = valueOf(Integer.valueOf(0), "成功");
	public static ErrorCode NotBlank = valueOf(Integer.valueOf(100), "参数不能为空");

	public static ErrorCode SystemError = valueOf(Integer.valueOf(10001), "系统错误");
	public static ErrorCode ServiceUnavailable = valueOf(Integer.valueOf(10002), "服务暂停");
	public static ErrorCode RemoteServiceError = valueOf(Integer.valueOf(10003), "远程服务错误");
	public static ErrorCode IpLimit = valueOf(Integer.valueOf(10004), "IP限制不能请求该资源");
	public static ErrorCode PermissionDenied = valueOf(Integer.valueOf(10005), "该资源需要appkey拥有授权");
	public static ErrorCode MissAppKey = valueOf(Integer.valueOf(10006), "缺少appkey参数");
	public static ErrorCode UnsupportMimeType = valueOf(Integer.valueOf(10007), "不支持的 mime type");
	public static ErrorCode ParameterError = valueOf(Integer.valueOf(10008), "参数错误，请参考API文档");
	public static ErrorCode SystemIsBusy = valueOf(Integer.valueOf(10009), "系统繁忙");
	public static ErrorCode JobExpired = valueOf(Integer.valueOf(10010), "任务超时");
	public static ErrorCode RpcError = valueOf(Integer.valueOf(10011), "RPC错误");
	public static ErrorCode IllegalRequest = valueOf(Integer.valueOf(10012), "非法请求");
	public static ErrorCode InvalidUser = valueOf(Integer.valueOf(10013), "不合法的用户");
	public static ErrorCode InsufficientPermissions = valueOf(Integer.valueOf(10014), "应用的接口访问权限受限");
	public static ErrorCode MissRequiredParameter = valueOf(Integer.valueOf(10016), "缺失必选参数");
	public static ErrorCode InvalidParameterValue = valueOf(Integer.valueOf(10017), "参数值非法");
	public static ErrorCode RequestBodyLengthTooLong = valueOf(Integer.valueOf(10018), "请求长度超过限制");
	public static ErrorCode RequestApiNotFound = valueOf(Integer.valueOf(10020), "接口不存在");
	public static ErrorCode HttpMethodUnsupport = valueOf(Integer.valueOf(10021), "请求的HTTP方法不支持");
	public static ErrorCode IpRequestExceedQuota = valueOf(Integer.valueOf(10022), "IP请求频次超过上限");
	public static ErrorCode UserRequestExceedQuota = valueOf(Integer.valueOf(10023), "用户请求频次超过上限");
	public static ErrorCode UserRequestApiExceedQuota = valueOf(Integer.valueOf(10024), "用户请求特殊接口频次超过上限");
	public static ErrorCode InvalidCallbackUrl = valueOf(Integer.valueOf(10025), "Callback连接不合法");
	public static ErrorCode ApplicationInBlacklist = valueOf(Integer.valueOf(10026), "应用被加入黑名单，不能调用API");
	public static ErrorCode ApplicationApiCallExceedQuota = valueOf(Integer.valueOf(10027), "应用的API调用太频繁");
	public static ErrorCode ApplicationTotalApiCallExceedQuota = valueOf(Integer.valueOf(10028), "应用API调用总数太频繁");
	public static ErrorCode InvalidDevice = valueOf(Integer.valueOf(10029), "不合法的设备");

	public static ErrorCode ApplicationIllegal = valueOf(Integer.valueOf(22000), "非法应用");
	public static ErrorCode ApplicationNotExists = valueOf(Integer.valueOf(22001), "应用不存在");
	public static ErrorCode ApplicationRevoked = valueOf(Integer.valueOf(22002), "应用已经撤销");
	public static ErrorCode UpdateApplicationInfoFail = valueOf(Integer.valueOf(22003), "更新应用程序失败");
	public static ErrorCode MissApplicationInfo = valueOf(Integer.valueOf(22004), "缺少应用程序信息");
	public static ErrorCode InvalidApplicationName = valueOf(Integer.valueOf(22005), "应用程序名字不合法");
	public static ErrorCode InvalidApplicationId = valueOf(Integer.valueOf(22006), "应用程序Id不合法");
	public static ErrorCode InvalidApplicationKey = valueOf(Integer.valueOf(22007), "应用程序Key不合法");
	public static ErrorCode InvalidApplicationSecret = valueOf(Integer.valueOf(22008), "应用程序Secret不合法");
	public static ErrorCode InvalidApplicationDescription = valueOf(Integer.valueOf(22020), "应用程序描述信息不合法");
	public static ErrorCode UserNotAuthorizeApplication = valueOf(Integer.valueOf(22021), "用户没有授权给应用程序");
	public static ErrorCode InvalidPackageName = valueOf(Integer.valueOf(22022), "应用程序package name不合法");

	public static ErrorCode InvalidApplicationNotificationFormat = valueOf(Integer.valueOf(22100), "应用通知数据格式不合法");
	public static ErrorCode TooManyApplicationNotification = valueOf(Integer.valueOf(22101), "太多应用通知消息");
	public static ErrorCode SendApplicationNotificationFail = valueOf(Integer.valueOf(22102), "发送应用通知消息失败");
	public static ErrorCode InvalidNotifyId = valueOf(Integer.valueOf(22103), "应用通知ID不合法");
}
