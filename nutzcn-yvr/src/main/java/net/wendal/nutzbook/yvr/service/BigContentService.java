package net.wendal.nutzbook.yvr.service;

import java.io.InputStream;

import net.wendal.nutzbook.yvr.bean.Topic;
import net.wendal.nutzbook.yvr.bean.TopicReply;

public interface BigContentService {

    String put(Object ins);

    InputStream get(String key);

    String getString(String key);

    void fill(Topic topic);

    void fill(TopicReply reply);

}