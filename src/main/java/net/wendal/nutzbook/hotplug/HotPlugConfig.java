package net.wendal.nutzbook.hotplug;

import java.util.HashMap;

import org.nutz.ioc.IocLoader;
import org.nutz.mvc.UrlMapping;

public class HotPlugConfig {

    /**
     * 插件的唯一id
     */
    protected String name;
    /**
     * 插件必须有自己的顶层package
     */
    protected String base;
    /**
     * 插件自身的MainModule,只有少量注解会生效,通常只@Ok和@Fail.
     */
    protected String main;
    /**
     * 插件的Setup实现类,还没用上
     */
    protected String setup;
    /**
     * 映射类别
     */
    protected UrlMapping urlMapping;
    /**
     * IocLoader,即Ioc配置加载器
     */
    protected IocLoader iocLoader;
    /**
     * 资源文件,例如js/css/png等,还没搞定,应该是弄个入口方法什么的来提供服务
     */
    protected HashMap<String, byte[]> asserts;
    /**
     * 模板数据,一般都是文本吧!!
     */
    protected HashMap<String, String> tmpls;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBase() {
        return base;
    }
    public void setBase(String base) {
        this.base = base;
    }
    public String getMain() {
        return main;
    }
    public void setMain(String main) {
        this.main = main;
    }
    public String getSetup() {
        return setup;
    }
    public void setSetup(String setup) {
        this.setup = setup;
    }
    public UrlMapping getUrlMapping() {
        return urlMapping;
    }
    public void setUrlMapping(UrlMapping urlMapping) {
        this.urlMapping = urlMapping;
    }
    public IocLoader getIocLoader() {
        return iocLoader;
    }
    public void setIocLoader(IocLoader iocLoader) {
        this.iocLoader = iocLoader;
    }
    public HashMap<String, byte[]> getAsserts() {
        return asserts;
    }
    public void setAsserts(HashMap<String, byte[]> asserts) {
        this.asserts = asserts;
    }
    public HashMap<String, String> getTmpls() {
        return tmpls;
    }
    public void setTmpls(HashMap<String, String> tmpls) {
        this.tmpls = tmpls;
    }
}
