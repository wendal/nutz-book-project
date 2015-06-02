package net.wendal.nutzbook.module;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wendal.nutzbook.bean.Topic;
import net.wendal.nutzbook.bean.TopicReply;
import net.wendal.nutzbook.bean.UserProfile;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

@IocBean
@At("/ask")
public class AskModule extends BaseModule {

	@GET
	@At
	@Ok("json")
	public Object topics(@Param("..")Pager pager, @Param("tab")String tab) {
		QueryResult qr = query(Topic.class, Strings.isBlank(tab) ? null : Cnd.where("tab", "=", tab), pager, null);
		if (qr.getList().size() > 0) {
			for (Object t : qr.getList()) {
				Topic topic = (Topic)t;
				handleTopic(topic);
			}
		}
		return ajaxOk(qr);
	}
	
	@At("/topic/?")
	@GET
	public Object topic(String id) {
		Topic topic = dao.fetch(Topic.class, id);
		if (topic != null) {
			dao.update(Topic.class, Chain.makeSpecial("vistors", "+1"), Cnd.where("id", "=", id));
		}
		return ajaxOk(topic);
	}
	
	@RequiresUser
	@At
	@POST
	public Object topic(@Param("..")Topic topic, @Attr("me")int userId) {
		if (topic == null || Strings.isBlank(topic.getTitle())) {
			return ajaxFail("标题为空");
		}
		if (Strings.isBlank(topic.getContent())) {
			return ajaxFail("内容为空");
		}
		topic.setTitle(topic.getTitle().trim());
		topic.setContent(topic.getContent().trim());
		if (topic.getTags() != null) {
			topic.setTags(topic.getTags().trim());
		}
		topic.setStatus(0);
		topic.setUserId(userId);
		topic.setCreateTime(new Date());
		topic.setUpdateTime(topic.getCreateTime());
		return ajaxOk(dao.insert(topic));
	}
	
//	@RequiresUser
//	@At("/collect/?")
//	@GET
//	public void collect(String id, @Attr("me")int userId){
//		
//	}
	
	@GET
	@At("/topic/?/replies")
	public Object replies(String id){
		List<TopicReply> list = dao.query(TopicReply.class, Cnd.where("topicId", "=", id).asc("createTime"));
		dao.fetchLinks(list, null);
		for (TopicReply tr : list) {
			handleTopic(tr);
		}
		return ajaxOk(list);
	}
	
	@RequiresUser
	@POST
	@At("/topic/?/reply")
	public Object reply(String id, @Param("..")TopicReply reply, @Attr("me")int userId){
		if (reply == null || Strings.isBlank(reply.getContent())) {
			return ajaxFail("回复内容为空");
		}
		Topic topic = dao.fetch(Topic.class, id);
		if (topic == null) {
			return ajaxFail("帖子不存在");
		}
		if (topic.getStatus() != 0) {
			return ajaxFail("帖子已经被关闭");
		}
		if (reply.getParent() != null && dao.fetch(TopicReply.class, reply.getParent()) == null) {
			return ajaxFail("父评论不存在");
		}
		reply.setTopicId(id);
		reply.setUserId(userId);
		reply.setCreateTime(new Date());
		reply.setUpdateTime(new Date());
		return ajaxOk(dao.insert(reply));
	}
	
//	@RequiresUser
//	@GET
//	@At("/replies/?/ups")
//	public void updown(String id, @Attr("me")int userId) {
//		
//	}
	
	protected Set<String> allowSuffix = new HashSet<String>();
	{
		allowSuffix.add(".jpg");
		allowSuffix.add(".png");
		allowSuffix.add(".gif");
	}
	
	@AdaptBy(type=UploadAdaptor.class, args={"${app.root}/tmp/ask/image"})
	@POST
	@At("/image/upload")
	@Ok("raw:html")
	public Object imageUpload(@Param("editormd-image-file")TempFile tmp, HttpServletResponse resp) throws IOException {
		resp.addHeader("Access-Control-Allow-Origin", "*");
		//resp.setContentType("application/json; charset=utf-8");
		String suffix = tmp.getMeta().getFileExtension();
		NutMap re = new NutMap();
		if (!allowSuffix.contains(suffix)) {
			re.put("success", 0);
			re.put("message", "文件格式不合法");
			return fuckUploadStr(re);
		}
		if (tmp.getFile().length() == 0) {
			re.put("success", 0);
			re.put("message", "不允许上传空文件");
			return fuckUploadStr(re);
		}

		if (tmp.getFile().length() > 2*1024*102) {
			re.put("success", 0);
			re.put("message", "不允许超过2mb");
			return fuckUploadStr(re);
		}
		ServletContext sc = Mvcs.getServletContext();
		String path = "/ask/image/upload/" + R.UU32() + suffix;
		File f = new File(sc.getRealPath("/") + path);
		Files.createNewFile(f);
		Files.copy(tmp.getFile(), f);
		
		re.put("success", 1);
//		re.put("message", "上传成功");
		re.put("url", sc.getContextPath() + path);
		return fuckUploadStr(re);
	}
	
	protected String fuckUploadStr(NutMap re) {
		return "<html><head><title>_</title></head><body>" + Json.toJson(re) + "</body></html>";
	}
	
	@At("/v/?")
	@Ok("->:/ask/index.jsp")
	public Object viewTopic(String id, HttpServletRequest req) {
		Topic topic = dao.fetch(Topic.class, id);
		if (topic != null) {
			dao.update(Topic.class, Chain.makeSpecial("vistors", "+1"), Cnd.where("id", "=", id));
			dao.fetchLinks(topic, null);
			handleTopic(topic);
		}
		Context ctx = Lang.context();
		ctx.set("topic", topic);
		return ctx;
	}
	
	protected void handleTopic(Topic topic) {
		topic.replies = dao.count(TopicReply.class, Cnd.where("topicId", "=", topic.getId()));
		if (topic.getUser() != null) {
			UserProfile profile = dao.fetch(UserProfile.class, topic.getUserId());
			if (profile != null && !Strings.isBlank(profile.getNickname())) {
				topic.getUser().displayName = profile.getNickname();
			} else {
				topic.getUser().displayName = topic.getUser().getName();
			}
		}
	}
	
	protected void handleTopic(TopicReply tr) {
		if (tr.getUser() != null) {
			UserProfile profile = dao.fetch(UserProfile.class, tr.getUserId());
			if (profile != null && !Strings.isBlank(profile.getNickname())) {
				tr.getUser().displayName = profile.getNickname();
			} else {
				tr.getUser().displayName = tr.getUser().getName();
			}
		}
	}
}
