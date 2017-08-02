package net.wendal.nutzbook.cxfdemo.client;

import java.net.MalformedURLException;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Assert;
import org.junit.Test;

import net.wendal.nutzbook.cxfdemo.webservice.EchoService;

public class CxfClientTest extends Assert {

    @Test
    public void test_local() throws MalformedURLException {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(EchoService.class);
        factory.setAddress("http://localhost:8080/nutzcn/ws/EchoService");
        EchoService client = (EchoService) factory.create();
         
        String reply = client.echo("hi, cxf");
        System.out.println("Server said: " + reply);
        System.out.println("Server said: " + client.ping());
        assertEquals("hi, cxf", reply);
    }
}
