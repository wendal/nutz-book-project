package net.wendal.nutzbook.service;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdEntityService;

import net.wendal.nutzbook.bean.msg.UserMessage;

@IocBean(fields={"dao"})
public class UserMessageService extends IdEntityService<UserMessage> {

}
