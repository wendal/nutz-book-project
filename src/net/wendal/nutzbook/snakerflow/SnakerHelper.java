/*
 *  Copyright 2013-2015 www.snakerflow.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package net.wendal.nutzbook.snakerflow;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wendal.nutzbook.snakerflow.meta.NodeLayout;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.nutz.img.Images;
import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.snaker.engine.entity.Task;
import org.snaker.engine.model.CustomModel;
import org.snaker.engine.model.DecisionModel;
import org.snaker.engine.model.EndModel;
import org.snaker.engine.model.ForkModel;
import org.snaker.engine.model.JoinModel;
import org.snaker.engine.model.NodeModel;
import org.snaker.engine.model.ProcessModel;
import org.snaker.engine.model.StartModel;
import org.snaker.engine.model.SubProcessModel;
import org.snaker.engine.model.TaskModel;
import org.snaker.engine.model.TransitionModel;

/**
 * Snaker的帮助类
 * @author yuqs
 * @since 0.1
 * @author wendal 调整代码并添加流程图的图像的输出
 */
public class SnakerHelper {
	
	public static Map<Class<? extends NodeModel>, String> mapper = new HashMap<Class<? extends NodeModel>, String>();
	static {
		mapper.put(TaskModel.class, "task");
		mapper.put(CustomModel.class, "custom");
		mapper.put(DecisionModel.class, "decision");
		mapper.put(EndModel.class, "end");
		mapper.put(ForkModel.class, "fork");
		mapper.put(JoinModel.class, "join");
		mapper.put(StartModel.class, "start");
		mapper.put(SubProcessModel.class, "subprocess");
	}
	public static String getActiveJson(List<Task> tasks) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{'activeRects':{'rects':[");
		for(Task task : tasks) {
			buffer.append("{'paths':[],'name':'");
			buffer.append(task.getTaskName());
			buffer.append("'},");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append("]}}");
		buffer.append("");
		buffer.append("");
		return buffer.toString();
	}
	
	// TODO 这个拼json的方法很坑爹, 高版本的jquery无法识别,需要改进.
	public static String getModelJson(ProcessModel model) {
		StringBuffer buffer = new StringBuffer();
		List<TransitionModel> tms = new ArrayList<TransitionModel>();
		for(NodeModel node : model.getNodes()) {
			for(TransitionModel tm : node.getOutputs()) {
				tms.add(tm);
			}
		}
		buffer.append("{");
		buffer.append(getNodeJson(model.getNodes()));
		buffer.append(getPathJson(tms));
		buffer.append("props:{props:{name:{name:'name',value:'");
		buffer.append(convert(model.getName()));
		buffer.append("'},displayName:{name:'displayName',value:'");
		buffer.append(convert(model.getDisplayName()));
		buffer.append("'},expireTime:{name:'expireTime',value:'");
		buffer.append(convert(model.getExpireTime()));
		buffer.append("'},instanceUrl:{name:'instanceUrl',value:'");
		buffer.append(convert(model.getInstanceUrl()));
		buffer.append("'},instanceNoClass:{name:'instanceNoClass',value:'");
		buffer.append(convert(model.getInstanceNoClass()));
		buffer.append("'}}}}");
		return buffer.toString();
	}
	public static String getNodeJson(List<NodeModel> nodes) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("states: {");
		for(NodeModel node : nodes) {
			buffer.append(node.getName());
			buffer.append(getBase(node));
			buffer.append(getLayout(node));
			buffer.append(getProperty(node));
			buffer.append(",");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append("},");
		return buffer.toString();
	}
	
	public static String getPathJson(List<TransitionModel> tms) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("paths:{");
		for(TransitionModel tm : tms) {
			buffer.append(tm.getName());
			buffer.append(":{from:'");
			buffer.append(tm.getSource().getName());
			buffer.append("',to:'");
			buffer.append(tm.getTarget().getName());
			buffer.append("', dots:[");
			if(StringUtils.isNotEmpty(tm.getG())) {
		        String[] bendpoints = tm.getG().split(";");
		        for (String bendpoint: bendpoints) {
		        	buffer.append("{");
		            String[] xy = bendpoint.split(",");
		            buffer.append("x:").append(getNumber(xy[0]));
		            buffer.append(",y:").append(xy[1]);
		            buffer.append("},");
		        }
		        buffer.deleteCharAt(buffer.length() - 1);
			}
			buffer.append("],text:{text:'");
			buffer.append(tm.getDisplayName());
			buffer.append("'},textPos:{");
			if(StringUtils.isNotEmpty(tm.getOffset())) {
				String[] values = tm.getOffset().split(",");
				buffer.append("x:").append(values[0]).append(",");
				buffer.append("y:").append(values[1]).append("");
			}
			buffer.append("}, props:{name:{value:'" + tm.getName() + "'},expr:{value:'" + tm.getExpr() + "'}}}");
			buffer.append(",");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append("},");
		return buffer.toString();
	}
	
	private static String getBase(NodeModel node) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(":{type:'");
		buffer.append(mapper.get(node.getClass()));
		buffer.append("',text:{text:'");
		buffer.append(node.getDisplayName());
		buffer.append("'},");
		return buffer.toString();
	}
	
	private static String getProperty(NodeModel node) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("props:{");
		try {
		PropertyDescriptor[] beanProperties = PropertyUtils.getPropertyDescriptors(node);
		for (PropertyDescriptor propertyDescriptor : beanProperties) {
			if(propertyDescriptor.getReadMethod() == null || propertyDescriptor.getWriteMethod() == null)
				continue;
			String name = propertyDescriptor.getName();
			String value = "";
			if(propertyDescriptor.getPropertyType() == String.class) {
				value = (String)BeanUtils.getProperty(node, name);
			} else {
				continue;
			}
			if(value == null || value.equals("")) continue;
			buffer.append(name);
			buffer.append(":{value:'");
			buffer.append(convert(value));
			buffer.append("'},");
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append("}}");
		return buffer.toString();
	}
	
	public static String getLayout(NodeModel node) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("attr:{");
		String[] values = node.getLayout().split(",");
		buffer.append("x:").append(getNumber(values[0])).append(",");
		buffer.append("y:").append(values[1]).append(",");
		if("-1".equals(values[2])) {
			if(node instanceof TaskModel || node instanceof CustomModel || node instanceof SubProcessModel) {
				values[2] = "100";
			} else {
				values[2] = "50";
			}
		}
		if("-1".equals(values[3])) {
			values[3] = "50";
		}
		buffer.append("width:").append(values[2]).append(",");
		buffer.append("height:").append(values[3]);
		buffer.append("},");
		return buffer.toString();
	}
	
	private static String convert(String value) {
		if (StringUtils.isEmpty(value))
			return "";
		if (value.indexOf("'") != -1) {
			value = value.replaceAll("'", "#1");
		}
		if (value.indexOf("\"") != -1) {
			value = value.replaceAll("\"", "#2");
		}
		if (value.indexOf("\r\n") != -1) {
			value = value.replaceAll("\r\n", "#3");
		}
		if (value.indexOf("\n") != -1) {
			value = value.replaceAll("\n", "#4");
		}
		if (value.indexOf(">") != -1) {
			value = value.replaceAll(">", "#5");
		}
		if (value.indexOf("<") != -1) {
			value = value.replaceAll("<", "#6");
		}
        if (value.indexOf("&amp;") != -1) {
            value = value.replaceAll("&amp;", "#7");
        }
		return value;
	}
	
	public static String convertXml(String value) {
		if(value.indexOf("#1") != -1) {
			value = value.replaceAll("#1", "'");
		}
		if(value.indexOf("#2") != -1) {
			value = value.replaceAll("#2", "\"");
		}
		if(value.indexOf("#5") != -1) {
			value = value.replaceAll("#5", "&gt;");
		}
		if(value.indexOf("#6") != -1) {
			value = value.replaceAll("#6", "&lt;");
		}
        if(value.indexOf("&") != -1) {
            value = value.replaceAll("#7", "&amp;");
        }
		return value;
	}
	
	private static int getNumber(String value) {
		if(value == null) return 0;
		try {
			return Integer.parseInt(value) + 180;
		} catch(Exception e) {
			return 0;
		}
	}
	
	//------------------------------------
	// 输出流程图的图像
	
	/**
	 * 输出流程图
	 * @param displayName 流程的displayName,可以为null
	 * @param model 流程模型,不可以为空
	 * @param _w 期望输出的图片宽,建议1280
	 * @param _h 期望输出的图片高,建议720
	 * @return 流程图
	 */
	public static BufferedImage image(String displayName, ProcessModel model, int _w, int _h) {
		BufferedImage img = new BufferedImage(_w, _h, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		// ==== 首先,画上本流程的基本信息
		g2d.setColor(Color.blue);
		g2d.setFont(new Font(Font.SANS_SERIF, 0, 13));
		if (!Strings.isBlank(displayName))
			g2d.drawString(displayName, 30, 30);
		
		// 自动扩展图层用,未实现
		int maxX = 0;
		int maxY = 0;

		// 节点layout数据
		Map<String, Rectangle2D> layouts = new HashMap<String, Rectangle2D>();
		for (NodeModel node : model.getNodes()) {
			String layoutJson = SnakerHelper.getLayout(node);
			layoutJson = layoutJson.substring(layoutJson.indexOf('{'), layoutJson.lastIndexOf('}')+1);
			NodeLayout layout = Json.fromJson(NodeLayout.class, layoutJson);
			// 算出右边界和下边界
			layouts.put(node.getName(), new Rectangle2D.Double(layout.x, layout.y, layout.width, layout.height));
			maxX = Math.max(maxX, layout.x + layout.width);
			maxY = Math.max(maxY, maxY + layout.height);
		}
		
		// 绘制流程流转线, 只需要画出 output的线
		g2d.setColor(Color.BLACK); // TODO 做成可扩展
		for (NodeModel node : model.getNodes()) {
			List<TransitionModel> trans = node.getOutputs();
			if (trans != null) {
				Rectangle2D fromLayout = layouts.get(node.getName());
				for (TransitionModel tran : trans) { // 变量所有TransitionModel
					NodeModel to = model.getNode(tran.getTo());
					Rectangle2D toLayout = layouts.get(to.getName());
					int lastX = (int)fromLayout.getCenterX();
					int lastY = (int)fromLayout.getCenterY();
					String g = tran.getG();
					int textX = -1;
					int textY = -1;
					if (!Strings.isBlank(g)) { // 有折线点
						String[] endpoints = g.split(";");
						int[][] eps = new int[endpoints.length][2];
						for (int i = 0; i < endpoints.length; i++) {
							String[] tmp = endpoints[i].split(",");
							int x = Integer.parseInt(tmp[0]) + 180; // snaker 额外加的偏移量
							int y = Integer.parseInt(tmp[1]);
							eps[i] = new int[]{x, y};
							
							maxX = Math.max(maxX, x);
							maxY = Math.max(maxY, y);
							
							// 开始画线
							g2d.drawLine(lastX, lastY, x, y);
							lastX = x; // 当前点就变成下一条线段的起点了
							lastY = y;
						}
						// 还需要计算线上文字的坐标
						if (endpoints.length % 2 == 0) {
							// 线段总数是奇数,所以位于中间那条线
							textX = (eps[endpoints.length/2-1][0] + eps[endpoints.length/2][0]) / 2;
							textY = (eps[endpoints.length/2-1][1] + eps[endpoints.length/2][1]) / 2;
						} else {
							textX = eps[endpoints.length/2][0];
							textY = eps[endpoints.length/2][1];
						}
					}
					//g2d.drawLine(lastX, lastY, toLayout.cX, toLayout.cY);
					
					// 还有字
					String text = tran.getDisplayName();
					if (!Strings.isBlank(text)) {
						if (textX == -1) { // 没有折线点的时候为-1, 取两个Node的中心点连线的中点.
							textX = (int)(fromLayout.getX() + toLayout.getY())/2 ;
							textY = (int)(fromLayout.getX() + toLayout.getY())/2 ;
						}
						// 用户可能移动了文字,所以需要把偏移加上
						if (!Strings.isBlank(tran.getOffset())) {
							String[] tmp = tran.getOffset().split(",");
							textX += Integer.parseInt(tmp[0]);
							textY += Integer.parseInt(tmp[1]);
						}
						textX -= 5; // 稍微偏移一下,好看一点点
						g2d.drawString(text, textX, textY);
					}
					
					// 计算最后一根线与Node的交点,然后画上箭头
					Line2D.Double line2 = new Line2D.Double(lastX, lastY, toLayout.getCenterX(), toLayout.getCenterY());
					Rectangle2D r2d = toLayout;
					Point2D[] ps = getIntersectionPoint(line2, r2d);
					for (Point2D _p : ps) {
		                if (_p != null && r2d.contains(_p)) {
		                	drawAL(lastX, lastY, (int)_p.getX(), (int)_p.getY(), g2d);
		                	break;
		                }
		            }
					// TODO 有没有无交点的情况呢?
				}
			}
		}
		
		// 绘制流程节点
		g2d.setColor(Color.BLACK);
		for (NodeModel node : model.getNodes()) {
			String type = SnakerHelper.mapper.get(node.getClass());
			String text = node.getDisplayName();
			Rectangle2D attr = layouts.get(node.getName());
			switch (type) {
			case "start":
				drawProcessStateImage(g2d, attr, "start_event_empty.png");
				break;
			case "end":
				drawProcessStateImage(g2d, attr, "end_event_terminate.png");
				break;
			case "task":
				drawProcessStateImage(g2d, attr, "task_empty.png"); // 不晓得为啥没有web流程设计图上的节点好看
				if (!Strings.isEmpty(text)) {
					g2d.drawString(text, (int)attr.getX() + 20, (int)attr.getCenterY() + 6);
				}
				break;
			case "decision":
				drawProcessStateImage(g2d, attr, "gateway_exclusive.png");
				break;
			case "fork":
				drawProcessStateImage(g2d, attr, "gateway_parallel.png");
				break;
			case "join":
				drawProcessStateImage(g2d, attr, "gateway_parallel.png");
				break;
			case "subprocess":
				drawProcessStateImage(g2d, attr, "task_empty.png");
				break;
			case "custom":
				drawProcessStateImage(g2d, attr, "task_empty.png");
				break;
			default:
				System.out.println(type); // 有不支持的节点? 不会吧...
				break;
			}
		}
		
		// 扩展流程图大小, 这里再调用一次本方法就ok了, 还没想好是不是应该调用
		if (maxX+20 > _w) {
			System.out.println("Need bigger w");
		}
		if (maxY+20 > _h) {
			System.out.println("Need bigger h");
		}
		
		g2d.dispose();
		return img;
	}
	
	// --------------- 流程图帮助方法--------------------------------------------------
	
	public static void drawProcessStateImage(Graphics2D g2d, Rectangle2D attr, String imageName) {
		InputStream in = NodeLayout.class.getResourceAsStream(imageName);
		BufferedImage tmp = Images.read(in);
		g2d.drawImage(tmp, (int)attr.getX(), (int)attr.getY(), (int)attr.getWidth(), (int)attr.getHeight(),  null);
		//System.out.println(Json.toJson(attr));
		Streams.safeClose(in);
	}
	
    public static Point2D[] getIntersectionPoint(Line2D line, Rectangle2D rectangle) {

        Point2D[] p = new Point2D[4];

        // Top line
        p[0] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX(),
                        rectangle.getY(),
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY()));
        // Bottom line
        p[1] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX(),
                        rectangle.getY() + rectangle.getHeight(),
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY() + rectangle.getHeight()));
        // Left side...
        p[2] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX(),
                        rectangle.getY(),
                        rectangle.getX(),
                        rectangle.getY() + rectangle.getHeight()));
        // Right side
        p[3] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY(),
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY() + rectangle.getHeight()));

        return p;

    }

    public static Point2D getIntersectionPoint(Line2D lineA, Line2D lineB) {

        double x1 = lineA.getX1();
        double y1 = lineA.getY1();
        double x2 = lineA.getX2();
        double y2 = lineA.getY2();

        double x3 = lineB.getX1();
        double y3 = lineB.getY1();
        double x4 = lineB.getX2();
        double y4 = lineB.getY2();

        Point2D p = null;

        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d != 0) {
            double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
            double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

            p = new Point2D.Double(xi, yi);

        }
        return p;
    }
    
    public static void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2)  
    {  
  
        double H = 10; // 箭头高度  
        double L = 4; // 底边的一半  
        int x3 = 0;  
        int y3 = 0;  
        int x4 = 0;  
        int y4 = 0;  
        double awrad = Math.atan(L / H); // 箭头角度  
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度  
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);  
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);  
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点  
        double y_3 = ey - arrXY_1[1];  
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点  
        double y_4 = ey - arrXY_2[1];  
  
        Double X3 = new Double(x_3);  
        x3 = X3.intValue();  
        Double Y3 = new Double(y_3);  
        y3 = Y3.intValue();  
        Double X4 = new Double(x_4);  
        x4 = X4.intValue();  
        Double Y4 = new Double(y_4);  
        y4 = Y4.intValue();  
        // 画线  
        g2.drawLine(sx, sy, ex, ey);  
        //  
        GeneralPath triangle = new GeneralPath();  
        triangle.moveTo(ex, ey);  
        triangle.lineTo(x3, y3);  
        triangle.lineTo(x4, y4);  
        triangle.closePath();  
        //实心箭头  
        g2.fill(triangle);  
        //非实心箭头  
        //g2.draw(triangle);  
  
    }
    
    public static double[] rotateVec(int px, int py, double ang,  
            boolean isChLen, double newLen) {  
  
        double mathstr[] = new double[2];  
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度  
        double vx = px * Math.cos(ang) - py * Math.sin(ang);  
        double vy = px * Math.sin(ang) + py * Math.cos(ang);  
        if (isChLen) {  
            double d = Math.sqrt(vx * vx + vy * vy);  
            vx = vx / d * newLen;  
            vy = vy / d * newLen;  
            mathstr[0] = vx;  
            mathstr[1] = vy;  
        }  
        return mathstr;  
    }  
}
