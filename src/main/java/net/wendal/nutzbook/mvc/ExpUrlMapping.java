package net.wendal.nutzbook.mvc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.ObjectInfo;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.impl.ActionInvoker;
import org.nutz.mvc.impl.UrlMappingImpl;
import org.nutz.mvc.view.UTF8JsonView;

import net.wendal.nutzbook.annotation.Api;

/**
 * 
 * @author wendal
 *
 */
public class ExpUrlMapping extends UrlMappingImpl {

    /**
     * 按类(或组?)分类排好的列表
     */
    protected LinkedHashMap<String, ExpClass> infos = new LinkedHashMap<>();

    private static final Log log = Logs.get();

    protected static String[] EMTRY = new String[0];

    public void add(ActionChainMaker maker, ActionInfo ai, NutConfig config) {
        super.add(maker, ai, config);
        _add(maker, ai, config);
    }

    public ActionInvoker get(ActionContext ac) {
        // 如果是读取expPath,俺就自行处理了
        if (expPath.equals(Mvcs.getRequestPath(ac.getRequest())))
            return docInvoker;
        return super.get(ac);
    }

    protected View view = new UTF8JsonView(JsonFormat.full());
    protected ActionInvoker docInvoker = new DocActionInvoker();
    protected String expPath = "/_/exp";

    class DocActionInvoker extends ActionInvoker {
        public boolean invoke(ActionContext ac) {
            try {
                ac.getResponse().setCharacterEncoding("UTF-8");
                view.render(ac.getRequest(), ac.getResponse(), new NutMap("data", infos));
            }
            catch (Throwable e) {
                log.debug("exp fail", e);
            }
            return true;
        }
    }

    protected static class ExpClass {
        String name;
        String typeName;
        String description;
        String iocName;
        String[] pathPrefixs;
        List<ExpMethod> methods = new ArrayList<>();
        // TODO 是不是应该加上作者
    }

    protected static class ExpMethod extends ActionInfo {
        protected List<ExpParam> params = new ArrayList<>();

        public String toJson(JsonFormat jf) {
            NutMap map = new NutMap();
            map.put("chainName", getChainName());
            map.put("className", getModuleType().getName());
            map.put("okView", getOkView());
            map.put("failView", getFailView());
            map.put("httpMethods", getHttpMethods());
            map.put("lineNumber", getLineNumber());
            map.put("paths", getPaths());

            map.put("methodName", getMethod().getName());
            if (getAdaptorInfo() != null)
                map.put("adaptorName", getAdaptorInfo().getType().getSimpleName());
            ObjectInfo<? extends ActionFilter>[] filters = getFilterInfos();
            if (filters == null)
                map.put("filters", EMTRY);
            else {
                List<String> filterNames = new ArrayList<>();
                for (ObjectInfo<? extends ActionFilter> objectInfo : filters) {
                    filterNames.add(objectInfo.getType().getSimpleName());
                }
                map.put("filters", filterNames);
            }
            map.put("params", params);
            return Json.toJson(map, jf);
        }
    }

    /**
     * 方法参数
     * 
     * @author wendal
     *
     */
    protected static class ExpParam {
        String paramName;
        String paramVale;
        String defaultValue;
        String defaultDateFormat;
        String className;
        String attrName;
    }

    protected void _add(ActionChainMaker maker, ActionInfo ai, NutConfig config) {
        ExpMethod info = new ExpMethod();
        Lang.copyProperties(ai, info);
        String typeName = info.getModuleType().getName();
        ExpClass expClass = infos.get(typeName);
        if (expClass == null) {
            Class<?> klass = ai.getModuleType();
            expClass = new ExpClass();
            expClass.typeName = typeName;
            IocBean ib = klass.getAnnotation(IocBean.class);
            if (ib != null)
                expClass.iocName = Strings.isBlank(ib.name()) ? Strings.lowerFirst(klass.getSimpleName())
                                                              : ib.name();
            else
                expClass.iocName = "";
            Api api = klass.getAnnotation(Api.class);
            if (api != null) {
                expClass.name = api.name();
                expClass.description = api.description();
            }
            if (Strings.isBlank(expClass.name))
                expClass.name = klass.getSimpleName();
            At at = klass.getAnnotation(At.class);
            if (at != null)
                expClass.pathPrefixs = at.value();
            else
                expClass.pathPrefixs = new String[0];

            infos.put(typeName, expClass);
        }
        // TODO 还得解析参数
        expClass.methods.add(info);
    }
}
