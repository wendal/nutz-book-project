package net.wendal.nutzbook.yvr.bean;

public class TopicTag {

	protected String name;
	protected int count;
	
	public TopicTag() {}
	
	
	
	public TopicTag(String name, int count) {
		super();
		this.name = name;
		this.count = count;
	}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
