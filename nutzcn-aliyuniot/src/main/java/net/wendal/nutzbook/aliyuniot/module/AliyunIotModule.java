package net.wendal.nutzbook.aliyuniot.module;

import java.util.Base64;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.iot.model.v20170420.PubRequest;
import com.aliyuncs.iot.model.v20170420.PubResponse;
import com.aliyuncs.iot.model.v20170420.QueryDeviceRequest;
import com.aliyuncs.iot.model.v20170420.QueryDeviceResponse;
import com.aliyuncs.iot.model.v20170420.QueryDeviceResponse.DeviceInfo;
import com.aliyuncs.iot.model.v20170420.RegistDeviceRequest;
import com.aliyuncs.iot.model.v20170420.RegistDeviceResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import net.wendal.nutzbook.aliyuniot.bean.AliyunDev;
import net.wendal.nutzbook.aliyuniot.service.AliyunIotService;

@IocBean
@At("/aliyuniot/admin")
public class AliyunIotModule {
    
    private static final Log log = Logs.get();

    @Inject
    protected AliyunIotService aliyunIotService;
    
    @Inject
    protected Dao dao;
    
    @Inject
    protected PropertiesProxy conf;

    @RequiresPermissions("aliyuniot.admin")
    @At("/listen/start")
    public void startListen() {
        aliyunIotService.startListen();
    }
    
    @RequiresPermissions("aliyuniot.admin")
    @At("/listen/top")
    public void topListen() {
        aliyunIotService.stopListen();
    }

    @RequiresPermissions("aliyuniot.admin")
    @At("/listen/status")
    public boolean getListenStatus() {
        return aliyunIotService.isListening();
    }
    
    @RequiresPermissions("aliyuniot.admin")
    @At("/query")
    public NutMap queryDev(@Param("..")Pager pager, String nickname, String imei, Boolean online, String deviceName) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(nickname)) {
            cnd.and(Cnd.exps("nickname", "like", nickname + "%").or("nickname", "like", "%" + nickname));
        }
        if (!Strings.isBlank(imei)) {
            cnd.and(Cnd.exps("imei", "like", imei + "%").or("imei", "like", "%" + imei));
        }
        if (!Strings.isBlank(deviceName)) {
            cnd.and(Cnd.exps("deviceName", "like", deviceName + "%").or("deviceName", "like", "%" + deviceName));
        }
        if (online != null) {
            cnd.and("online", "=", online);
        }
        cnd.desc("deviceName");
        List<AliyunDev> list = dao.query(AliyunDev.class, cnd, pager);
        pager.setRecordCount(dao.count(AliyunDev.class, cnd));
        return new NutMap("ok", true).setv("data", new QueryResult(list, pager));
    }
    
    @RequiresPermissions("aliyuniot.admin")
    @At("/add")
    public NutMap addDevice(String deviceNames) {
        NutMap re = new NutMap();
        if (Strings.isBlank(deviceNames)) {
            return re.setv("ok", false).setv("msg", "imei不能是空");
        }
        deviceNames = deviceNames.trim();
        if (deviceNames.length() < 10) {
            return re.setv("ok", false).setv("msg", "imei不能少于10位");
        }
        DefaultAcsClient client = getDefaultAcsClient();

        String productKey = conf.get("aliyuniot.productKey");
        // 逐个添加并记录状态
        NutMap data = new NutMap();
        for (String imei : Strings.splitIgnoreBlank(deviceNames, "[,\\\n]")) {
            RegistDeviceRequest req = new RegistDeviceRequest();
            req.setDeviceName(imei);
            req.setProductKey(productKey);
            try {
                RegistDeviceResponse resp = client.getAcsResponse(req);
                if (resp.getSuccess() != null && resp.getSuccess()) {
                    data.put(imei, "ok");
                } else {
                    data.put(imei, resp.getErrorMessage());
                }
            }
            catch (Throwable e) {
                log.debug("请求失败[" + imei + "]", e);
                data.put(imei, "请求失败[" + imei + "] " + e.getMessage());
            }
        }
        re.put("ok", true);
        re.put("data", data);
        return re;
    }
    
    @RequiresPermissions("aliyuniot.admin")
    @At("/sync/aliyun")
    public NutMap syncAliyun() {
        DefaultAcsClient client = getDefaultAcsClient();

        String productKey = conf.get("aliyuniot.productKey");

        QueryDeviceRequest req = new QueryDeviceRequest();
        req.setProductKey(productKey);
        req.setPageSize(1);
        req.setCurrentPage(0);
        int total = 0;
        try {
            QueryDeviceResponse resp = client.getAcsResponse(req);
            if (resp.getSuccess() != null && resp.getSuccess()) {
                total = resp.getTotal();
            }
            int insertCount = 0;
            int existCount = 0;
            int updateCount = 0;
            for (int i = 0; i <= total/100; i++) {
                req = new QueryDeviceRequest();
                req.setProductKey(productKey);
                req.setPageSize(100);
                req.setCurrentPage(i+1);
                resp = client.getAcsResponse(req);
                if (resp.getSuccess() != null && resp.getSuccess()) {
                    for (DeviceInfo info : resp.getData()) {
                        String deviceName = info.getDeviceName();
                        String aliyunId = info.getDeviceId();
                        String deviceSecret = info.getDeviceSecret();
                        Cnd cnd = Cnd.where("productKey", "=", productKey);
                        cnd.and("deviceName", "=", deviceName);
                        AliyunDev dev = dao.fetch(AliyunDev.class, cnd);
                        if (dev == null) {
                            log.debugf("新设备 deviceName=%s", deviceName);
                            dev = new AliyunDev();
                            dev.setProductKey(productKey);
                            dev.setDeviceName(deviceName);
                            dev.setDeviceSecret(deviceSecret);
                            dev.setAliyunId(aliyunId);
                            dev.setOnline(info.getDeviceStatus().equalsIgnoreCase("ONLINE"));
                            dev.setOnlineTime(System.currentTimeMillis());
                            dev.setCreateAt(System.currentTimeMillis());
                            dao.insert(dev);
                            insertCount++;
                        }
                        else if (dev.getAliyunId() == null || !info.getDeviceId().equals(dev.getAliyunId())) {
                            dev.setAliyunId(aliyunId);
                            dev.setDeviceSecret(deviceSecret);
                            dev.setOnline(info.getDeviceStatus().equalsIgnoreCase("ONLINE"));
                            dev.setOnlineTime(System.currentTimeMillis());
                            dao.update(dev, "^(aliyunId|deviceSecret|online|onlineTime)$");
                            updateCount++;
                        }
                        else {
                            existCount++;
                        }
                    }
                }
            }
            return new NutMap("ok", true).setv("msg", "新增"+insertCount+"台,更新"+updateCount+"台,无需干预"+existCount+"台,共"+resp.getTotal()+"台");
        }
        catch (Throwable e) {
            return new NutMap("ok", false).setv("msg", "请求失败 " + e.getMessage());
        }
    }
    
    @RequiresPermissions("aliyuniot.admin")
    @At("/publish")
    public NutMap publish(String deviceNames, String cnt, int qos) {
        DefaultAcsClient client = getDefaultAcsClient();
        cnt = Base64.getEncoder().encodeToString(cnt.getBytes());
        PubRequest req = new PubRequest();
        String productKey = conf.get("aliyuniot.productKey");
        NutMap re = new NutMap("ok", true);
        NutMap data = new NutMap();
        for (String deviceName : Strings.splitIgnoreBlank(deviceNames, "[,\\\n]")) {
            try {
                req.setTopicFullName("/" + productKey + "/" + deviceName + "/get");
                req.setProductKey(productKey);
                req.setMessageContent(cnt);
                req.setQos(qos);
                PubResponse resp = client.getAcsResponse(req);
                data.put(deviceName, resp);
            }
            catch (Throwable e) {
                log.debugf("发布到 /%s/%s/get 的时候抛错了", e);
                PubResponse resp = new PubResponse();
                resp.setSuccess(false);
                resp.setErrorMessage(e.getMessage());
                data.put(deviceName, resp);
            }
        }
        return re;
    }
    
    protected DefaultAcsClient getDefaultAcsClient() {
        String accessKey = conf.get("aliyuniot.accessKey");
        String accessSecret = conf.get("aliyuniot.accessSecret");
        String regionId = conf.get("aliyuniot.regionId");
        try {
            DefaultProfile.addEndpoint("cn-shanghai", "cn-shanghai", "Iot", "iot.cn-shanghai.aliyuncs.com");
        }
        catch (ClientException e) {
            e.printStackTrace();
        }
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        return new DefaultAcsClient(profile);
    }
}
