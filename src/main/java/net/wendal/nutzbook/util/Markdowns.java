package net.wendal.nutzbook.util;

import static org.pegdown.FastEncoder.encode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.parboiled.common.StringUtils;
import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.LinkRenderer.Rendering;
import org.pegdown.PegDownProcessor;
import org.pegdown.Printer;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.TextNode;
import org.pegdown.ast.VerbatimNode;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class Markdowns {

	protected static final Log log = Logs.get();
	
	public static Cache cache;
	
	public static Pattern VideoURL = Pattern.compile(".+\\.(mp4|webm|ogg)$");
	public static Pattern AtUser = Pattern.compile("(@[a-zA-Z0-9_]+[ ])");
	
	public static HashSet<String> codeNames = new HashSet<String>();
	static {
		String[] codes = new String[]{
				"bsh", "c", "cc", "cpp", "cs", "csh", "cyc", "cv", "htm", "html",
			    "java", "js", "m", "mxml", "perl", "pl", "pm", "py", "rb", "sh",
			    "xhtml", "xml", "xsl"
		};
		for (String code : codes) {
			codeNames.add(code);
		}
	}
	
	static  VerbatimSerializer PrettyPrint = new PrettyPrintVerbatimSerializer();
	
	static class PrettyPrintVerbatimSerializer implements VerbatimSerializer {
	    public void serialize(final VerbatimNode node, final Printer printer) {
	        printer.println().print("<pre class='prettyprint'><code");
	        if (!StringUtils.isEmpty(node.getType())) {
	            printAttribute(printer, "class", "language-"+node.getType());
	        }
	        printer.print(">");
	        String text = node.getText();
	        // print HTML breaks for all initial newlines
	        while (text.charAt(0) == '\n') {
	            printer.print("<br/>");
	            text = text.substring(1);
	        }
	        printer.printEncoded(text);
	        printer.print("</code></pre>");

	    }

	    private void printAttribute(final Printer printer, final String name, final String value) {
	        printer.print(' ').print(name).print('=').print('"').print(value).print('"');
	    }
	}
	
	public static String toHtml(String cnt, final String urlbase) {
		if (Strings.isBlank(cnt) || cnt == null)
			return "";
		String key = urlbase == null ? "" : urlbase;
		key += "," + cnt;
		if (cache != null) {
			Element ele = cache.get(key);
			if (ele != null) {
				return (String)ele.getObjectValue();
			}
		}
		Stopwatch sw = Stopwatch.begin();
		PegDownProcessor processor = new PegDownProcessor(Extensions.SUPPRESS_INLINE_HTML | Extensions.AUTOLINKS | Extensions.HARDWRAPS | Extensions.FENCED_CODE_BLOCKS, 5000);
		try {
			Map<String, VerbatimSerializer> plugins = new HashMap<String, VerbatimSerializer>();
			plugins.put(VerbatimSerializer.DEFAULT, PrettyPrint);
            RootNode astRoot = processor.parseMarkdown(cnt.toCharArray());
            String re = new ToHtmlSerializer(new LinkRenderer(){
            	public Rendering render(ExpImageNode node, String text) {
            		String url = node.url;
            		if (urlbase != null && node.url != null && node.url.startsWith("/")) {
            			url = urlbase + node.url;
            		}
            		Rendering rendering = new Rendering(url, text);
                    return StringUtils.isEmpty(node.title) ? rendering : rendering.withAttribute("title", encode(node.title));
            	}
            }, plugins){
            	protected void printLink(Rendering rendering) {
            		super.printLink(rendering);
            		String href = rendering.href;
            		if (VideoURL.matcher(href).find()) {
            			printer.print("<p/><video width=\"320\" height=\"240\" controls>");
            			printer.print("<source");
            			printAttribute("src", href);
            			if (href.endsWith("mp4")) {
            				printAttribute("type", "video/mp4");
            			} else if (rendering.href.endsWith("webm")) {
            				printAttribute("type", "video/webm");
            			} else {
            				printAttribute("type", "video/ogg");
            			}
            			printer.print(">");
            			printer.print("</video>");
            		}
            	}
            	
            	public void printAttribute(String name, String value) {
                    printer.print(' ').print(name).print('=').print('"').print(value).print('"');
                }
            	
            	public void visit(TextNode node) {
            	    String text = node.getText();
            	    if (text != null && text.length() > 5) {
            	        Matcher m = AtUser.matcher(text);
            	        StringBuilder sb = new StringBuilder();
            	        int start = 0;
            	        while (m.find()) {
            	            if (m.start()>start) {
            	                sb.append(text.substring(start, m.start()));
            	            }
            	            String user = m.group().substring(1).trim();
            	            String p = String.format("<a href='%s/yvr/u/%s'>@%s</a> ", Mvcs.getServletContext().getContextPath(), user, user);
            	            sb.append(p);
                            start = m.end();
            	        }
            	        if (text.length() > start)
            	            sb.append(text.substring(start));
            	        super.visit(new TextNode(sb.toString()));
            	        return;
            	    }
            	    super.visit(node);
            	}
            	
            }.toHtml(astRoot);
            if (cache != null) {
            	cache.put(new Element(key, re));
            }
            return re;
        } catch(Exception e) {
            return "";
        } finally {
        	if (log.isDebugEnabled()) {
        		sw.stop();
        		log.debugf("cnt[len=%d] time=%sms",cnt.length(), sw.getDuration());
        	}
        }
	}
}
