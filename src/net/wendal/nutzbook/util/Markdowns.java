package net.wendal.nutzbook.util;

import org.nutz.lang.Stopwatch;
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
	
	public static String toHtml(String cnt) {
		Stopwatch sw = Stopwatch.begin();
		PegDownProcessor processor = new PegDownProcessor(Extensions.SUPPRESS_INLINE_HTML | Extensions.AUTOLINKS | Extensions.HARDWRAPS, 5000);
		try {
            RootNode astRoot = processor.parseMarkdown(cnt.toCharArray());
            return new ToHtmlSerializer(new LinkRenderer()){
            	public void visit(CodeNode node) {
            		printer.print("<pre class='prettyprint'>\n");
            		super.visit(node);
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
