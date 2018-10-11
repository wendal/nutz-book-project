package net.wendal.nutzbook.aliyuniot.service;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.segment.Segments;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.repo.Base64;

import com.aliyun.openservices.iot.api.Profile;
import com.aliyun.openservices.iot.api.message.MessageClientFactory;
import com.aliyun.openservices.iot.api.message.api.MessageClient;
import com.aliyun.openservices.iot.api.message.callback.ConnectionCallback;
import com.aliyun.openservices.iot.api.message.callback.MessageCallback;
import com.aliyun.openservices.iot.api.message.entity.Message;
import com.aliyun.openservices.iot.api.message.entity.MessageToken;

import net.wendal.nutzbook.aliyuniot.bean.AliyunDev;
import net.wendal.nutzbook.aliyuniot.bean.AliyunDevMessage;
import net.wendal.nutzbook.aliyuniot.bean.AliyunDevStatusLog;

@IocBean
public class AliyunIotService implements MessageCallback {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected PropertiesProxy conf;
    
    @Inject
    protected Dao dao;
    
    protected  MessageClient client;

    public boolean isListening() {
        return client != null && client.isConnected();
    }
    
    public boolean startListen() {
        if (isListening())
            return false;
        // 阿里云accessKey
        String accessKey = conf.get("aliyuniot.accessKey");
        // 阿里云accessSecret
        String accessSecret = conf.get("aliyuniot.accessSecret");
        // regionId
        String regionId = conf.get("aliyuniot.regionId", "cn-shanghai");
        // 阿里云uid
        String uid = conf.get("aliyuniot.uid");
        String productKey = conf.get("aliyuniot.productKey");

        if (Strings.isBlank(accessKey)) {
            log.info("未设置aliyuniot.accessKey");
            return false;
        }
        if (Strings.isBlank(accessSecret)) {
            log.info("未设置aliyuniot.accessSecret");
            return false;
        }
        if (Strings.isBlank(regionId)) {
            log.info("未设置aliyuniot.regionId");
            return false;
        }
        if (Strings.isBlank(uid)) {
            log.info("未设置aliyuniot.uid");
            return false;
        }
        if (Strings.isBlank(productKey)) {
            log.info("未设置aliyuniot.productKey");
            return false;
        }
        
        // endPoint:  https://${uid}.iot-as-http2.${region}.aliyuncs.com
        String endPoint = "https://" + uid + ".iot-as-http2." + regionId + ".aliyuncs.com";
        Profile profile = Profile.getAccessKeyProfile(endPoint, regionId, accessKey, accessSecret);
        profile.setMultiConnection(false);
        client = MessageClientFactory.messageClient(profile);
        client.setMessageListener("/as/mqtt/status/"+productKey+"/#", this);
        client.setMessageListener("/"+productKey+"/#", this);
        client.connect(new MessageCallback() {
            public Action consume(MessageToken messageToken) {
                Message m = messageToken.getMessage();
                log.info("receive message from " + m);
                return MessageCallback.Action.CommitSuccess;
            }
        });
        return true;
    }
    
    public boolean stopListen() {
        if (!isListening())
            return false;
        client.disconnect();
        client = null;
        return true;
    }


    public Action consume(MessageToken messageToken) {
        Message m = messageToken.getMessage();
        log.info("receive message from " + m);
        String topic = m.getTopic();
        // 如果topic是/as/mqtt/status/开头,那就是状态报告
        try {
            if (topic.startsWith("/as/mqtt/status/")) {
                NutMap payload = Json.fromJson(NutMap.class, new String(m.getPayload()));
                /*
                 *      {"lastTime":"2018-10-02 00:28:52.274",
                 *      "utcLastTime":"2018-10-01T16:28:52.274Z",
                 *      "clientIp":"111.206.162.6",
                 *      "utcTime":"2018-10-01T16:28:52.307Z",
                 *      "time":"2018-10-02 00:28:52.307",
                 *      "productKey":"a1eAGqw0Tfi",
                 *      "deviceName":"869300035495617",
                 *      "status":"online"}
                 */
                log.debug(Segments.create("设备状态报告 ${productKey} ${deviceName} ${status}").render(Lang.context(payload)));
                // 首先,查一下有没有这个设备
                boolean online = "online".equals(payload.get("status"));
                long time = Times.parse("yyyy-MM-dd HH:mm:ss.SSS", payload.getString("time")).getTime();
                AliyunDev dev = getOrCreate(payload.getString("productKey"), payload.getString("deviceName"));
                // 插入记录
                AliyunDevStatusLog stat = new AliyunDevStatusLog();
                stat.setDeviceId(dev.getId());
                stat.setOnline(online);
                stat.setTime(time);
                dao.insert(stat);
                dao.update(AliyunDev.class, Chain.make("online", online).add("onlineTime", time), Cnd.where("id", "=", dev.getId()).and("onlineTime", "<", time));
                // 触发online/offline脚本
            }
            else {
                String[] tmp = Strings.splitIgnoreBlank(topic, "/");
                String productKey = tmp[0];
                String deviceName = tmp[1];
                Cnd cnd = Cnd.where("productKey", "=", productKey);
                cnd.and("deviceName", "=", deviceName);
                AliyunDev dev = getOrCreate(productKey, deviceName);
                AliyunDevMessage msg = new AliyunDevMessage();
                msg.setDeviceId(dev.getId());
                msg.setTopic(topic);
                msg.setQos(m.getQos());
                msg.setDir(0);
                msg.setTime(m.getGenerateTime());
                msg.setCnt(Base64.encodeToString(m.getPayload(), false));
                dao.insert(msg);
            }
        }
        catch (Throwable e) {
            log.info("bug?", e);
        }
        return MessageCallback.Action.CommitSuccess;
    }
    
    public AliyunDev getOrCreate(String productKey, String deviceName) {
        Cnd cnd = Cnd.where("productKey", "=", productKey);
        cnd.and("deviceName", "=", deviceName);
        AliyunDev dev = dao.fetch(AliyunDev.class, cnd);
        if (dev == null) {
            dev = new AliyunDev();
            dev.setProductKey(productKey);
            dev.setDeviceName(deviceName);
            dev.setCreateAt(System.currentTimeMillis());
            dev.setOnlineTime(0);
            try {
                dao.insert(dev);
            }
            catch (Throwable e) {
                log.debug("似乎其他线程已经插入了记录,那直接查询吧 : " + cnd);
                dev = dao.fetch(AliyunDev.class, cnd);
            }
        }
        return dev;
    }
    
    public AliyunDev insertAliyunDev(String productKey, String deviceName, String deviceSecret, String aliyunId, boolean online) {
        AliyunDev dev = new AliyunDev();
        dev.setProductKey(productKey);
        dev.setDeviceName(deviceName);
        dev.setDeviceSecret(deviceSecret);
        dev.setAliyunId(aliyunId);
        dev.setOnline(online);
        dev.setOnlineTime(System.currentTimeMillis());
        dev.setCreateAt(System.currentTimeMillis());
        dao.insert(dev);
        return dev;
    }
}
