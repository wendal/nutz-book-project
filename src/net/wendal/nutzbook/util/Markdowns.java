package net.wendal.nutzbook.util;

import org.pegdown.Extensions;
import org.pegdown.ParsingTimeoutException;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.CodeNode;
import org.pegdown.ast.RootNode;

public class Markdowns {

	public static String toHtml(String cnt) {
		PegDownProcessor processor = new PegDownProcessor(Extensions.SUPPRESS_INLINE_HTML | Extensions.AUTOLINKS | Extensions.HARDWRAPS);
		try {
            RootNode astRoot = processor.parseMarkdown(cnt.toCharArray());
            return new ToHtmlSerializer(null){
            	public void visit(CodeNode node) {
            		printer.print("<pre class='prettyprint'>\n");
            		super.visit(node);
            		printer.print("\n</pre>\n");
            	}
            }.toHtml(astRoot);
        } catch(ParsingTimeoutException e) {
            return null;
        }
	}
}
