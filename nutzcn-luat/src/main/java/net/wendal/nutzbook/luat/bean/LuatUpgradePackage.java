package net.wendal.nutzbook.luat.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

import net.wendal.nutzbook.core.bean.BasePojo;
import net.wendal.nutzbook.core.bean.User;

@Table("t_luat_upgrade_pkg")
@TableIndexes({@Index(fields = {"projectId"}, unique = false)})
public class LuatUpgradePackage extends BasePojo {

    private static final long serialVersionUID = 1L;
    @Id(auto = false)
    @Prev(els = @EL("ig(view.tableName)"))
    private long id;
    @Column
    private String originName;
    @Column
    private String content;
    @Column
    private long projectId;
    @Column
    private String firmwareName;
    @Column
    private String versionStr;
    @Column
    private int versionInt;
    @Column
    private long userId;
    @Column
    private int len;
    @Column
    private String finger;
    @Column
    @ColDefine(width = 1024)
    private String ossUrl;

    @One
    private LuatProject project;

    @One
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String name) {
        this.originName = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getFirmwareName() {
        return firmwareName;
    }

    public void setFirmwareName(String firmwareName) {
        this.firmwareName = firmwareName;
    }

    public String getVersionStr() {
        return versionStr;
    }

    public void setVersionStr(String versionStr) {
        this.versionStr = versionStr;
    }

    public int getVersionInt() {
        return versionInt;
    }

    public void setVersionInt(int versionInt) {
        this.versionInt = versionInt;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }

    public LuatProject getProject() {
        return project;
    }

    public void setProject(LuatProject project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOssUrl() {
        return ossUrl;
    }

    public void setOssUrl(String ossUrl) {
        this.ossUrl = ossUrl;
    }
}
