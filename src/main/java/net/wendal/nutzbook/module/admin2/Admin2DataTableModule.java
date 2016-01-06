package net.wendal.nutzbook.module.admin2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.admin.DataTableColumn;
import net.wendal.nutzbook.bean.admin.DataTableOrder;
import net.wendal.nutzbook.bean.admin.DataTableSearch;
import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.util.OffsetPager;

import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.resource.Scans;

/**
 *
 * Created by wendal on 2015/12/20.
 */
@IocBean(create = "init")
@At("/admin/datatable")
@Ok("json:full")
public class Admin2DataTableModule extends BaseModule {

    private static Log log = Logs.get();

    Map<String, Entity<?>> maps = new HashMap<>();

    @At("/query/?")
    public Object query(String type,
                        @Param("..")Map<String, Object> params,
                        @Param("::columns")List<DataTableColumn> columns,
                        @Param("::order")List<DataTableOrder> orders,
                        @Param("start")int start,
                        @Param("length")int length,
                        @Param("::search")DataTableSearch search,
                        @Param("draw")int draw
    ) {
        log.debug("params="+params);
        log.debug("columns="+columns);
        log.debug("orders="+orders);
        log.debug("start="+start);
        log.debug("length="+length);
        log.debug("search="+search);
        if ("user".equals(type)) {
            return HTTP_403;
        }
        Entity<?> en = maps.get(type.toLowerCase());
        if (en == null) {
            return HTTP_404;
        }
        Class<?> klass = en.getType();
        NutMap re = new NutMap();
        re.put("recordsTotal", dao.count(klass));
        Cnd cnd = Cnd.NEW();

        if (!Strings.isBlank(search.getValue())) {
            for (MappingField mf: en.getMappingFields()) {
                if (!mf.getTypeMirror().isStringLike()) {
                    continue;
                }
                cnd.or(mf.getName(), "like", "%"+search.getValue().trim() + "%");
            }
        }

        if (orders != null && orders.size() > 0) {
            for (DataTableOrder order: orders) {
                DataTableColumn col = columns.get(order.getColumn());
                cnd.orderBy(Sqls.escapeSqlFieldValue(col.getData()).toString(), order.getDir());
            }
        }
        Pager pager = new OffsetPager(start, length);
        re.put("recordsFiltered", dao.count(klass, cnd));
        List<?> list = dao.query(klass, cnd, pager);
        dao.fetchLinks(list, null);
        re.put("list", list);
        re.put("draw", draw);
        return re;
    }

    public void init() throws Exception{
        super.init();
        for(Class<?> klass :Scans.me().scanPackage(UserProfile.class)){
            if (klass.getAnnotation(Table.class) == null)
                continue;
            maps.put(klass.getSimpleName().toLowerCase(), dao.getEntity(klass));
        }
    }
}
