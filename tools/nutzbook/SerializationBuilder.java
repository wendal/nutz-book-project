package nutzbook;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.resource.Scans;

public class SerializationBuilder {

	public static void main(String[] args) {
		for (Class<?> klass :Scans.me().scanPackage("net.wendal.nutzbook.bean")) {
			if (klass.isEnum())
				continue;
			update(klass, new File("src/" + klass.getName().replace('.', '/') + ".java"));
		}
	}
	
	public static void update(Class<?> klass, File f) {
		System.out.println(klass + " " + f);
		StringBuilder sb = new StringBuilder("//--SerializationBuilder\n");
		StringBuilder write = new StringBuilder("private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {\n");
		StringBuilder read = new StringBuilder("private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{\n");
		write.append("    java.io.DataOutputStream dos = new java.io.DataOutputStream(out);\n");
		read.append("    java.io.DataInputStream dis = new java.io.DataInputStream(in);\n");
		int _c = read.length();
		
		boolean hasEnum = false;
		for (Field field : Mirror.me(klass).getFields()) {
			//System.out.println(field);
			Class<?> t = field.getType();
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			if (Modifier.isTransient(field.getModifiers()))
				continue;
			String name = field.getName();
			if (t.isPrimitive()) {
				write.append("    dos.write").append(Strings.upperFirst(t.getName())).append("(").append(name).append(");\n");
				read.append("    ").append(field.getName()).append(" = dis.read").append(Strings.upperFirst(t.getName())).append("();\n");
			} else if (String.class.equals(t)) {
				write.append(String.format("    dos.writeUTF(%s==null?\"\":%s);\n", name, name));
				read.append("    ").append(name).append(" = dis.readUTF();\n");
			} else if (t.isEnum()) {
				hasEnum = true;
				write.append(String.format("    dos.writeUTF(%s==null?\"\":%s.name());\n", name, name));
				read.append("    _tmp = dis.readUTF(); if (_tmp.length()>0) ").append(field.getName()).append("=").append(t.getName()).append(".valueOf(_tmp);\n");
			} else {
				Mirror<?> m = Mirror.me(t);
				if (m.isDateTimeLike()) {
					write.append("    dos.writeLong(").append(field.getName()).append(" == null ? 0 : ").append(field.getName()).append(".getTime());\n");
					read.append("    ").append(field.getName()).append(" = new ").append(t.getName()).append("(dis.readLong());\n");
				}
			}
		}
		if (hasEnum)
			read.insert(_c, "    String _tmp = null;\n");
		write.append("\n}\n");
		read.append("\n}\n");
		sb.append(write);
		sb.append(read);
		//sb.append("private void readObjectNoData() throws java.io.ObjectStreamException{}\n");
		sb.append("//SerializationBuilder--");
		//System.out.println(sb);
		
		try {
			List<String> re = new ArrayList<String>();
			List<String> list = Files.readAllLines(f.toPath());
			boolean found = false;
			for (String line : list) {
				if (line.contains("//--SerializationBuilder")) {
					found = true;
					break;
				}
				re.add(line);
			}
			if (found) {
				re.add(sb.toString());
				re.add("}");
				Files.write(f.toPath(), re, new OpenOption[0]);
				System.out.println("Updated ->" + f);
			} else {
				System.out.println("Mark not found -->" + f);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
