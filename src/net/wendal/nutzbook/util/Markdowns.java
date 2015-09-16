package net.wendal.nutzbook.util;

import org.pegdown.PegDownProcessor;

public class Markdowns {

	public static String toHtml(String cnt) {
		PegDownProcessor processor = new PegDownProcessor();
		return processor.markdownToHtml(cnt);
	}
}
