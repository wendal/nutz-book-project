package org.nutz.integration.zbus;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.integration.zbus.annotation.ZBusConsumer;
import org.nutz.integration.zbus.annotation.ZBusService;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Streams;
import org.nutz.resource.Scans;
import org.zbus.broker.Broker;
import org.zbus.mq.Consumer;
import org.zbus.mq.MqConfig;
import org.zbus.mq.Protocol.MqMode;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;
import org.zbus.net.http.Message.MessageHandler;
import org.zbus.rpc.RpcProcessor;

public class ZBusFactory {

	protected Set<Consumer> consumers = new HashSet<>();
	protected Map<String, ZBusProducer> producers = new ConcurrentHashMap<>();
	protected Object lock = new Object();
	protected Broker broker;
	protected Ioc ioc;
	protected List<String> pkgs;

	public ZBusProducer getProducer(String mq) {
		ZBusProducer producer = producers.get(mq);
		if (producer == null) {
			synchronized (lock) {
				producer = producers.get(mq);
				if (producer == null) {
					producer = new ZBusProducer(broker, mq, MqMode.MQ, MqMode.PubSub);
					producers.put(mq, producer);
				}
			}
		}
		return producer;
	}

	public void init() {
		for (String pkg : pkgs) {
			for (Class<?> klass : Scans.me().scanPackage(pkg)) {
				addConsumer(klass);
			}
		}
	}

	public void close() {
		for (Consumer consumer : consumers) {
			try {
				consumer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addConsumer(Class<?> klass) {
		ZBusConsumer z = klass.getAnnotation(ZBusConsumer.class);
		if (z != null && z.enable()) {
			MqConfig mqConfig = fromAnnotation(broker, z);
			proxy(mqConfig, (MessageHandler) ioc.get(klass));
		}
		for (final Method method : klass.getMethods()) {
			z = method.getAnnotation(ZBusConsumer.class);
			if (z != null && z.enable()) {
				MqConfig mqConfig = fromAnnotation(broker, z);
				final Object obj = ioc.get(klass);
				MessageHandler handler = null;
				switch (method.getParameterCount()) {
				case 0:
					handler = new MessageHandler() {
						public void handle(Message msg, Session sess) throws IOException {
							try {
								method.invoke(obj);
							} catch (Exception e) {
								throw new RuntimeException(e.getCause());
							}
						}
					};
					break;
				case 1:
					handler = new MessageHandler() {
						public void handle(Message msg, Session sess) throws IOException {
							try {
								method.invoke(obj, msg);
							} catch (Exception e) {
								throw new RuntimeException(e.getCause());
							}
						}
					};
					break;
				case 2:
					handler = new MessageHandler() {
						public void handle(Message msg, Session sess) throws IOException {
							try {
								method.invoke(obj, msg, sess);
							} catch (Exception e) {
								throw new RuntimeException(e.getCause());
							}
						}
					};
					break;
				default:
					throw new RuntimeException("method[" + method + "] not good");
				}
				proxy(mqConfig, handler);
			}
		}
	}

	protected static MqConfig fromAnnotation(Broker broker, ZBusConsumer z) {
		MqConfig mqConfig = new MqConfig();
		mqConfig.setBroker(broker);
		mqConfig.setMode(z.mode());
		mqConfig.setMq(z.mq());
		mqConfig.setTopic(z.topic());
		mqConfig.setVerbose(z.verbose());
		return mqConfig;
	}

	protected void proxy(MqConfig mqConfig, MessageHandler handler) {
		Consumer c = new Consumer(mqConfig);
		try {
			c.onMessage(handler);
			c.createMQ();
		} catch (Exception e) {
			Streams.safeClose(c);
			throw new RuntimeException("create Consumer fail obj=" + handler.getClass().getName(), e);
		}
		c.start();
		consumers.add(c);
	}
	
	public static void buildServices(RpcProcessor rpcProcessor, Ioc ioc, String... pkgs) {
		for (String pkg : pkgs) {
			for (Class<?> klass : Scans.me().scanPackage(pkg)) {
				ZBusService zBusService = klass.getAnnotation(ZBusService.class);
				if (zBusService != null) {
					rpcProcessor.addModule(ioc.get(klass));
				}
			}
		}
	}
}
