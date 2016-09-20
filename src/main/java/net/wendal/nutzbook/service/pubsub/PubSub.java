package net.wendal.nutzbook.service.pubsub;

public interface PubSub {

    void onMessage(String channel, String message);
}
