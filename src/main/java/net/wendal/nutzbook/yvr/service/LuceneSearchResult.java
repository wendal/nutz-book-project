package net.wendal.nutzbook.yvr.service;

public class LuceneSearchResult {

	private String id;
	private String result;
	
	public LuceneSearchResult() {
		super();
	}
	public LuceneSearchResult(String id, String result) {
		super();
		this.id = id;
		this.result = result;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
}
