package net.wendal.nutzbook;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class InjectBySetter {

    @Inject
    public void setDao(Dao dao){}
}
