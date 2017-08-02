package net.wendal.nutzbook.cxfdemo.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;

// 接口类仅需要声明一个空的@WebService,不可以加serviceName,endpointInterface
@WebService
public interface EchoService {

    // 测试一下无参,有返回值的
    String ping();

    // 测试一下有参数,有返回值
    String echo(@WebParam(name = "str") String str);
}
