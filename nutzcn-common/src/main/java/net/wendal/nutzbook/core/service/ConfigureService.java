package net.wendal.nutzbook.core.service;

public interface ConfigureService {

    void doReload();

    void update(String key, String value, boolean reload);

    
}