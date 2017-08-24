package net.wendal.nutzbook.web;

import java.io.IOException;

import javax.servlet.ServletException;

import org.nutz.mvc.NutFilter;

public class NutzcnNutFilter extends NutFilter {

    @Override
    protected boolean isExclusion(String matchUrl) throws IOException, ServletException {
        if (matchUrl.startsWith("/uflo"))
            return false;
        return super.isExclusion(matchUrl);
    }
}
