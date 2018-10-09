package net.wendal.nutzbook.aliyuniot.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("t_aliyuniot_dev")
@TableIndexes({@Index(fields= {"productKey", "deviceName"}, unique=true),
               @Index(fields= {"nickname"}, unique=false),
               @Index(fields= {"imei", "iccid"}, unique=false),
               @Index(fields= {"online"}, unique=false)})
public class AliyunDev {
    
    /**
     * 设备本地id
     */
    @Id
    private long id;
    /**
     * 设备所属的productKey
     */
    @Column("prod")
    private String productKey;
    /**
     * 设备的deviceName,通常是imei
     */
    @Column("devnm")
    private String deviceName;
    @Column("devsec")
    private String deviceSecret;
    @Column("aid")
    private String aliyunId;
    // ====个性化信息====
    @Column("nname")
    private String nickname;
    @Column
    private String tags;
    // ====设备基本信息====
    @Column
    private String imei;
    @Column
    private String iccid;
    @Column("phone")
    private String phoneNumber;
    // 网卡mac地址
    @Column
    private String mac;
    // 软件版本号
    @Column("swv")
    private String swversion;
    // ====位置信息====
    @Column
    private double lat;
    @Column
    private double lng;
    @Column
    private double ele;
    // ====状态信息====
    @Column("cat")
    private long createAt;
    @Column("ls")
    private long lastSee;
    @Column("onn")
    private boolean online;
    @Column("onnt")
    private long onlineTime;
    @Column
    private int rssi;
    @Column("pv")
    private int powerVoltage;
    // ====触发函数=====
    @ColDefine(width=4096)
    @Column("wonline")
    private String whenOnline;
    @ColDefine(width=4096)
    @Column("woffline")
    private String whenOffline;
    @ColDefine(width=4096)
    @Column("wmesssage")
    private String whenMessage;
    
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getProductKey() {
        return productKey;
    }
    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public String getAliyunId() {
        return aliyunId;
    }
    public void setAliyunId(String aliyunId) {
        this.aliyunId = aliyunId;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public String getImei() {
        return imei;
    }
    public void setImei(String imei) {
        this.imei = imei;
    }
    public String getIccid() {
        return iccid;
    }
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getMac() {
        return mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public double getEle() {
        return ele;
    }
    public void setEle(double ele) {
        this.ele = ele;
    }
    public long getCreateAt() {
        return createAt;
    }
    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }
    public long getLastSee() {
        return lastSee;
    }
    public void setLastSee(long lastSee) {
        this.lastSee = lastSee;
    }
    public boolean isOnline() {
        return online;
    }
    public void setOnline(boolean online) {
        this.online = online;
    }
    public int getRssi() {
        return rssi;
    }
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
    public int getPowerVoltage() {
        return powerVoltage;
    }
    public void setPowerVoltage(int powerVoltage) {
        this.powerVoltage = powerVoltage;
    }
    public String getWhenOnline() {
        return whenOnline;
    }
    public void setWhenOnline(String whenOnline) {
        this.whenOnline = whenOnline;
    }
    public String getWhenOffline() {
        return whenOffline;
    }
    public void setWhenOffline(String whenOffline) {
        this.whenOffline = whenOffline;
    }
    public String getWhenMessage() {
        return whenMessage;
    }
    public void setWhenMessage(String whenMessage) {
        this.whenMessage = whenMessage;
    }
    public long getOnlineTime() {
        return onlineTime;
    }
    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }
    public String getDeviceSecret() {
        return deviceSecret;
    }
    public void setDeviceSecret(String deviceSecret) {
        this.deviceSecret = deviceSecret;
    }
    
    
}
