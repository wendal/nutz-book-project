package net.wendal.nutzbook.yvr.beetl;

import org.beetl.core.resource.ClasspathResourceLoader;
import org.nutz.lang.util.Disks;

public class ClasspathResourceLoader2 extends ClasspathResourceLoader {
    
    protected String getChildPath(String path,String child){
        String re = path;
        if(child.length()==0){
            re = path;
        }else if(child.startsWith("/")){
            re = path+child;          
        }else{
            re = path+"/"+child;
        }
        return Disks.getCanonicalPath(re);
    }
}
