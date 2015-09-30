package net.wendal.nutzbook.util;

import static org.pegdown.FastEncoder.encode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.parboiled.common.StringUtils;
import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.Printer;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.VerbatimNode;

public class Markdowns {

	protected static final Log log = Logs.get();
	
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
		Stopwatch sw = Stopwatch.begin();
		PegDownProcessor processor = new PegDownProcessor(Extensions.SUPPRESS_INLINE_HTML | Extensions.AUTOLINKS | Extensions.HARDWRAPS | Extensions.FENCED_CODE_BLOCKS, 5000);
		try {
			Map<String, VerbatimSerializer> plugins = new HashMap<String, VerbatimSerializer>();
			plugins.put(VerbatimSerializer.DEFAULT, PrettyPrint);
            RootNode astRoot = processor.parseMarkdown(cnt.toCharArray());
            return new ToHtmlSerializer(new LinkRenderer(){
            	public Rendering render(ExpImageNode node, String text) {
            		String url = node.url;
            		if (urlbase != null && node.url != null && node.url.startsWith("/")) {
            			url = urlbase + node.url;
            		}
            		Rendering rendering = new Rendering(url, text);
                    return StringUtils.isEmpty(node.title) ? rendering : rendering.withAttribute("title", encode(node.title));
            	}
            }, plugins).toHtml(astRoot);
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
