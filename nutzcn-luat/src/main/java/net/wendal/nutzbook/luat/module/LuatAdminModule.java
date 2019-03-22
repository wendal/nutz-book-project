package net.wendal.nutzbook.luat.module;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Each;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;

import net.wendal.nutzbook.core.bean.User;
import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.luat.bean.LuatDevice;
import net.wendal.nutzbook.luat.bean.LuatProject;
import net.wendal.nutzbook.luat.bean.LuatUpgradeHistory;
import net.wendal.nutzbook.luat.bean.LuatUpgradePackage;
import net.wendal.nutzbook.luat.bean.LuatUpgradePlan;
import net.wendal.nutzbook.luat.bean.LuatUpgradeReq;
import net.wendal.nutzbook.luat.service.LuatUpdateService;

@IocBean
@At("/luat/admin")
@Ok("json:full")
public class LuatAdminModule extends BaseModule {

    @Inject
    protected LuatUpdateService luatUpdateService;

    /*
     * 项目相关的增删改查
     */
    // ---------------------------------------------------------------
    // 新增
    @RequiresAuthentication
    @POST
    @At("/project/add")
    public NutMap projectAdd(@Param("..") LuatProject project, @Attr("me") User me) {
        project.setAccessKey(R.UU32());
        project.setUserId(me.getId());
        dao.insert(project);
        return _ok(project);
    }

    // 更新
    @RequiresAuthentication
    @POST
    @At("/project/update")
    public NutMap projectUpdate(@Param("..") LuatProject project, @Attr("me") User me) {
        LuatProject prj = dao.fetch(LuatProject.class, project.getId());
        if (prj == null) {
            return _fail("no_such_project");
        }
        if (prj.getUserId() != me.getId()) {
            return _fail("not_your_project");
        }
        project.setUpdateTime(new Date());
        dao.update(project, "nickname|updateTime");
        return _ok(project);
    }

    // 删除, 但不允许
    @RequiresAuthentication
    @POST
    @At("/project/delete")
    public NutMap projectDelete(@Param("..") LuatProject project, @Attr("me") User me) {
        LuatProject prj = dao.fetch(LuatProject.class, project.getId());
        if (prj == null) {
            return _fail("no_such_project");
        }
        if (prj.getUserId() != me.getId()) {
            return _fail("not_your_project");
        }
        return _fail("not_allow_yet"); // 当前无论如何都不允许删除,哈哈
    }

    // 查询
    @RequiresAuthentication
    @At("/project/query")
    public NutMap projectQuery(@Param("..") Pager pager, @Param("nickname") String nickname, @Param("id") long id, @Attr("me") User me) {
        Cnd cnd = Cnd.where("userId", "=", me.getId());
        _like(cnd, "nickname", nickname);
        if (id > 0) {
            cnd.and("id", "=", id);
        }
        cnd.desc("id");
        List<LuatProject> list = dao.query(LuatProject.class, cnd, pager);
        int count = dao.count(LuatProject.class, cnd);
        pager.setRecordCount(count);
        for (LuatProject project : list) {
            project.setDeviceCount(dao.count(LuatDevice.class, Cnd.where("projectId", "=", project.getId())));
            project.setPackageCount(dao.count(LuatUpgradePackage.class, Cnd.where("projectId", "=", project.getId())));
        }
        return _ok(new QueryResult(list, pager));
    }

    @RequiresAuthentication
    @At("/project/select")
    @POST
    public NutMap projectSelect(long id, @Attr("me") User me, HttpSession session) {
        if (id == 0)
            return _ok(null);
        LuatProject project = dao.fetch(LuatProject.class, id);
        if (project == null) {
            return _fail("no_such_project");
        }
        if (project.getUserId() != me.getId()) {
            return _fail("not_your_project");
        }
        session.setAttribute("luat_project", project);
        return _ok(null);
    }

    @RequiresAuthentication
    @At("/project/current")
    public NutMap projectCurrent(@Attr("luat_project") LuatProject project) {
        return _ok(project);
    }
    
    @RequiresAuthentication
    @At("/project/change_key")
    @POST
    public NutMap projectChangeName(long id, String newKey, 
                                    @Attr("me") User me) {
        if (id < 0)
            return _fail("no_such_project");
        if (Strings.isBlank(newKey))
            return _fail("new_key_invaild");
        newKey = newKey.trim();
        if (newKey.contains(" "))
            return _fail("new_key_continues_space");
        if (newKey.length() < 10)
            return _fail("new_key_aleast_10chars");
        LuatProject project = dao.fetch(LuatProject.class, id);
        if (project == null)
            return _fail("no_such_project");
        if (project.getUserId() != me.getId())
            return _fail("not_your_project");
        int c = dao.count(LuatProject.class, Cnd.where("name", "=", newKey));
        if (c > 0)
            return _fail("new_key_exists");
        dao.update(LuatProject.class, Chain.make("name", newKey), Cnd.where("id", "=", id));
        return _ok(null);
    }

    /*
     * 设备相关的增删改查
     */
    // ---------------------------------------------------------------
    @RequiresAuthentication
    @POST
    @At("/device/add")
    public NutMap deviceAdd(@Param("..") LuatDevice device, @Attr("me") User me, @Attr("luat_project") LuatProject project) {
        if (Strings.isBlank(device.getImei()))
            return _fail("need_imei");
        if (project == null) {
            return _fail("select_one_project_first");
        }
        if (project.getUserId() != me.getId()) {
            return _fail("not_your_project");
        }
        String[] imeis = Strings.splitIgnoreBlank(device.getImei(), "[\\r\\n,]");
        device.setUserId(me.getId());
        device.setProjectId(project.getId());
        int added = 0;
        int exists = 0;
        for (String imei : imeis) {
            if (dao.count(LuatDevice.class, Cnd.where("imei", "=", imei).and("projectId", "=", project.getId())) == 0) {
                device.setImei(imei);
                dao.insert(device);
                added ++;
            }
            else {
                exists ++;
            }
        }
        return _ok(_map("added", added, "exists", exists));
    }

    // 更新
    @RequiresAuthentication
    @POST
    @At("/device/update")
    public NutMap deviceUpdate(@Param("..") LuatDevice device, @Attr("me") User me) {
        LuatDevice dev = dao.fetch(LuatDevice.class, device.getId());
        if (dev == null) {
            return _fail("no_such_device");
        }
        if (dev.getUserId() != me.getId()) {
            return _fail("not_your_device");
        }
        device.setUpdateTime(new Date());
        dao.update(device, "iccid|firmwareName|versionStr|versionInt|lat|lng|province|city|content|tags");
        return _ok(device);
    }

    // 删除, 但不允许
    @RequiresAuthentication
    @POST
    @At("/device/delete")
    public NutMap deviceDelete(@Param("..") LuatDevice device, @Attr("me") User me) {
        LuatDevice dev = dao.fetch(LuatDevice.class, device.getId());
        if (dev == null) {
            return _fail("no_such_device");
        }
        if (dev.getUserId() != me.getId()) {
            return _fail("not_your_device");
        }
        return _fail("not_allow_yet"); // 当前无论如何都不允许删除,哈哈
    }

    // 查询
    @RequiresAuthentication
    @At("/device/query")
    public NutMap deviceQuery(@Param("..") Pager pager, @Param("..") LuatDevice device, @Attr("me") User me, @Attr("luat_project") LuatProject project) {
        if (project == null) {
            return _fail("select_one_project_first");
        }
        Cnd cnd = Cnd.where("userId", "=", me.getId()).and("projectId", "=", project.getId());
        _like(cnd, "imei", device.getImei());
        _like(cnd, "iccid", device.getIccid());
        _like(cnd, "imsi", device.getImsi());
        _like(cnd, "msisdn", device.getMsisdn());
        _like(cnd, "firmwareName", device.getFirmwareName());
        _like(cnd, "city", device.getCity());
        _like(cnd, "province", device.getProvince());

        cnd.desc("id");

        List<LuatDevice> list = dao.query(LuatDevice.class, cnd, pager);
        int count = dao.count(LuatDevice.class, cnd);
        pager.setRecordCount(count);
        return _ok(new QueryResult(list, pager));
    }
    
    @RequiresAuthentication
    @POST
    @At("/device/unlock_upgrade")
    public NutMap deviceUnlockUpgrade(@Param("..") LuatDevice device, @Attr("luat_project") LuatProject project) {
        LuatDevice dev = dao.fetch(LuatDevice.class, device.getId());
        if (dev == null) {
            return _fail("no_such_device");
        }
        if (dev.getProjectId() != project.getId()) {
            return _fail("not_your_device");
        }
        dev.setUnlockUpgradeUtil(System.currentTimeMillis() + 3600*1000);
        dao.update(dev, "^unlockUpgradeUtil$");
        return _ok(null);
    }

    /*
     * 固件相关的增删改查
     */
    // -------------------------------------------------------------------------------------------------
    // 新增
    @RequiresAuthentication
    @AdaptBy(type = WhaleAdaptor.class)
    @POST
    @At("/upgrade/package/upload")
    @Ok("json:full")
    public NutMap packageUpload(@Param("file") TempFile tf, @Param("content") String content, @Attr("luat_project") LuatProject project, @Attr("me") User me) throws IOException {
        if (project == null)
            return _fail("select_on_project_first");
        // 解析文件名
        String[] tmp = tf.getSubmittedFileName().split("_", 3);
        if (tmp.length != 3) {
            return _fail("wrong_file_name");
        }
        if (tf.getSize() < 1024) {
            return _fail("need_file");
        }
        LuatUpgradePackage pkg = new LuatUpgradePackage();
        pkg.setOriginName(tf.getSubmittedFileName());;
        pkg.setContent(content);
        pkg.setFinger(Lang.md5(tf.getFile()));
        pkg.setProjectId(project.getId());
        pkg.setUserId(me.getId());
        pkg.setFirmwareName(tmp[0] + "_" + tmp[2].substring(0, tmp[2].indexOf('.')));
        pkg.setVersionStr(tmp[1]);
        pkg.setVersionInt(luatUpdateService.getVersionInt(pkg.getVersionStr()));
        dao.insert(pkg);
        Files.copyFile(tf.getFile(), Files.createFileIfNoExists(luatUpdateService.getPkgPath(pkg)));
        tf.delete();
        return _map("ok", true, "data", pkg);
    }

    // 查询
    @RequiresAuthentication
    @At("/upgrade/package/query")
    public NutMap packageQuery(@Param("..") Pager pager, @Param("..") LuatUpgradePackage pkg, @Attr("me") User me, @Attr("luat_project") LuatProject project) {
        if (project == null)
            return _fail("select_on_project_first");
        Cnd cnd = Cnd.where("userId", "=", me.getId()).and("projectId", "=", project.getId());
        _like(cnd, "firmwareName", pkg.getFirmwareName());
        _like(cnd, "versionStr", pkg.getVersionStr());
        _like(cnd, "finger", pkg.getFinger());
        _like(cnd, "content", pkg.getContent());

        cnd.desc("id");
        List<LuatUpgradePackage> list = dao.query(LuatUpgradePackage.class, cnd, pager);
        int count = dao.count(LuatUpgradePackage.class, cnd);
        pager.setRecordCount(count);
        return _ok(new QueryResult(list, pager));
    }

    /*
     * 升级计划的增删改查
     */
    // -------------------------------------------------------------------------------------------------
    @RequiresAuthentication
    @POST
    @At("/upgrade/plan/add")
    @AdaptBy(type = WhaleAdaptor.class)
    public NutMap upgradePlanAdd(@Param("..") LuatUpgradePlan plan, @Attr("me") User me, @Attr("luat_project") LuatProject project) {
        if (project == null)
            return _fail("select_one_project_first");
        plan.setUserId(me.getId());
        plan.setProjectId(project.getId());
        dao.insert(plan);
        return _ok(plan);
    }

    // 更新
    @RequiresAuthentication
    @POST
    @At("/upgrade/plan/update")
    @AdaptBy(type = WhaleAdaptor.class)
    public NutMap upgradePlanUpdate(@Param("..") LuatUpgradePlan device, @Attr("me") User me) {
        LuatUpgradePlan plan = dao.fetch(LuatUpgradePlan.class, device.getId());
        if (plan == null) {
            return _fail("no_such_upgrade_plan");
        }
        if (plan.getUserId() != me.getId()) {
            return _fail("not_your_upgrade_plan");
        }
        device.setUpdateTime(new Date());
        dao.update(device, "nickname|pkgId|imeis|iccids|versionEq|versionNEq|provinces|cities|content|updateTime");
        return _ok(device);
    }

    // 删除, 但不允许
    @RequiresAuthentication
    @POST
    @At("/upgrade/plan/delete")
    public NutMap upgradePlanDelete(@Param("..") LuatUpgradePlan plan, @Attr("me") User me) {
        LuatUpgradePlan pl = dao.fetch(LuatUpgradePlan.class, plan.getId());
        if (pl == null) {
            return _fail("no_such_upgrade_plan");
        }
        if (pl.getUserId() != me.getId()) {
            return _fail("not_your_upgrade_plan");
        }
        return _fail("not_allow_yet"); // 当前无论如何都不允许删除,哈哈
    }

    @RequiresAuthentication
    @POST
    @At("/upgrade/plan/enable")
    public NutMap upgradePlanEnable(@Param("id") long id, 
                                    @Param("enable") boolean enable, 
                                    @Attr("me") User me) {
        LuatUpgradePlan plan = dao.fetch(LuatUpgradePlan.class, id);
        if (plan == null) {
            return _fail("no_such_upgrade_plan");
        }
        if (plan.getUserId() != me.getId()) {
            return _fail("not_your_upgrade_plan");
        }
        plan.setEnable(enable);
        dao.update(plan, "^enable$");
        return _ok(null);
    }
    
    @RequiresAuthentication
    @POST
    @At("/upgrade/plan/disable_all")
    public NutMap upgradePlanDisableAll(@Attr("luat_project") LuatProject project) {
        if (project == null)
            return _fail("select_on_project_first");
        int changed = dao.update(LuatUpgradePlan.class, Cnd.where("projectId", "=", project.getId()));
        return _ok(_map("changed", changed));
    }

    // 查询

    @RequiresAuthentication
    @At("/upgrade/plan/query")
    public NutMap upgradePlanQuery(@Param("..") Pager pager, @Param("..") LuatUpgradePlan plan, @Attr("me") User me, @Attr("luat_project") LuatProject project) {
        if (project == null)
            return _fail("select_on_project_first");
        Cnd cnd = Cnd.where("userId", "=", me.getId()).and("projectId", "=", project.getId());
        _like(cnd, "nickname", plan.getNickname());
        // cnd.and("enable", "=", plan.isEnable());
        cnd.desc("id");

        List<LuatUpgradePlan> list = dao.query(LuatUpgradePlan.class, cnd, pager);
        int count = dao.count(LuatUpgradePlan.class, cnd);
        pager.setRecordCount(count);
        dao.fetchLinks(list, "pkg");
        return _ok(new QueryResult(list, pager));
    }
    
    @RequiresAuthentication
    @POST
    @At("/upgrade/plan/do_predict")
    public NutMap upgradePlanPredict(@Param("id") long id, @Param("enable") boolean enable, @Attr("me") User me) {
        LuatUpgradePlan plan = dao.fetch(LuatUpgradePlan.class, id);
        if (plan == null) {
            return _fail("no_such_upgrade_plan");
        }
        if (plan.getUserId() != me.getId()) {
            return _fail("not_your_upgrade_plan");
        }
        Cnd cnd = Cnd.where("projectId", "=", plan.getProjectId());
        dao.fetchLinks(plan, "pkg");
        int deviceCount = dao.count(LuatDevice.class, Cnd.where("projectId", "=", plan.getProjectId()));
        AtomicLong matchCount = new AtomicLong();
        dao.each(LuatDevice.class, cnd, new Each<LuatDevice>() {
            @Override
            public void invoke(int index, LuatDevice device, int length) {
                LuatUpgradeReq req = new LuatUpgradeReq();
                req.setFirmwareName(device.getFirmwareName());
                req.setImei(device.getImei());
                req.setNeedOssUrl(1);
                req.setVersion(device.getVersionStr());
                if (luatUpdateService.exec(req, plan, device, false).isMatched()) {
                    matchCount.incrementAndGet();
                }
            } 
        });
        return _ok(_map("deviceCount", deviceCount, "matchCount", matchCount.intValue()));
    }

    /*
     * 升级日志查询
     */
    // -------------------------------------------------------------------------------------------------
    @RequiresAuthentication
    @At("/upgrade/history/query")
    public NutMap upgradeHistoryQuery(@Param("..") Pager pager, @Param("..") LuatUpgradeHistory his, @Attr("me") User me, @Attr("luat_project") LuatProject project) {
        if (project == null)
            return _fail("select_one_project_first");
        Cnd cnd = Cnd.where("projectId", "=", project.getId());
        _like(cnd, "imei", his.getImei());
        cnd.desc("id");

        List<LuatUpgradeHistory> list = dao.query(LuatUpgradeHistory.class, cnd, pager);
        dao.fetchLinks(list, null);
        int count = dao.count(LuatUpgradeHistory.class, cnd);
        pager.setRecordCount(count);
        return _ok(new QueryResult(list, pager));
    }
}
