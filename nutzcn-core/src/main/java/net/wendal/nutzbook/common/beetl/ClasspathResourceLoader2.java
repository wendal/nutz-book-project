package net.wendal.nutzbook.common.beetl;

import java.util.LinkedList;

import org.beetl.core.resource.ClasspathResourceLoader;

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
        // 首先, 与父路径拼接起来
        String re = null;
        if(child.length()==0){
            return path;
        }
        else if(child.startsWith("/")){
            re = path+child;          
        }
        else{
            re = path+"/"+child;
        }
        // 如果不包含`../` 或者 `./`,那就无需处理了
        if (!re.contains("./"))
            return re;
        // 分解成一段一段的路径
        String[] tmp = re.split("[\\\\/]");
        LinkedList<String> list = new LinkedList<>();
        for (String str : tmp) {
            if (str == null || str.isEmpty())
                continue;
            if ("..".equals(str)) { // 向上退一层
                int sz = list.size();
                if (sz > 0) // 预防顶部异常
                    list.remove(sz - 1);
            }
            else if (".".equals(str))
                continue;
            else {
                list.add(str);
            }
        }
        // 没有一个值? 返回空吧
        if (list.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str).append('/');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    
    public static void main(String[] args) {
        ClasspathResourceLoader2 loader = new ClasspathResourceLoader2();
        System.out.println(loader.getChildPath("a/b/c", "/d"));
        System.out.println(loader.getChildPath("a/b/c", "../d"));
        System.out.println(loader.getChildPath("a/b/c", "../../d"));
        System.out.println(loader.getChildPath("a/b/c", "../../../d"));
        System.out.println(loader.getChildPath("a/b/c", "../../../../../../../../d"));
        

        System.out.println(loader.getChildPath("a/b/c", "/d"));
        System.out.println(loader.getChildPath("a/b/c", "/../d"));
        System.out.println(loader.getChildPath("a/b/c", "/../../d"));
        System.out.println(loader.getChildPath("a/b/c", "/../../../d"));
        System.out.println(loader.getChildPath("a/b/c", "/../../../../../../../../d"));
        

        System.out.println(loader.getChildPath("a/b/c", "./d"));
        System.out.println(loader.getChildPath("a/b/c", "./../d"));
        System.out.println(loader.getChildPath("a/b/c", "./../../d"));
        System.out.println(loader.getChildPath("a/b/c", "./../../../d"));
        System.out.println(loader.getChildPath("a/b/c", "./../../../../../../../../d"));
    }
}
