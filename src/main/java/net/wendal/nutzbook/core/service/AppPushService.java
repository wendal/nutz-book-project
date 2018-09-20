package net.wendal.nutzbook.core.service;

import java.util.Map;

import com.xiaomi.xmpush.server.Message;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.model.PushPayload;

public interface AppPushService {

    /**
     * 通知类型: 帖子被回复
     */
    int PUSH_TYPE_REPLY = 0;
    /**
     * 通知类型: 被at
     */
    int PUSH_TYPE_AT = 1;

    void alert(long userId, String alert, String description, Map<String, String> extras);

    void message(long userId, String message, Map<String, String> extras);

    void doJpush(String name, JPushClient jpush, PushPayload payload);

    void doMxPush(Message message, String alias);
    
    void reload();

}