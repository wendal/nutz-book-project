package net.wendal.nutzbook.luat.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

import net.wendal.nutzbook.core.bean.BasePojo;
import net.wendal.nutzbook.core.bean.User;

@Table("t_luat_upgrade_plan")
public class LuatUpgradePlan extends BasePojo {

    private static final long serialVersionUID = 1L;
    @Id(auto = false)
    @Prev(els = @EL("ig(view.tableName)"))
    private long id;
    @Name
    private String name;
    @Column
    private String content;
    @Column
    private long projectId;
    @Column
    private long pkgId;
    @Column
    private long userId;
    @Column
    private int versionMin;
    @Column
    private int versionMax;
    @Column
    private String versionEq;
    @Column
    private String versionNEq;
    @Column
    private String imeis;
    @Column
    private String iccids;
    @Column
    private Date timeBegin;
    @Column
    private Date timeEnd;
    @Column
    private String provinces;
    @Column
    private String cities;
    @Column
    private boolean enable;

    @One
    private LuatProject project;

    @One
    private User user;

    @One
    private LuatUpgradePackage pkg;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getPkgId() {
        return pkgId;
    }

    public void setPkgId(long pkgId) {
        this.pkgId = pkgId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getVersionMin() {
        return versionMin;
    }

    public void setVersionMin(int versionMin) {
        this.versionMin = versionMin;
    }

    public int getVersionMax() {
        return versionMax;
    }

    public void setVersionMax(int versionMax) {
        this.versionMax = versionMax;
    }

    public String getVersionEq() {
        return versionEq;
    }

    public void setVersionEq(String versionEq) {
        this.versionEq = versionEq;
    }

    public String getVersionNEq() {
        return versionNEq;
    }

    public void setVersionNEq(String versionNEq) {
        this.versionNEq = versionNEq;
    }

    public String getImeis() {
        return imeis;
    }

    public void setImeis(String imeis) {
        this.imeis = imeis;
    }

    public String getIccids() {
        return iccids;
    }

    public void setIccids(String iccids) {
        this.iccids = iccids;
    }

    public Date getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(Date timeBegin) {
        this.timeBegin = timeBegin;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getProvinces() {
        return provinces;
    }

    public void setProvinces(String provinces) {
        this.provinces = provinces;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
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

    public LuatUpgradePackage getPkg() {
        return pkg;
    }

    public void setPkg(LuatUpgradePackage pkg) {
        this.pkg = pkg;
    }

}
