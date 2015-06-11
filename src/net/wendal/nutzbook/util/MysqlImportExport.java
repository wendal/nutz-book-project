package net.wendal.nutzbook.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MysqlImportExport {

	public int Export(String exec, Properties prop, OutputStream out) {
		List<String> cmds = new ArrayList<String>();
		cmds.add(prop.getProperty("exec", "mysqldump"));
		cmds.add("-C");
		if (prop.getProperty("user") != null) {
			cmds.add("-u" + prop.getProperty("root"));
		}
		if (prop.getProperty("password") != null) {
			cmds.add("-p" + prop.getProperty("password"));
		}
		if (prop.getProperty("host") != null) {
			cmds.add("-h");
			cmds.add(prop.getProperty("host"));
		}
		cmds.add(prop.getProperty("database"));
		try {
			Process p = Runtime.getRuntime().exec(cmds.toArray(new String[cmds.size()]));
			byte[] buf = new byte[128*1024];
			int len = 0;
			InputStream in = p.getInputStream();
			while ((len = in.read(buf)) != -1) {
				if (len > 0) 
					out.write(buf, 0, len);
			}
			return p.waitFor();
		} catch (Exception e) {
			return -1;
		}
	}
	
	public void Import(String exec, String database, File file, List<String> tables) {
		
	}
}
