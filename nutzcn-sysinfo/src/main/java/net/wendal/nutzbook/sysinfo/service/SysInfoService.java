package net.wendal.nutzbook.sysinfo.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.resource.Scans;

@IocBean(create="init")
public class SysInfoService {

    @Inject
    protected PropertiesProxy conf;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    protected Map<String, SysInfoProvider> providers = new LinkedHashMap<>();
    
    public Map<String, List<NutMap>> fetch(String match) {
        Pattern pattern = Pattern.compile(match == null ? ".+" : match);
        Map<String, List<NutMap>> re = new LinkedHashMap<>();
        for (Entry<String, SysInfoProvider> en : providers.entrySet()) {
            if (pattern.matcher(en.getKey()).find())
                re.put(en.getValue().name(), en.getValue().fetch());
        }
        return re;
    }
    
    public void init() {
        for (Class<?> klass : Scans.me().scanPackage(getClass().getPackage().getName())) {
            if (SysInfoProvider.class.isAssignableFrom(klass)) {
                if (klass.getAnnotation(IocBean.class) == null)
                    continue;
                SysInfoProvider provider = (SysInfoProvider) ioc.get(klass);
                providers.put(provider.id(), provider);
            }
        }
    }
    
    public void addProvider(SysInfoProvider provider) {
        providers.put(provider.id(), provider);
    }
}
