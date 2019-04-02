package net.wendal.nutzbook.luat.bean;

import org.nutz.json.JsonField;

public class LuatUpgradeResp {
    // {"code": 26, "msg": "\u65e0\u6548\u7684\u56fa\u4ef6"}
    private int code;
    @JsonField(ignore = true)
    private boolean matched;
    @JsonField("msg")
    private String reson;
    @JsonField(ignore = true)
    private LuatUpgradePackage pkg;
    @JsonField(ignore = true)
    private long projectId;
    @JsonField(ignore = true)
    private long planId;

    public static LuatUpgradeResp FAIL(int code, String reson) {
        return FAIL(code, reson, 0);
    }
    
    public static LuatUpgradeResp FAIL(int code, String reson, long projectId) {
        LuatUpgradeResp resp = new LuatUpgradeResp();
        resp.code = code;
        resp.reson = reson;
        resp.projectId = projectId;
        return resp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public String getReson() {
        return reson;
    }

    public void setReson(String reson) {
        this.reson = reson;
    }

    public LuatUpgradePackage getPkg() {
        return pkg;
    }

    public void setPkg(LuatUpgradePackage pkg) {
        this.pkg = pkg;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

}
