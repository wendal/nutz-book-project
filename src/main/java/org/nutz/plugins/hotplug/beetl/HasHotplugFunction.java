package org.nutz.plugins.hotplug.beetl;

import org.beetl.core.Context;
import org.beetl.core.Function;

public class HasHotplugFunction implements Function {

    public Object call(Object[] paras, Context ctx) {
        return true;
    }

}
