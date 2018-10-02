package net.wendal.nutzbook.core.service;

import org.nutz.lang.util.NutMap;

public interface JvmJsService {

    public Object invoke(String jsStr, NutMap context, boolean returnException);
}
