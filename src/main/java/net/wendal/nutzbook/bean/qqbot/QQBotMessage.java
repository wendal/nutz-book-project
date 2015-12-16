package net.wendal.nutzbook.bean.qqbot;

import org.nutz.lang.Lang;
import org.nutz.lang.util.NutBean;
import org.nutz.lang.util.NutMap;

/**
 * QQLite机器人发送过来的参数
 * Created by wendal on 2015/12/16.
 */
public class QQBotMessage {
    public String RobotQQ;
    public String Version;
    public String Message;
    public String Event;
    public String Key;
    public String Port;
    public String Sender;
    public long SendTime;
    public String SenderName;
    public String GroupName;
    public String GroupId;

    public NutBean toNutBean() {
        return new NutMap(Lang.obj2map(this));
    }
}
