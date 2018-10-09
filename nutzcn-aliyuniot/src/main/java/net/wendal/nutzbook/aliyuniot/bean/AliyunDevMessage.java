package net.wendal.nutzbook.aliyuniot.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("t_aliyuniot_msg")
@TableIndexes({@Index(fields = {"deviceId"}, unique = false), @Index(fields = {"topic"}, unique = false), @Index(fields = {"time"}, unique = false)})
public class AliyunDevMessage {

    @Id
    private long id;
    @Column("did")
    private long deviceId;
    @Column
    private int dir;
    @Column
    private String topic;
    @Column
    private String cnt;
    @Column
    private int qos;
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

    public int isDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
