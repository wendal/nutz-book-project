package net.wendal.nutzbook.module;

import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.PUT;
import org.nutz.mvc.annotation.Param;
import org.nutz.service.IdNameEntityService;

@IocBean
@Ok("json:full")
public abstract class VueResourceModule<T> extends BaseModule {
    
    protected Class<T> entityClass;
    
    protected IdNameEntityService<T> entityService;
    
    @SuppressWarnings("unchecked")
    public VueResourceModule() {
        entityClass = (Class<T>) Mirror.getTypeParam(getClass(), 0);
    }
    
    public void setDao(Dao dao) {
        super.dao = dao;
        entityService = new IdNameEntityService<T>(dao, entityClass) {};
    }

    @GET
    @At("/?")
    public T get(String id) {
        return entityService.smartFetch(id);
    }
    
    @POST
    @At("/?")
    public void save(String id, @Param("..")NutMap map) {
        
    }
    
    @GET
    @At("/")
    public void query(@Param("..")Pager pager) {
        
    }
    
    @PUT
    @At("/?")
    public void update(String id, @Param("..")NutMap map) {
        
    }
    
    @DELETE
    @At("/?")
    public void remove(String id) {
        
    }
    
}
