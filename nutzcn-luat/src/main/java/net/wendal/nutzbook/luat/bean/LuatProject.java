package net.wendal.nutzbook.luat.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

import net.wendal.nutzbook.core.bean.BasePojo;
import net.wendal.nutzbook.core.bean.User;

@Table("t_luat_prj")
public class LuatProject extends BasePojo {

    private static final long serialVersionUID = 1L;
    @Id(auto = false)
    @Prev(els = @EL("ig(view.tableName)"))
    private long id;
    @Name
    @Prev(els = @EL("uuid()"))
    private String name;
    @Column
    private String nickname;
    @Column
    private String content;
    @Column
    private long userId;
    @Column
    private String accessKey;

    @One
    private User user;

    // -----非数据库字段---
    // 设备总数
    private int deviceCount;
    // 固件总数
    private int packageCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

}
