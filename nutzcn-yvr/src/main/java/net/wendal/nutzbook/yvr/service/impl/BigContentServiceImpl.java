package net.wendal.nutzbook.yvr.service.impl;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.nutz.dao.Dao;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.util.blob.SimpleBlob;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import net.wendal.nutzbook.core.bean.BigContent;
import net.wendal.nutzbook.yvr.bean.Topic;
import net.wendal.nutzbook.yvr.bean.TopicReply;
import net.wendal.nutzbook.yvr.service.BigContentService;

@IocBean(name="bigContentService")
public class BigContentServiceImpl implements BigContentService {
	
	private static final Log log = Logs.get();

	@Inject
	protected Dao dao;
	
	public String put(Object ins) {
		BigContent big = new BigContent();
		big.setId(R.UU32());
		File tmp = Jdbcs.getFilePool().createFile(".big");
		Files.write(tmp, ins);
		big.setData(new SimpleBlob(tmp));
		dao.insert(big);
		return big.getId();
	}
	
	public InputStream get(String key) {
		BigContent big = dao.fetch(BigContent.class, key);
		if (big == null)
			return null;
		try {
			return big.getData().getBinaryStream();
		} catch (SQLException e) {
			log.debug("fail", e);
			return null;
		}
	}
	
	public String getString(String key) {
		return Streams.readAndClose(new InputStreamReader(get(key), Encoding.CHARSET_UTF8));
	}
	
	public void fill(Topic topic) {
		if (topic == null)
			return;
		topic.setContent(getString(topic.getContentId()));
		if (topic.getReplies() != null) {
			for (TopicReply	reply : topic.getReplies()) {
				fill(reply);
			}
		}
	}
	
	public void fill(TopicReply reply) {
	    reply.setContent(getString(reply.getContentId()));
	}
}
