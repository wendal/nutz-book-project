package net.wendal.nutzbook.common.beetl;

import org.beetl.core.Context;
import org.beetl.core.Function;

public class HasHotplugFunction implements Function {

    public Object call(Object[] paras, Context ctx) {
        return getClass().getClassLoader().getResource("hotplug/hotplug.nutzcn." + paras[0] + ".json") != null;
    }

}
