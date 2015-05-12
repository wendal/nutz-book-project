package net.wendal.nutzbook.bean;

import java.io.Serializable;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_faq_item")
public class FaqItem extends BasePojo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	protected long id;
	@ColDefine(width=1024)
	@Column
	protected String title;
	@Column
	protected byte[] answer;
	@ColDefine(width=1024)
	@Column
	protected List<String> tags;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public byte[] getAnswer() {
		return answer;
	}
	public void setAnswer(byte[] answer) {
		this.answer = answer;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
}
