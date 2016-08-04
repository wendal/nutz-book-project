package net.wendal.nutzbook.module;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.annotation.ReqHeader;

import net.wendal.nutzbook.bean.BeePayment;
import net.wendal.nutzbook.service.PushService;
import net.wendal.nutzbook.util.Toolkit;

@IocBean
@At("/pay/bc")
public class BeePayModule extends BaseModule {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected PropertiesProxy conf;
    
    @Inject
    Dao dao;
    
    @Inject 
    PushService pushService;
    
    @At("/")
    @Ok("beetl:yvr/bc/list.btl")
    public void index() {}
    
    /**
     * 仅返回付款成功的记录
     */
    @At
    @Ok("json")
    public Object query(@Param("..")Pager pager) {
        if (pager.getPageNumber() < 1)
            pager.setPageNumber(1);
        if (pager.getPageSize() > 20)
            pager.setPageSize(20);
        Cnd cnd = Cnd.where("trade_success", "=", true);
        List<BeePayment> list = dao.query(BeePayment.class, cnd, pager);
        pager.setRecordCount(dao.count(BeePayment.class, cnd));
        return new QueryResult(list, pager);
    }

    @POST
    @RequiresUser
    @Ok("json")
    @At
    public Object create(@Param("to")int toUserId, 
                         @Param("title")String title, 
                         @Param("amount")int amount,
                         @ReqHeader("Referer")String referer) {
        String out_trade_no = "u"+toUserId+"z"+R.UU32();
        if (amount == 0) {
            amount = R.random(1, 100);
        }
        else if (amount < 1)
            amount = 1;
        else if (amount > 100)
            amount = 100;
        String appid = conf.get("bc.appId");
        String appSecret = conf.get("bc.appSecret");
        if (Strings.isBlank(title))
            title = "随机打赏给uid="+toUserId;
        NutMap re = new NutMap();
        re.put("out_trade_no", out_trade_no);
        re.put("amount", amount);
        re.put("title", title);
        re.put("sign", Lang.md5(appid+title+amount+out_trade_no+appSecret));
        re.put("instant_channel", "wxmp"); // 限制为微信支付好了
        re.put("return_url", referer);
        BeePayment payment = new BeePayment();
        payment.setOut_trade_no(out_trade_no);
        payment.setFromUser(Toolkit.uid());
        payment.setToUser(toUserId);
        payment.setCommet(title);
        dao.insert(payment);
        //re.put("debug", true);
        return _map("data", re);
    }
    
    @At
    @Ok("raw")
    @AdaptBy(type=JsonAdaptor.class)
    public String callback(@Param("..")BeePayment payment, @Param("sign")String sign, @Param("timestamp")String timestamp, @Param("..")NutMap params) {
        String appid = conf.get("bc.appId");
        String appSecret = conf.get("bc.appSecret");
        if (!verify(sign, appid+appSecret, timestamp)) {
            log.info("BeePay: verify fail transaction_id=" + payment.getTransaction_id());
            return "success";
        }
        log.info("BeePay: verify ok transaction_id=" + payment.getTransaction_id());
        NutMap dt = payment.getMessage_detail();
        String out_trade_no = dt.getString("out_trade_no");
        BeePayment indb = dao.fetch(BeePayment.class, out_trade_no);
        if (indb == null) {
            log.warn("BeePay: No such out_trade_no="+out_trade_no);
        } else {
            payment.setFromUser(indb.getFromUser());
            payment.setToUser(indb.getToUser());
            payment.setCommet(indb.getCommet());
            payment.setOut_trade_no(out_trade_no);
            payment.setCreateTime(null);
            payment.setUpdateTime(new Date());
            log.info("BeePay: update out_trade_no="+out_trade_no);
            dao.updateIgnoreNull(payment);
            // 提醒收款人
            String payfee = String.format("%.2f", payment.getTransaction_fee()/100.0);
            pushService.alert(payment.getToUser(), "您收到一笔打赏" + payfee+"元", new HashMap<>());
            pushService.alert(payment.getFromUser(), "您成功打赏了" + payfee+"元", new HashMap<>());
        }
        return "success";
    }
    
    protected static boolean verify(String sign, String text, String key) {
        text = text + key;
        String mysign = DigestUtils.md5Hex(text.getBytes());
        //log.info("mysign:" + mysign);
        long timeDifference = System.currentTimeMillis() - Long.valueOf(key);
        //log.info("timeDifference:" + timeDifference);
        if (mysign.equals(sign) && timeDifference <= 300000) {
            return true;
        } else {
            return false;
        }
    }
    
    public static void main(String[] args) {
        System.out.printf("%.2f", 0.987);
    }
}
