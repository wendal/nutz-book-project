package net.wendal.nutzbook.common.util;

import java.util.Map;

public interface OnConfigureChange {

    void configureChanged(Map<String, Object> props);
}
