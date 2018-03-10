package net.wendal.nutzbook.cxfdemo.webservice;

import javax.jws.WebService;

import org.nutz.ioc.loader.annotation.IocBean;

// net.wendal.nutzbook.cxfdemo.CxfServlet 会扫描到这个类
// 因为它有@WebService和@IocBean两个注解
@WebService(endpointInterface = "net.wendal.nutzbook.cxfdemo.webservice.HelloService", serviceName = "HelloService")
@IocBean(name = "helloService")
public class HelloServiceImpl implements HelloService {

    public String ping() {
        return "pong"; // ping --> pong 心跳回路
    }

    public String echo(String str) {
        return str; // 简单回显
    }
}
