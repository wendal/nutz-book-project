package net.wendal.nutzbook.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import com.chenlb.mmseg4j.analysis.MaxWordAnalyzer;

@IocBean(depose="close")
public class LuceneService {
	private static final Log log = Logs.get();
	protected static final byte[] lock = new byte[0]; // TODO 改成ReadWriterLock
	protected String basepath;

	protected ConcurrentHashMap<String, IndexReader> readers = new ConcurrentHashMap<String, IndexReader>();
	protected ConcurrentHashMap<String, IndexSearcher> searchers = new ConcurrentHashMap<String, IndexSearcher>();
	
	public LuceneService() {
		basepath = Mvcs.getServletContext().getRealPath("/WEB-INF/") + "lucene/";
	}

	public TopDocs top(String catalog, int size, String...keys) throws IOException {
		IndexSearcher searcher = searcher(catalog);
		QueryBuilder parser = new QueryBuilder(analyzer(catalog));
		Query query = parser.createPhraseQuery("title", keys[0]);
		return searcher.search(query, size);
	}
	
	public IndexSearcher searcher(String catalog) throws IOException {
		IndexSearcher searcher = searchers.get(catalog);
		if (searcher == null) {
			synchronized (lock) {
				searcher = searchers.get(catalog);
				if (searcher == null) {
					IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(basepath + catalog)));
					searcher = new IndexSearcher(ir);
					searchers.put(catalog, searcher);
				}
			}
		}
		return searcher;
	}
	
	public IndexWriter writer(String catalog) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(basepath + catalog));
		Analyzer analyzer = analyzer(catalog);
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		return new IndexWriter(dir, iwc);
	}
	
	public synchronized void close() {
		for (Entry<String, IndexReader> en : readers.entrySet()) {
			try {
				en.getValue().close();
			} catch (IOException e) {
				log.info("close reader fail catalog=" + en.getKey());
			}
		}
		readers = null;
		searchers = null;
	}
	
	Analyzer analyzer = new MaxWordAnalyzer();
	
	protected Analyzer analyzer(String catalog) {
		return analyzer;
	}
}
