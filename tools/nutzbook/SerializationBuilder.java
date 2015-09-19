package nutzbook;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.resource.Scans;

public class SerializationBuilder {

	public static void main(String[] args) {
		for (Class<?> klass :Scans.me().scanPackage("net.wendal.nutzbook.bean.yvr")) {
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
		read.append("    String _tmp = null;\n");
		
		for (Field field : Mirror.me(klass).getFields()) {
			//System.out.println(field);
			Class<?> t = field.getType();
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			if (Modifier.isTransient(field.getModifiers()))
				continue;
			
			if (t.isPrimitive()) {
				write.append("    dos.write").append(Strings.upperFirst(t.getName())).append("(").append(field.getName()).append(");\n");
				read.append("    ").append(field.getName()).append(" = dis.read").append(Strings.upperFirst(t.getName())).append("();\n");
			} else if (String.class.equals(t)) {
				write.append("    dos.writeUTF(").append(field.getName()).append(");\n");
				read.append("    ").append(field.getName()).append(" = dis.readUTF();\n");
			} else if (t.isEnum()) {
				write.append("    dos.writeUTF(").append(field.getName()).append(" == null ? null : ").append(field.getName()).append(".name());\n");
				read.append("    _tmp = dis.readUTF(); if (_tmp != null) ").append(field.getName()).append("=").append(t.getName()).append(".valueOf(_tmp);\n");
			} else {
				Mirror<?> m = Mirror.me(t);
				if (m.isDateTimeLike()) {
					write.append("    dos.writeLong(").append(field.getName()).append(" == null ? 0 : ").append(field.getName()).append(".getTime());\n");
					read.append("    ").append(field.getName()).append(" = new ").append(t.getName()).append("(dis.readLong());\n");
				}
			}
		}
		write.append("\n}\n");
		read.append("\n}\n");
		sb.append(write);
		sb.append(read);
		sb.append("private void readObjectNoData() throws java.io.ObjectStreamException{}\n");
		sb.append("//SerializationBuilder--");
		System.out.println(sb);
	}
}
