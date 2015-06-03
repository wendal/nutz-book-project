package net.wendal.nutzbook.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.wendal.nutzbook.bean.Topic;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.wltea.analyzer.lucene.IKAnalyzer;

@IocBean(create = "init", depose = "close")
public class LuceneService {
	private static final Log log = Logs.get();
	protected static final byte[] lock = new byte[0]; // TODO 改成ReadWriterLock
	protected String basepath;
	private Set<String> fieldSet = new HashSet<String>();
	protected ConcurrentHashMap<String, IndexReader> readers = new ConcurrentHashMap<String, IndexReader>();
	protected ConcurrentHashMap<String, IndexSearcher> searchers = new ConcurrentHashMap<String, IndexSearcher>();
	private static Analyzer analyzer = new IKAnalyzer();

	public LuceneService() {
		basepath = Mvcs.getServletContext().getRealPath("/WEB-INF/") + "/lucene/";
	}

	public void init() {
		fieldSet.add("id");
		fieldSet.add("title");
		fieldSet.add("content");
	}

	public QueryResult top(String catalog, int page, int size, String key) throws Exception {
		QueryResult qr = new QueryResult(null, new Pager().setPageNumber(page).setPageSize(size));
		List<Topic> pageList = new ArrayList<Topic>();
		qr.setList(pageList);
		if (StringUtils.isBlank(key)) {
			return qr;
		}
		IndexSearcher searcher = searcher(catalog);
		Term term = new Term("content", key);
		Query query = new TermQuery(term);
		QueryScorer scorer = new QueryScorer(query);
		TopDocs docs = getScoreDocsByPerPage(page, size, searcher, query);
		ScoreDoc[] scoreDocs = docs.scoreDocs;
		System.out.println("所有的数据总数为：" + docs.totalHits);
		qr.getPager().setRecordCount(docs.totalHits);
		System.out.println("本页查询到的总数为：" + scoreDocs.length);
		SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("<font color=\"red\">", "</font>");
		Highlighter highlighter = new org.apache.lucene.search.highlight.Highlighter(simpleHtmlFormatter, scorer);
		highlighter.setTextFragmenter(new SimpleFragmenter(180));
		ScoreDoc[] scores = docs.scoreDocs;
		for (ScoreDoc scoreDoc : scores) {
			Document doc = searcher.doc(scoreDoc.doc, fieldSet);
			String t = highlighter.getBestFragment(analyzer, "content", doc.get("content"));
			Topic workOrder = new Topic();
			workOrder.setId(doc.get("id"));
			workOrder.setTitle(doc.get("title"));
			workOrder.setContent(t);
			pageList.add(workOrder);
		}
		return qr;
	}

	private TopDocs getScoreDocsByPerPage(int page, int perPage, IndexSearcher searcher, Query query) throws IOException {
		TopDocs result = null;
		if (query == null) {
			System.out.println(" Query is null return null ");
			return null;
		}
		ScoreDoc before = null;
		if (page != 1) {
			TopDocs docsBefore = searcher.search(query, (page-1) * perPage);
			ScoreDoc[] scoreDocs = docsBefore.scoreDocs;
			if (scoreDocs.length > 0) {
				before = scoreDocs[scoreDocs.length - 1];
			}
		}
		result = searcher.searchAfter(before, query, perPage);
		return result;
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
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, iwc);
		return writer;

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

	protected Analyzer analyzer(String catalog) {
		return analyzer;
	}
}
