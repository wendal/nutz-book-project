package net.wendal.nutzbook.bean.yvr;

public enum TopicType {

	ask("问答"), 新闻("新闻"), share("分享"), NEWBEE("灌水区"), job("招聘");
	
	public String display;
	
	TopicType(String display) {
		this.display = display;
	}
	
	public String getDisplay() {
		return display;
	}
}
