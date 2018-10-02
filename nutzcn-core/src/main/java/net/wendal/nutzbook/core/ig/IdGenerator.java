package net.wendal.nutzbook.core.ig;

import org.nutz.el.opt.RunMethod;

public interface IdGenerator extends RunMethod {

    long next(String key) throws Exception;
}
