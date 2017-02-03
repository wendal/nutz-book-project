package net.wendal.nutzbook.yvr.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.ansj.lucene4.AnsjAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneIndex implements AutoCloseable, Closeable {

    // 索引器
    public IndexWriter writer = null;

    protected String path;

    public LuceneIndex(String indexStorePath, OpenMode mode) throws IOException {
        // 索引文件的保存位置
        Directory dir = FSDirectory.open(new File(indexStorePath));
        // 分析器
        Analyzer analyzer = analyzer();
        // 配置类
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
        iwc.setOpenMode(mode);// 创建模式 OpenMode.CREATE_OR_APPEND 添加模式
        writer = new IndexWriter(dir, iwc);
        path = indexStorePath;
    }

    public void close() throws IOException {
        IndexWriter writer = this.writer;
        if (writer != null) {
            writer.commit();
            writer.close(true);
            this.writer = null;
        }
    }

    public IndexReader reader() throws IOException {
        return DirectoryReader.open(writer, false);
    }

    public Analyzer analyzer() {
        return new AnsjAnalysis();
    }
}
