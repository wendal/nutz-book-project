package net.wendal.nutzbook.common.beetl;

import org.beetl.core.resource.ClasspathResourceLoader;
import org.nutz.lang.util.Disks;

public class ClasspathResourceLoader2 extends ClasspathResourceLoader {

    public ClasspathResourceLoader2() {
        // TODO Auto-generated constructor stub
    }

    public ClasspathResourceLoader2(ClassLoader classLoader) {
        super(classLoader);
        // TODO Auto-generated constructor stub
    }

    public ClasspathResourceLoader2(String root) {
        super(root);
        // TODO Auto-generated constructor stub
    }

    public ClasspathResourceLoader2(ClassLoader classLoader, String root) {
        super(classLoader, root);
        // TODO Auto-generated constructor stub
    }

    public ClasspathResourceLoader2(String root, String charset) {
        super(root, charset);
        // TODO Auto-generated constructor stub
    }

    public ClasspathResourceLoader2(ClassLoader classLoader, String root, String charset) {
        super(classLoader, root, charset);
        // TODO Auto-generated constructor stub
    }

    protected String getChildPath(String path,String child){
        path = super.getChildPath(path, child);
        path = Disks.getCanonicalPath(path);
        System.out.println("路径是 ---> " + path);
        return path;
    }
}
