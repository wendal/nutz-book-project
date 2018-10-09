package net.wendal.nutzbook.aliyuniot.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("t_aliyuniot_dev_stat")
@TableIndexes({@Index(fields="deviceId", unique=false), @Index(fields="time", unique=false)})
public class AliyunDevStatusLog {

    @Id
    private long id;
    
    @Column("did")
    private long deviceId;
    
    @Column("ot")
    private boolean online;
    
    @Column("t")
    private long time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
