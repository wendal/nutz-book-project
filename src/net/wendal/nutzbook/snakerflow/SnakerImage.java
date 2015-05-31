package net.wendal.nutzbook.snakerflow;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nutz.lang.Xmls;
import org.snaker.engine.SnakerException;
import org.snaker.engine.entity.Process;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SnakerImage {

	public SnakerImage(){}

	protected int defaultTaskW = 100;
	protected int defaultTaskH = 50;
	protected int defaultOtherW = 48;
	protected int defaultOtherH = 48;
	
	public BufferedImage render(Process p) throws Exception {
		return render(new ByteArrayInputStream(p.getDBContent()));
	}
	
	public BufferedImage render(File f) throws Exception {
		return render(new FileInputStream(f));
	}
	
	public BufferedImage render(InputStream ins) throws Exception {
		Document doc = Xmls.xmls().parse(ins);
		doc.normalizeDocument();
		return render(doc.getDocumentElement());
	}
	
	public BufferedImage render(Element root) throws Exception {
		if (!"process".equals(root.getNodeName()))
			throw new SnakerException("Not snakerflow xml");
		// 首先, 得出所有节点的位置
		Map<String, ProcessNode> nodes = new LinkedHashMap<String, ProcessNode>();
		NodeList NL = root.getChildNodes();
		for (int i = 0; i < NL.getLength(); i++) {
			Node node = NL.item(i);
			if (node instanceof Element) {
				Element ele = (Element)node;
				ProcessNode pn = new ProcessNode();
				pn.type = ele.getNodeName();
				pn.name = ele.getAttribute("name");
				pn.displayName = ele.getAttribute("displayName");
				String layout = ele.getAttribute("layout");
				if (layout == null) {
					throw new SnakerException("bad layout at name="+pn.name);
				}
				String[] tmp = layout.split(",");
				if (tmp.length != 4) {
					throw new SnakerException("bad layout at name="+pn.name);
				}
				pn.left = Integer.parseInt(tmp[0]);
				pn.top = Integer.parseInt(tmp[0]);
				pn.w = Integer.parseInt(tmp[0]);
				pn.h = Integer.parseInt(tmp[0]);
				if (pn.w < 1) {
					pn.w = "task".equals(pn.type) ? defaultTaskW : defaultOtherW;
				}
				if (pn.h < 1) {
					pn.h = "task".equals(pn.type) ? defaultTaskH : defaultOtherH;
				}
				nodes.put(pn.name, pn);
				// 继续遍历transition
				NodeList cl = ele.getChildNodes();
				for (int j = 0; j < cl.getLength(); j++) {
					Node t = cl.item(j);
					if (t instanceof Element && "transition".equals(t.getNodeName())) {
						
					}
				}
			}
		}
		
		
		return null;
	}
	
}

class ProcessNode {
	public String name;
	public String displayName;
	public String type;
	public int w;
	public int h;
	public int top;
	public int left;
}

class TransLine {
	public String from;
	public String to;
	public int[][] g;
}