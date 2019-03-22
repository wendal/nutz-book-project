package net.wendal.nutzbook.luat.service;

import java.util.Date;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Regex;

import net.wendal.nutzbook.luat.bean.LuatDevice;
import net.wendal.nutzbook.luat.bean.LuatProject;
import net.wendal.nutzbook.luat.bean.LuatUpgradeHistory;
import net.wendal.nutzbook.luat.bean.LuatUpgradePackage;
import net.wendal.nutzbook.luat.bean.LuatUpgradePlan;
import net.wendal.nutzbook.luat.bean.LuatUpgradeReq;
import net.wendal.nutzbook.luat.bean.LuatUpgradeResp;

@IocBean(create="init")
public class LuatUpdateService {

    @Inject
    protected Dao dao;

    @Inject
    protected PropertiesProxy conf;

    public LuatUpgradeResp exec(LuatUpgradeReq req) {
        // 首先,防御一下
        if (Strings.isBlank(req.getImei()))
            return miss_imei;
        if (Strings.isBlank(req.getProjectKey()))
            return miss_project_key;
        if (Strings.isBlank(req.getFirmwareName()))
            return miss_firmware_name;
        if (Strings.isBlank(req.getVersion()))
            return miss_version;
        // 是否有对应的Project呢?
        LuatProject prj = dao.fetch(LuatProject.class, req.getProjectKey());
        if (prj == null) {
            return no_such_project;
        }
        long now = System.currentTimeMillis();
        // 是否有对应的设备呢? 没有就新增
        LuatDevice device = dao.fetch(LuatDevice.class, Cnd.where("imei", "=", req.getImei()).and("projectId", "=", prj.getId()));
        if (device == null) {
            device = new LuatDevice();
            device.setImei(req.getImei());
            device.setFirmwareName(req.getFirmwareName());
            device.setVersionStr(req.getVersion());
            device.setProjectId(prj.getId());
            device.setUserId(prj.getUserId());
            device.setLastUpgradeCheckTime(now);
            dao.insert(device);
        }
        // 更新最后汇报的时间
        else {
            device.setFirmwareName(req.getFirmwareName());
            device.setVersionStr(req.getVersion());
            device.setUpdateTime(new Date());
            device.setLastUpgradeCheckTime(now);
            dao.update(device, "firmwareName|versionStr|updateTime|lastUpgradeCheckTime");
        }
        
        // 设备是否被锁定,不允许继续升级
        // 是否在豁免期? 不在的话就要统计最近的升级次数
        if (device.getUnlockUpgradeUtil() == 0 || device.getUnlockUpgradeUtil() < now) {
            // 最近1小时的升级次数
            int upgradeCount = dao.count(LuatUpgradeHistory.class, Cnd.where("imei", "=", req.getImei()).and("projectId", "=", prj.getId()).and("createTime", ">", now - 3600000));
            if (upgradeCount > conf.getInt("luat.upgrade.limit.prehour", 5)) {
                device.setLockUpgradeUtil(now + 3*3600*1000); // 冷却3小时, TODO可配置
                dao.update(device, "lockUpgradeUtil");
                return device_locked;
            }
            if (device.getLockUpgradeUtil() > now) {
                return device_locked;
            }
        }
        // 查出升级计划
        Cnd cnd = Cnd.where("enable", "=", true).and("projectId", "=", prj.getId());
        cnd.desc("id");
        List<LuatUpgradePlan> plans = dao.query(LuatUpgradePlan.class, cnd);
        if (plans.isEmpty()) {
            return no_actives_plan;
        }
        // 逐个计划测试
        LuatUpgradeResp resp = null;
        for (LuatUpgradePlan plan : plans) {
            dao.fetchLinks(plan, "pkg");
            resp = exec(req, plan, device, true);
            if (resp.isMatched())
                break;
        }
        return resp;
    }

    public LuatUpgradeResp exec(LuatUpgradeReq req, LuatUpgradePlan plan, LuatDevice device, boolean requirePkg) {
        // 最基本的firewareName要匹配
        if (!plan.getPkg().getFirmwareName().equals(req.getFirmwareName())) {
            return not_match_firmware_name; // Lod/blf版本就不对嘛
        }
        if (plan.getPkg().getVersionStr().equals(req.getVersion())) {
            // 版本一样,无需升级
            return LuatUpgradeResp.FAIL(2, "version_equals", plan.getProjectId());
        }
        // 1. 时间范围对不对
        Date now = new Date();
        // 开始时间
        if (plan.getTimeBegin() != null && plan.getTimeBegin().getTime() > 0) {
            if (!now.after(plan.getTimeBegin())) {
                return not_match_timeBegin; // 尚未开始
            }
        }
        // 结束时间
        if (plan.getTimeEnd() != null && plan.getTimeEnd().getTime() > 0) {
            if (!now.before(plan.getTimeEnd())) {
                return not_match_timeEnd; // 已经结束
            }
        }
        // 2. 版本范围
        if (plan.getVersionMin() != 0) {
            int myversion = getVersionInt(req.getVersion());
            if (myversion < plan.getVersionMin()) {
                return not_match_versionMin; // 最低版本号不满足
            }
        }
        if (plan.getVersionMax() != 0) {
            int myversion = getVersionInt(req.getVersion());
            if (myversion > plan.getVersionMax()) {
                return not_match_versionMax; // 最高版本号不满足
            }
        }
        // 3. 会不会是指定了文本型的版本范围呢?
        if (!Strings.isBlank(plan.getVersionEq())) {
            if (!plan.getVersionEq().contains(req.getVersion())) {
                return not_match_versionEq; // 不在允许版本范围内
            }
        }
        if (!Strings.isBlank(plan.getVersionNEq())) {
            if (plan.getVersionNEq().contains(req.getVersion())) {
                return not_match_versionNEq; // 在不允许的版本范围内
            }
        }
        // 4. 会不会指定了IMEI呢?
        if (!Strings.isBlank(plan.getImeis())) {
            if (!plan.getImeis().contains(req.getImei())) {
                return not_match_imeis; // imei不在允许范围内
            }
        }
        // 5. 会不会指定了ICCID呢?
        if (!Strings.isBlank(plan.getIccids())) {
            if (!plan.getIccids().contains(req.getIccid())) {
                return not_match_iccids; // iccid不在允许范围呢
            }
        }
        // 6. 会不会指定了省份呢?
        if (!Strings.isBlank(plan.getProvinces())) {
            if (!plan.getProvinces().contains(device.getProvince())) {
                return not_match_provinces; // 省份不在允许范围呢
            }
        }
        if (!Strings.isBlank(plan.getCities())) {
            if (!plan.getCities().contains(device.getCity())) {
                return not_match_cities; // 城市不在允许范围呢
            }
        }
        LuatUpgradeResp resp = new LuatUpgradeResp();
        resp.setMatched(true);
        if (requirePkg) {
            dao.fetchLinks(plan, "pkg");
            resp.setPkg(plan.getPkg());
        }
        resp.setPlanId(plan.getId());
        resp.setProjectId(device.getProjectId());
        return resp;
    }

    public int getVersionInt(String verionStr) {
        if (Regex.match("^[0-9]+\\.[0-9]+\\.[0-9]+$", verionStr)) {
            String[] tmp = verionStr.split("\\.");
            return Integer.parseInt(tmp[0], 10) * 10000 + Integer.parseInt(tmp[1], 10) * 100 + Integer.parseInt(tmp[2], 10);
        }
        return -1;
    }

    public String getPkgPath(LuatUpgradePackage pkg) {
        String path = conf.get("luat.update.bindir", "/data/luat/update/");
        path += pkg.getProjectId() + "/";
        path += pkg.getId();
        path += ".bin";
        return path;
    }
    
    public LuatUpgradeHistory addHistory(LuatUpgradeReq req, LuatUpgradeResp resp) {
        // 记录日志
        LuatUpgradeHistory his = new LuatUpgradeHistory();
        his.setFirmwareName(req.getFirmwareName());
        his.setVersionReport(req.getVersion());
        his.setImei(req.getImei());
        his.setIccid(req.getIccid());
        if (resp.getProjectId() > 0)
            his.setProjectId(resp.getProjectId());
        else if (!Strings.isBlank(req.getProjectKey())) {
            LuatProject prj = dao.fetch(LuatProject.class, req.getProjectKey());
            if (prj != null)
                his.setProjectId(prj.getId());
        }
        if (resp.isMatched()) {
            his.setMatched(true);
            his.setPkgId(resp.getPkg().getId());
            his.setPlanId(resp.getPlanId());
            his.setRespCode(200);
        }
        else {
            his.setRespReson(resp.getReson());
            his.setRespCode(400);
        }
        return dao.insert(his);
    }
    
    public void init() {
        Daos.migration(dao, LuatDevice.class, true, false, false);
    }

    static LuatUpgradeResp device_locked = LuatUpgradeResp.FAIL(1, "device_locked");
    static LuatUpgradeResp no_actives_plan = LuatUpgradeResp.FAIL(1, "no_actives_plan");
    static LuatUpgradeResp miss_firmware_name = LuatUpgradeResp.FAIL(1, "miss_firmware_name");
    static LuatUpgradeResp miss_imei = LuatUpgradeResp.FAIL(1, "miss_imei");
    static LuatUpgradeResp miss_version = LuatUpgradeResp.FAIL(1, "miss_version");
    static LuatUpgradeResp miss_project_key = LuatUpgradeResp.FAIL(1, "miss_project_key");
    static LuatUpgradeResp no_such_project = LuatUpgradeResp.FAIL(1, "no_such_project");
    static LuatUpgradeResp not_match_firmware_name = LuatUpgradeResp.FAIL(1, "not_match_firmware_name");
    static LuatUpgradeResp no_matched_plan = LuatUpgradeResp.FAIL(1, "no_matched_plan");
    static LuatUpgradeResp not_match_timeBegin = LuatUpgradeResp.FAIL(1, "not_match_timeBegin");
    static LuatUpgradeResp not_match_timeEnd = LuatUpgradeResp.FAIL(1, "not_match_timeEnd");
    static LuatUpgradeResp not_match_versionMin = LuatUpgradeResp.FAIL(1, "not_match_versionMin");
    static LuatUpgradeResp not_match_versionMax = LuatUpgradeResp.FAIL(1, "not_match_versionMax");
    static LuatUpgradeResp not_match_versionEq = LuatUpgradeResp.FAIL(1, "not_match_versionEq");
    static LuatUpgradeResp not_match_versionNEq = LuatUpgradeResp.FAIL(1, "not_match_versionNEq");
    static LuatUpgradeResp not_match_imeis = LuatUpgradeResp.FAIL(1, "not_match_imeis");
    static LuatUpgradeResp not_match_iccids = LuatUpgradeResp.FAIL(1, "not_match_iccids");
    static LuatUpgradeResp not_match_provinces = LuatUpgradeResp.FAIL(1, "not_match_provinces");
    static LuatUpgradeResp not_match_cities = LuatUpgradeResp.FAIL(1, "not_match_cities");
}
