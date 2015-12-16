package net.wendal.nutzbook.module.qqbot;

import net.wendal.nutzbook.bean.qqbot.QQBotMessage;
import net.wendal.nutzbook.bean.qqbot.QQBotRole;

/**
 * Created by wendal on 2015/12/16.
 */
public interface QQBotExecutor {

    String execute(QQBotMessage message, QQBotRole role) throws Exception;

}
