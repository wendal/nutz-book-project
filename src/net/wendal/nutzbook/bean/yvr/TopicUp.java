package net.wendal.nutzbook.bean.yvr;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

@Table("t_topic_up")
@PK({"userId", "topicId"})
public class TopicUp implements Serializable {

	@Column("topic_id")
	protected int topicId;
	@Column("u_id")
	protected int userId;
}
