package net.wendal.nutzbook.service;

import java.io.Closeable;
import java.io.IOException;

import net.wendal.nutzbook.bean.Topic;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.log.Log;
import org.nutz.log.Logs;


@IocBean(create = "init", depose = "close")
public class TopicService implements Closeable {

	private static final Log log = Logs.get();

	@Inject
	Dao dao;
	@Inject
	LuceneService luceneService;

	protected IndexWriter writer;

	public void init() throws IOException {
		writer = luceneService.writer("faq");
		rebuildIndex();
	}

	public void close() throws IOException {
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

	public void add2Index(Topic item) {
		Document doc = new Document();
		doc.add(new StringField("id", item.getId(), Field.Store.YES));
		doc.add(new StringField("title", item.getTitle(), Field.Store.YES));
		doc.add(new TextField("content", item.getContent(), Field.Store.YES));
		try {
			writer.addDocument(doc);
		} catch (IOException e) {
			log.debug("add doc fail id=" + item.getId(), e);
		}
	}

	public QueryResult top(Integer page,String keyword) {
		page = Lang.isEmpty(page)?1:page;
		try {
			return luceneService.top("faq", page, 20, keyword);
		} catch (Exception e) {
			log.error(e);
		}
		return new QueryResult();
	}

	public void update2Index(Topic item) throws IOException {
		Document doc = new Document();
		doc.add(new StringField("id", item.getId(), Field.Store.YES));
		doc.add(new StringField("title", item.getTitle(), Field.Store.YES));
		doc.add(new TextField("content", item.getContent(), Field.Store.YES));
		writer.updateDocument(new Term("id", item.getId()), doc);
	}

	public void removeIndex(String id) throws IOException {
		writer.deleteDocuments(new Term("id", id));
	}

	protected void rebuildIndex() {
		try {
			dao.each(Topic.class, null, new Each<Topic>() {
				public void invoke(int index, Topic ele, int length) throws ExitLoop, ContinueLoop, LoopException {
					add2Index(ele);
				}
			});
			commitIndex();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public void commitIndex() throws IOException {
		writer.commit();
	}
}
