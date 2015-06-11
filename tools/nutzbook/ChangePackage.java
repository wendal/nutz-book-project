package nutzbook;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;



import java.util.HashSet;
import java.util.Set;

import org.apache.catalina.tribes.util.Arrays;
import org.nutz.lang.Files;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;

public class ChangePackage {

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			help();
			return;
		}
		File source = new File(args[0]);
		if (!source.exists()) {
			System.out.println("源码路径不存在!!");
			return;
		}
		
		final String srcPackage = "net.wendal.nutzbook";
		final String dstPackage = args[2];
		
		//先整体拷贝
		File dst = new File(args[1]);
		boolean re = Files.copyDir(source, dst);
		if (!re) {
			System.out.println("拷贝失败");
			return;
		}
		
		System.out.println("args= " + Arrays.toString(args));
		
		// 先处理src目录
		// 处理 src/net/wendal.net
		File ppp = new File(dst, "src/" + srcPackage.replace(".", "/"));
		File tmp = new File(dst, "src/" + dstPackage.replace(".", "/"));
		Files.makeDir(tmp);
		Files.deleteDir(tmp);
		re = ppp.renameTo(new File(dst, "src/" + dstPackage.replace(".", "/")));
		if (!re) {
			System.out.println("移动package失败");
			return;
		}
		Files.deleteDir(new File(dst, "src/" + srcPackage.replace(".", "/")));
		
		final Set<String> suffix = new HashSet<String>();
		suffix.add("java");
		suffix.add("jsp");
		suffix.add("json");
		suffix.add("js");
		suffix.add("xml");
		suffix.add("properties");
		suffix.add("ini");
		FileFilter ff = new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.isDirectory())
					return true;
				return suffix.contains(Files.getSuffixName(pathname));
			}
		};
		FileVisitor fv = new FileVisitor() {
			public void visit(File file) {
				if (file.isDirectory())
					return;
				System.out.println("Rewrite " + file);
				String origin = Files.read(file);
				String output = origin.replaceAll(srcPackage, dstPackage);
				if (origin.equals(output))
					return;
				Files.write(file, output);
			}
		};
		// 遍历src, conf, WebContent下的文件, 替换package
		Disks.visitFile(new File(dst, "src"), fv, ff);
		Disks.visitFile(new File(dst, "conf"), fv, ff);
		Disks.visitFile(new File(dst, "WebContent"), fv, ff);
	}

	public static void help() {
		System.out.println("参数:    nutzbook源码所在目录  输出目录 新的package名称 ");
		System.out.println("例如:   /opt/nutzbook /opt/dnet org.dnet");
	}
}
