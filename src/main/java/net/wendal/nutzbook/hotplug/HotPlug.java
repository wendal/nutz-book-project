package net.wendal.nutzbook.hotplug;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.ScopeContext;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.UrlMapping;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.impl.NutLoading;
import org.nutz.resource.Scans;
import org.nutz.resource.impl.JarResourceLocation;

/**
 * 动态增减web插件,新增映射关系(@At相关), Ioc相关, 模板相关. <p/>
 * 
 * 用法:  <p/>
 * 主项目的MainModule标注<code>@LoadingBy(HotPlug.class)</code><p/>
 * 插件项目的格式要求: 需要一个XXXMainModule, 必须带一个hotplug.XXX.json文件,且文件格式如下<p/>
 * <code>{name:"cms", "version":"1.0", "base": "net.wendal.nutzbook.cms", "main":"net.wendal.nutzbook.cms.CmsMainModule"}</code>
 * @author wendal
 *
 */
@SuppressWarnings("unchecked")
public class HotPlug extends NutLoading {
    
    private static final Log log = Logs.get();

    /**
     * 保持已有的Ioc容器,映射插件的@At的时候需要用到
     */
    protected Ioc ioc;
    //-----------------------------------------------------
    // 为了动态增减ioc内的对象,需要hack一下NutIoc内的私有属性
    protected ScopeContext scopeContext;
    protected List<IocLoader> comboIocLoader_iocLoaders;
    //-----------------------------------------------------
    
    /**
     *  主项目的配置对象
     */
    protected NutConfig config;
    
    /**
     * 代理原有的映射关系,优先使用插件的映射关系
     */
    protected UrlMappingProxy ump;
    
    /**
     * 插件列表,为了方便,这里直接用静态属性了
     */
    public static Map<String, HotPlugConfig> plugins = new LinkedHashMap<>();
    
    /**
     * 主项目的@Ok/@Fail处理类,新增插件时需要用到.
     */
    protected ViewMaker[] views;
    
    @Override
    public UrlMapping load(NutConfig config) {
        UrlMapping um = super.load(config);
        this.config = config; // 保存起来,后面会用到
        ump = new UrlMappingProxy(um); // 代理之
        return ump;
    }
    
    protected Ioc createIoc(NutConfig config, Class<?> mainModule) throws Exception {
        ioc = super.createIoc(config, mainModule);
        if (ioc == null)
            throw new RuntimeException("Ioc is needed!");
        if (!(ioc instanceof NutIoc)) {
            throw new RuntimeException("only NutIoc is supported");
        }
        // 将自身放入ioc容器, 这样就能通过主项目的入口方法调用本类的方法
        ((NutIoc)ioc).getIocContext().save("app", "hotPlug", new ObjectProxy(this));
        //-------------------------------------------------------------
        // 以下是hack一下NutIoc内部属性的代码
        Mirror<NutIoc> mirror = Mirror.me(NutIoc.class);
        // 取出Ioc上下文,目的是卸载插件时,把相关的ioc bean移除掉
        scopeContext = (ScopeContext)mirror.getValue(ioc, "context");
        // ioc loader列表页需要改一下, 新增插件和卸载插件,都直接去操作它的内部列表好了
        ComboIocLoader comboIocLoader = (ComboIocLoader) mirror.getValue(ioc, "loader");
        comboIocLoader_iocLoaders = (List<IocLoader>) Mirror.me(comboIocLoader).getValue(comboIocLoader, "iocLoaders");
        return ioc;
    }
    
    protected ViewMaker[] createViewMakers(Class<?> mainModule, Ioc ioc) throws Exception {
        if (views == null) //保持主项目的@Ok/@Fail处理器
            views = super.createViewMakers(mainModule, ioc);
        return views;
    }
    
    /**
     * 载入一个插件,必须符合特定的jar包
     * @param f 插件jar文件,通过文件上传/数据库读取等方式保存到本地,然后调用本方法
     * @return 插件信息
     * @throws Exception
     */
    public HotPlugConfig add(File f) throws Exception {
        // 首先,我们需要解析这个jar. Jar文件也是Zip. 解析完成前,还不会影响到现有系统的运行
        ZipFile zf = new ZipFile(f);
        Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zf.entries();
        HashMap<String, byte[]> asserts = new HashMap<>();
        HashMap<String, String> tmpls = new HashMap<>();
        HotPlugConfig hc = null;
        while (en.hasMoreElements()) {
            ZipEntry ze = en.nextElement();
            String name = ze.getName();
            if (name.endsWith("/"))
                continue;
            if (name.startsWith("META-INF/hotplug.") && name.endsWith(".json"))  {
                String j = new String(Streams.readBytes(zf.getInputStream(ze)));
                hc = Json.fromJson(HotPlugConfig.class, j);
            }
            else if (name.startsWith("asserts/")) {
                asserts.put(name.substring("asserts/".length()), Streams.readBytes(zf.getInputStream(ze)));
            } else if (name.startsWith("tmpls/")) {
                tmpls.put(name.substring("tmpls/".length()), new String(Streams.readBytes(zf.getInputStream(ze))));
            }
        }
        zf.close();
        hc.asserts = asserts;
        hc.tmpls = tmpls;
        // 解析完成, 开始影响现有系统. TODO 下面的代码应该用try-catch整体包起来
        // -----------------------------------------------------
        
        // 放入插件列表, 因为tmpls已经生效,所以会影响beetl的模板加载系统(其实嘛,一点问题没有...)
        plugins.put(hc.name, hc);
        
        // 构建URLClassLoader,开始加载这个jar包
        URLClassLoader classLoader = new URLClassLoader(new URL[]{f.toURI().toURL()}, getClass().getClassLoader());
        
        // 先保存当前的类加载器
        ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // 将其设置为线程上下文的ClassLoader, 这样才能是下面的资源扫描和类扫描生效
            Thread.currentThread().setContextClassLoader(classLoader);
            // 添加资源扫描路径,为下面的类扫描打下基础, 这里开始影响"资源扫描子系统",其实也是没一点问题...
            Scans.me().addResourceLocation(new JarResourceLocation(f.getAbsolutePath()));
            // 加载插件的MainModule类
            Class<?> klass = classLoader.loadClass(hc.main);
            // 加载旗下的@IocBean
            hc.iocLoader = new AnnotationIocLoader(hc.base);
            // 放入NutIoc的Ioc加载器列表,开始影响"Ioc子系统", 恩, 一般情况下很好.
            comboIocLoader_iocLoaders.add(hc.iocLoader);
            // 生成插件的URL映射, 即@At的配置
            UrlMapping um = evalUrlMapping(config, klass, ioc);
            // 赋值给hc,然后让ump添加映射表
            hc.urlMapping = um;
            // TODO 还需要执行一下hc.setup,让插件自行初始化一下
            if (um != null) {
                // 正式对外服务的开始, 插件相关的URL可以访问了
                ump.add(hc);
            }
        } finally {
            // 还原ClassLoader
            Thread.currentThread().setContextClassLoader(prevClassLoader);
        }
        return hc;
    }
    
    public void remove(String key) {
        HotPlugConfig hc = plugins.get(key);
        if (hc == null) {
            return;
        }
        // 移除URL映射, 对外服务停止.
        ump.remove(key);
        // 移除出插件列表, beetl的模板加载似乎不会变. 
        plugins.remove(key);
        // 如果存在iocLoader,清理一下. 为空的可能性,只有初始化过程中抛出异常,但,还没写呢...
        if (hc.iocLoader != null) {
            // 变量所持有的ioc bean,逐一销毁
            for (String beanName : hc.iocLoader.getName()) {
                try {
                    ObjectProxy op = scopeContext.fetch(beanName);
                    if (op == null)
                        continue;
                    op.depose();
                }
                catch (Exception e) {
                    log.debug("depose hotplug bean fail", e);
                }
            }
            // 移除ioc上下文中存在的ioc bean
            for (String beanName : hc.iocLoader.getName()) {
                scopeContext.remove("app", beanName);
            }
            // 移除加载器
            comboIocLoader_iocLoaders.remove(hc.iocLoader);
        }
    }
    
}
