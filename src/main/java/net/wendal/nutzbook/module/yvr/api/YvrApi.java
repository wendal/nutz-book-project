package net.wendal.nutzbook.module.yvr.api;

import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicReply;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.upload.TempFile;

/**
 * Created by wendal on 06/11/2015.
 */
@At("/yvr/api/v1")
@Ok("json")
public interface YvrApi {

    @GET
    @At
    Object topics(@Param("page")int page, @Param("tab")String type,
    			  @Param("tag")String tag,
    			  @Param("search")String search,
                  @Param("limit")int limit, @Param("mdrender")String mdrender);

    @GET
    @At("/topic/?")
    Object topic(String id,@Param("mdrender")String mdrender);

    @At("/accesstoken")
    Object checkAccessToken(@Param("accesstoken")String accesstoken);

    @At("/user/?")
    @GET
    Object user(String loginname);

    @POST
    @At("/topics")
    Object add(@Param("..")Topic topic, @Attr(scope= Scope.SESSION, value="me")int userId, @Param("tab")String tab);

    @POST
    @At("/topic/?/replies")
    Object addReply(String topicId, @Param("..") TopicReply reply, @Attr(scope = Scope.SESSION, value = "me") int userId);

    @POST
    @At("/reply/?/ups")
    Object replyUp(String replyId, @Attr(scope = Scope.SESSION, value = "me") int userId);

    @GET
    @At("/message/count")
    Object msgCount(@Attr(scope = Scope.SESSION, value = "me") int userId);

    @GET
    @At("/messages")
    Object getMessages(@Attr(scope = Scope.SESSION, value = "me") int userId);

    @POST
    @At("/message/mark_all")
    Object markAllMessage(@Attr(scope = Scope.SESSION, value = "me") int userId);

    @POST
    @At("/images")
    Object images(@Param("file")TempFile tmp, @Attr(scope = Scope.SESSION, value = "me") int userId) throws Exception;
}
