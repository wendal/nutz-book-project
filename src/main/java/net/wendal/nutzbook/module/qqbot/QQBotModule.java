package net.wendal.nutzbook.module.qqbot;

import net.wendal.nutzbook.bean.qqbot.QQBotMessage;
import net.wendal.nutzbook.bean.qqbot.QQBotRole;
import net.wendal.nutzbook.module.qqbot.executors.QQBotTopicSearch;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * QQLite机器人入口类
 * Created by wendal on 2015/12/16.
 */
@At("/qqbot")
@IocBean(create = "init")
public class QQBotModule {

    private static final Log log = Logs.get();

    @Inject
    Dao dao;

    protected List<QQBotRole> roles;

    protected String prefix = "@wendal机器人(2096459391) ";

    /**
     * 主入口方法,qqlite的回调入口
     */
    @POST
    @At
    @Ok("raw")
    public String income(@Param("..")QQBotMessage msg, HttpServletRequest req) {
        log.debug("params==>" + req.getParameterMap());
        if (msg == null) {
            return "";
        }
        if (!Strings.isBlank(msg.Message)) {
            msg.Message = msg.Message.trim();
            if (msg.Message.startsWith(prefix))
                msg.Message = msg.Message.substring(prefix.length()).trim();
            if ("帮助".equals(msg.Message)) {
                StringBuilder sb = new StringBuilder();
                sb.append("序号 -- 功能名称\r\n");
                for (QQBotRole role : roles) {
                    sb.append("" + role.id).append("\t").append(role.name).append("\r\n");
                }
                return sb.toString();
            }
            if (msg.Message.startsWith("帮助 ")) {
                int index = Integer.parseInt(msg.Message.substring(3).trim());
                if (index > 0 && index < roles.size()) {
                    return roles.get(index).helpText;
                }
                return "没有找到帮助信息";
            }
        }

        DefaultQQBotExecutor exec = new DefaultQQBotExecutor();
        for (QQBotRole role : roles) {
            try {
                String result = exec.execute(msg, role);
                if (result != null)
                    return result;
            } catch (Exception e) {
                log.info("run role fail = " + role.name, e);
            }
        }
        return "";
    }
    // --------------------------------------------
    //   管理接口
    //--------------------------------------------

    /**
     * 重新加载规则
     */
    @RequiresPermissions("qqbot:role:reload")
    @At
    public void reload() {
        roles = dao.query(QQBotRole.class, Cnd.orderBy().desc("priority"));
    }

    @RequiresPermissions("qqbot:role:add")
    /**
     * 添加规则
     */
    @At
    @POST
    public void add(@Param("..") QQBotRole role) {
        role.priority = dao.count(QQBotRole.class) == 0 ? 1 : dao.getMaxId(QQBotRole.class)+1;
        dao.insert(role);
    }

    @RequiresPermissions("qqbot:role:update")
    /**
     * 更新规则,但不修改优先级
     */
    @At
    @POST
    public void update(@Param("..") QQBotRole role) {
        Daos.ext(dao, FieldFilter.create(QQBotRole.class, "priority"));
    }

    @RequiresPermissions("qqbot:role:update:priority")
    /**
     * 更新优先级
     */
    @At("/change/priority")
    @POST
    public void changePriority(@Param("id") int id, @Param("type") String type) {
        List<QQBotRole> roles = dao.query(QQBotRole.class, Cnd.orderBy().desc("priority"));

        int index = roles.indexOf(dao.fetch(QQBotRole.class, id));
        QQBotRole role = roles.remove(index);
        switch (type) {
            case "+1":
                if (index == 0)
                    return;
                roles.add(index -1, role);
                break;
            case "-1":
                if (index == roles.size())
                    return;
                roles.add(index, role);
                break;
            case "top":
                roles.add(0, role);
                break;
            case "tail":
                roles.add(role);
                break;
        }
        for (int i = 0; i < roles.size(); i++) {
            roles.get(i).priority = i+1;
        }
        Daos.ext(dao, FieldFilter.create(QQBotRole.class, "priority")).update(roles);
    }

    public void init() {
        reload();
        if (roles.isEmpty()) {
            QQBotRole role = new QQBotRole();
            role.name = "帖子搜索";
            role.helpText = "输入关键字即可";
            role.matchType = "class";
            role.matchValue = QQBotTopicSearch.class.getName();
            dao.insert(role);
        }
    }
}
