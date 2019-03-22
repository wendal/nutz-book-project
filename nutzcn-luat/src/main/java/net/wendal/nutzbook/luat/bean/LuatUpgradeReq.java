package net.wendal.nutzbook.luat.bean;

import org.nutz.json.JsonField;
import org.nutz.mvc.annotation.Param;

public class LuatUpgradeReq {

    @Param("project_key")
    @JsonField("project_key")
    private String projectKey;

    private String imei;

    @Param("firmware_name")
    @JsonField("firmware_name")
    private String firmwareName;

    private String version;

    @Param("need_oss_url")
    @JsonField("need_oss_url")
    private int needOssUrl;

    private String iccid;

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getFirmwareName() {
        return firmwareName;
    }

    public void setFirmwareName(String firmwareName) {
        this.firmwareName = firmwareName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getNeedOssUrl() {
        return needOssUrl;
    }

    public void setNeedOssUrl(int needOssUrl) {
        this.needOssUrl = needOssUrl;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
}
