package net.wendal.nutzbook.cxfdemo.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Assert;
import org.junit.Test;
import org.nutz.lang.Streams;

import net.wendal.nutzbook.cxfdemo.webservice.EchoService;

public class CxfClientTest extends Assert {

    @Test
    public void test_local() throws MalformedURLException {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(EchoService.class);
        factory.setAddress("http://localhost:8080/nutzcn/cxf/EchoService");
        EchoService client = (EchoService) factory.create();
         
        String reply = client.echo("hi, cxf");
        System.out.println("Server said: " + reply);
        System.out.println("Server said: " + client.ping());
        assertEquals("hi, cxf", reply);
    }
    
    @Test
    public void test_print_all_bus() throws IOException {
        Enumeration<URL> en = getClass().getClassLoader().getResources("META-INF/cxf/bus-extensions.txt");
        while (en.hasMoreElements()) {
            System.out.println(new String(Streams.readBytes(en.nextElement().openStream())));
        }
    }
}
