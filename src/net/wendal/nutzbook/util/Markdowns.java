package net.wendal.nutzbook.util;

import java.util.HashSet;

import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.ParsingTimeoutException;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.CodeNode;
import org.pegdown.ast.RootNode;

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
	
	public static String toHtml(String cnt) {
		Stopwatch sw = Stopwatch.begin();
		PegDownProcessor processor = new PegDownProcessor(Extensions.SUPPRESS_INLINE_HTML | Extensions.AUTOLINKS | Extensions.HARDWRAPS, 5000);
		try {
            RootNode astRoot = processor.parseMarkdown(cnt.toCharArray());
            return new ToHtmlSerializer(new LinkRenderer()){
            	public void visit(CodeNode node) {
            		String str = node.getText();
            		if (Strings.isBlank(str))
            			return;
            		printer.print("<pre class='prettyprint'>\n");
            		String[] tmps = str.split("\n", 2);
            		boolean flag = true;
            		if (tmps.length == 2) {
            			if (tmps[0] != null && tmps[1] != null && codeNames.contains(tmps[0].trim())) {
            				printer.print("<code class='language-"+tmps[0].trim()+"'>\n");
                			printer.printEncoded(tmps[1]);
                			printer.print("</code>\n");
                			flag = false;
            			}
            		}
            		if (flag){
            			printer.print("<code>");
            			printer.printEncoded(str);
            			printer.print("</code>\n");
            		}
            		printer.print("\n</pre>\n");
            	}
            }.toHtml(astRoot);
        } catch(ParsingTimeoutException e) {
            return null;
        } finally {
        	if (log.isDebugEnabled()) {
        		sw.stop();
        		log.debugf("cnt[len=%d] time=%sms",cnt.length(), sw.getDuration());
        	}
        }
	}
}
