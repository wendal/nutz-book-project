package net.wendal.nutzbook.beepay.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.util.NutMap;

import net.wendal.nutzbook.core.bean.BasePojo;
import net.wendal.nutzbook.core.bean.UserProfile;

/**
 * 对接BeeCloub的支付功能
 * @author wendal
 *
 */
@Table("t_bee_payment")
public class BeePayment extends BasePojo {

    private static final long serialVersionUID = -4144327030515932274L;
    // 这两个属性是内部关联用的
    @Column("from_user")
    protected long fromUser;
    @One(field="fromUser", target=UserProfile.class)
    protected UserProfile fromUserProfile;
    @Column("to_user")
    protected long toUser;
    @One(field="toUser", target=UserProfile.class)
    protected UserProfile toUserProfile;
    @Column("cm")
    protected String commet;
    @Column("pd")
    protected boolean paydone;
    // 这个属性是关联BeePayment与本地支付记录的字段
    @Name
    protected String out_trade_no;
    // 下面剩下的是Bee返回的webhook带的属性,大部分需要入库
    @Column
    protected String transaction_id;

    @Column
    protected String channel_type;
    @Column
    protected String sub_channel_type;
    @Column
    protected String transaction_type;
    /**单位是分*/
    @Column
    protected int transaction_fee;
    @Column
    protected boolean trade_success;
    @Column
    @ColDefine(width=4096)
    protected NutMap message_detail;
    @Column("opt")
    protected NutMap optional;
    public String getTransaction_id() {
        return transaction_id;
    }
    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getCommet() {
        return commet;
    }
    public void setCommet(String commet) {
        this.commet = commet;
    }
    public String getChannel_type() {
        return channel_type;
    }
    public void setChannel_type(String channel_type) {
        this.channel_type = channel_type;
    }
    public String getSub_channel_type() {
        return sub_channel_type;
    }
    public void setSub_channel_type(String sub_channel_type) {
        this.sub_channel_type = sub_channel_type;
    }
    public String getTransaction_type() {
        return transaction_type;
    }
    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }
    public int getTransaction_fee() {
        return transaction_fee;
    }
    public void setTransaction_fee(int transaction_fee) {
        this.transaction_fee = transaction_fee;
    }
    public boolean isTrade_success() {
        return trade_success;
    }
    public void setTrade_success(boolean trade_success) {
        this.trade_success = trade_success;
    }
    public NutMap getMessage_detail() {
        return message_detail;
    }
    public void setMessage_detail(NutMap message_detail) {
        this.message_detail = message_detail;
    }
    public NutMap getOptional() {
        return optional;
    }
    public void setOptional(NutMap optional) {
        this.optional = optional;
    }
    public String getOut_trade_no() {
        return out_trade_no;
    }
    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }
    public long getFromUser() {
        return fromUser;
    }
    public void setFromUser(long fromUser) {
        this.fromUser = fromUser;
    }
    public long getToUser() {
        return toUser;
    }
    public void setToUser(long toUser) {
        this.toUser = toUser;
    }
    public UserProfile getFromUserProfile() {
        return fromUserProfile;
    }
    public void setFromUserProfile(UserProfile fromUserProfile) {
        this.fromUserProfile = fromUserProfile;
    }
    public UserProfile getToUserProfile() {
        return toUserProfile;
    }
    public void setToUserProfile(UserProfile toUserProfile) {
        this.toUserProfile = toUserProfile;
    }
    public boolean isPaydone() {
        return paydone;
    }
    public void setPaydone(boolean paydone) {
        this.paydone = paydone;
    }
    
    
}
