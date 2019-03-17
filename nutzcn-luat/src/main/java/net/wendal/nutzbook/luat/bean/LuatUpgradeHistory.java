package net.wendal.nutzbook.luat.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

import net.wendal.nutzbook.core.bean.BasePojo;

@Table("t_luat_upgrade_his")
@TableIndexes(@Index(fields = {"imei", "projectId", "createTime"}, unique = false))
public class LuatUpgradeHistory extends BasePojo {

    private static final long serialVersionUID = 1L;
    @Id(auto = false)
    @Prev(els = @EL("ig(view.tableName)"))
    private long id;
    @Column
    private String imei;
    @Column
    private String iccid;
    @Column
    private long projectId;
    @Column
    private String versionReport;
    @Column
    private String firmwareName;
    @Column
    private String versionResp;
    @Column
    private boolean matched;
    @Column
    private long planId;
    @Column
    private long pkgId;
    @Column
    private int respCode;
    @Column
    private String respReson;
    @Column
    private String content;

    @One
    private LuatProject project;

    @One
    private LuatUpgradePlan plan;

    @One
    private LuatUpgradePackage pkg;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getVersionReport() {
        return versionReport;
    }

    public void setVersionReport(String versionReport) {
        this.versionReport = versionReport;
    }

    public String getFirmwareName() {
        return firmwareName;
    }

    public void setFirmwareName(String firmwareName) {
        this.firmwareName = firmwareName;
    }

    public String getVersionResp() {
        return versionResp;
    }

    public void setVersionResp(String versionResp) {
        this.versionResp = versionResp;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public long getPkgId() {
        return pkgId;
    }

    public void setPkgId(long pkgId) {
        this.pkgId = pkgId;
    }

    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public String getRespReson() {
        return respReson;
    }

    public void setRespReson(String respReson) {
        this.respReson = respReson;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LuatProject getProject() {
        return project;
    }

    public void setProject(LuatProject project) {
        this.project = project;
    }

    public LuatUpgradePlan getPlan() {
        return plan;
    }

    public void setPlan(LuatUpgradePlan plan) {
        this.plan = plan;
    }

    public LuatUpgradePackage getPkg() {
        return pkg;
    }

    public void setPkg(LuatUpgradePackage pkg) {
        this.pkg = pkg;
    }

}
