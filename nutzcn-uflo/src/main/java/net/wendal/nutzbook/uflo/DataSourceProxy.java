package net.wendal.nutzbook.uflo;

import javax.sql.DataSource;

import org.nutz.mvc.Mvcs;
import org.springframework.beans.factory.FactoryBean;

/**
 * 为Spring代理NutIoc内的DataSource
 * @author wendal
 *
 */
public class DataSourceProxy implements FactoryBean<DataSource> {

    public DataSource getObject() throws Exception {
        return Mvcs.ctx().getDefaultIoc().get(DataSource.class);
    }

    public Class<?> getObjectType() {
        return DataSource.class;
    }

    public boolean isSingleton() {
        return true;
    }

}
