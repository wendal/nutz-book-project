package net.wendal.nutzbook.aliyuniot.jsobj;

import java.util.concurrent.ConcurrentHashMap;

public class JsKeyValueObject {

    protected ConcurrentHashMap<String, String> kv = new ConcurrentHashMap<>();
    
    // 基本KV支持
    public void kv_set(String key, String value) {
        if (key == null)
            return;
        if (value == null)
            kv.remove(key);
        else
            kv.put(key, value);
    }
    
    public String kv_get(String key) {
        if (key == null)
            return null;
        return kv.get(key);
    }
}
