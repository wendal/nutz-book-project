package net.wendal.nutzbook.bean.yvr;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

@Table("t_topic_reply_up")
@PK({"replyId", "userId"})
public class TopicReplyUp {
	
	@Column("reply_id")
	protected int replyId;
	@Column("u_id")
	protected int userId;
}
