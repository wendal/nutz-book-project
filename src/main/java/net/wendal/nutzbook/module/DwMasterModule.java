package net.wendal.nutzbook.module;

import java.util.Date;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

import net.wendal.nutzbook.bean.DwRecord;
import net.wendal.nutzbook.util.Toolkit;

/**
 * nutzam.com的下载功能
 * @author wendal
 *
 */
@At("/nutzdw")
@IocBean
@Ok("json:full")
@Fail("http:500")
public class DwMasterModule extends BaseModule {
    
    @Inject
    protected PropertiesProxy conf;

    @RequiresRoles("admin")
    @At
    @POST
    public NutMap add(@Param("..")DwRecord dw) {
        DwRecord re = dao.fetch(DwRecord.class, dw.getName());
        if (re != null) {
            dw.setId(re.getId());
            dw.setUpdateTime(new Date());
            dao.update(dw, "md5|updateTime");
        } 
        else
            dao.insert(dw);
        return new NutMap("ok", true);
    }

    @Ok("json:full")
    @At
    @RequiresRoles("admin")
    public NutMap uptoken() {
        String accessKey = conf.get("qiniu.accessKey");
        String secretKey = conf.get("qiniu.secretKey");
        String bucketName = conf.get("qiniu.bucketName");
        Auth auth = Auth.create(accessKey, secretKey);
        StringMap policy = new StringMap(); // 可选,详情请查阅七牛的文档
        String token = auth.uploadToken(bucketName, null, 600, policy);
        return new NutMap("uptoken", token);
    }
    
    @RequiresRoles("admin")
    @DELETE
    @At("/?")
    @Ok("void")
    public void delete(String id) {
        dao.delete(DwRecord.class, id);
    }
    
    @RequiresRoles("admin")
    @POST
    @At("/?")
    @Ok("void")
    public void update(int id, @Param("..")DwRecord re) {
        re.setId(id);
        re.setUpdateTime(new Date());
        re.setCreateTime(null);
        dao.updateIgnoreNull(re);
    }
    
    @At
    public NutMap list(@Param("..")Pager pager) {
        NutMap map = new NutMap("ok", true);
        map.put("data", dao.query(DwRecord.class, Cnd.orderBy().desc("updateTime"), pager));
        return map;
    }
    
    @Ok("beetl:/yvr/nutzdw/nutzdw_index.html")
    @At("/")
    public NutMap index() {
        int userId = Toolkit.uid();
        if (userId > 0)
            return new NutMap("current_user", fetch_userprofile(userId));
        return new NutMap();
    }
}
