package net.wendal.nutzbook.service;

import java.io.Closeable;
import java.io.IOException;

import net.wendal.nutzbook.bean.FaqItem;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.QueryBuilder;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create="init", depose="close")
public class FaqService implements Closeable {
	
	private static final Log log = Logs.get();

	@Inject Dao dao;
	@Inject LuceneService luceneService;
	
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
	
	public void add2Index(FaqItem item) {
		Document doc = new Document();
		FieldType _id = new FieldType();
		_id.setStored(true);
		_id.setIndexOptions(IndexOptions.NONE);
		doc.add(new Field("id", ""+item.getId(), _id));
		
		FieldType _title = new FieldType();
		_title.setStored(false);
		_title.setIndexOptions(IndexOptions.DOCS);
		doc.add(new Field("title", item.getTitle(), _title));
		

		FieldType _answer = new FieldType();
		_answer.setStored(false);
		_answer.setIndexOptions(IndexOptions.DOCS);
		doc.add(new Field("answer", new String(item.getAnswer()), _answer));
		
		try {
			writer.addDocument(doc);
		} catch (IOException e) {
			log.debug("add doc fail id="+item.getId(), e);
		}
	}
	
	public TopDocs top(String ... keywords) {
		IndexReader indexReader = null;
		try {
			indexReader = DirectoryReader.open(writer, false);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			QueryBuilder parser = new QueryBuilder(luceneService.analyzer("faq"));
			Query query = parser.createPhraseQuery("title", keywords[0]);
			return searcher.search(query, 10);
		} catch (IOException e) {
			log.debug("open index fail?", e);
			return null;
		} finally {
			Streams.safeClose(indexReader);
		}
	}
	
	public void update2Index(FaqItem item) {}
	
	public void removeIndex(long id) {
		
	}
	
	protected void rebuildIndex() {
		try {
			dao.each(FaqItem.class, null, new Each<FaqItem>() {
				public void invoke(int index, FaqItem ele, int length)
						throws ExitLoop, ContinueLoop, LoopException {
					add2Index(ele);
				}
			});
			writer.commit();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}
	
	public void commitIndex() throws IOException {
		writer.commit();
	}
}
