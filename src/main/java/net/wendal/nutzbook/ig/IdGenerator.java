package net.wendal.nutzbook.ig;

import org.nutz.el.opt.RunMethod;

public interface IdGenerator extends RunMethod {

    long next(String key) throws Exception;
}
