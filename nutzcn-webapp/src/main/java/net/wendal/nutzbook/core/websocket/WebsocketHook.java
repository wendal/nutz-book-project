package net.wendal.nutzbook.core.websocket;

import javax.websocket.server.ServerEndpoint;

import org.nutz.ioc.loader.annotation.IocBean;

import net.wendal.nutzbook.core.websocket.NutIocWebSocketConfigurator;
import net.wendal.nutzbook.core.websocket.NutzbookWebsocket;

@ServerEndpoint(value = "/websocket", configurator=NutIocWebSocketConfigurator.class)
@IocBean(create="init", depose="depose")
public class WebsocketHook extends NutzbookWebsocket {

}
