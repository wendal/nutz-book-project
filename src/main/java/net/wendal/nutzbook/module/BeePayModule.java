package net.wendal.nutzbook.module;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.lang.util.Callback;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.annotation.ReqHeader;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfStamper;

import net.wendal.nutzbook.bean.BeePayment;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.service.PushService;
import net.wendal.nutzbook.util.Toolkit;

@Api(name="支付服务", description="对接BeeCloud的支付服务,当前只对接了打赏", match=ApiMatchMode.NONE)
@IocBean
@At("/pay/bc")
public class BeePayModule extends BaseModule {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected PropertiesProxy conf;
    
    @Inject 
    protected PushService pushService;
    
    @At("/")
    @Ok("beetl:yvr/bc/index.html")
    public Object index() {
        return _map("current_user", fetch_userprofile(Toolkit.uid()));
    }
    
    /**
     * 仅返回付款成功的记录
     */
    @At
    @Ok("json:{locked:'message_detail'}")
    public Object query(@Param("from")int fromUser, @Param("to")int toUser, @Param("..")Pager pager) {
        if (pager.getPageNumber() < 1)
            pager.setPageNumber(1);
        if (pager.getPageSize() > 20)
            pager.setPageSize(20);
        Cnd cnd = Cnd.where("trade_success", "=", true);
        if (fromUser > 0)
            cnd.and("fromUser", "=", fromUser);
        if (toUser > 0)
            cnd.and("toUser", "=", toUser);
        cnd.desc("createTime");
        List<BeePayment> list = dao.query(BeePayment.class, cnd, pager);
        dao.fetchLinks(list, null);
        pager.setRecordCount(dao.count(BeePayment.class, cnd));
        NutMap re = _map("list", list, "pager", pager);
        if (fromUser > 0)
            re.put("countF", dao.func(BeePayment.class, "sum", "transaction_fee", Cnd.where("trade_success", "=", true).and("fromUser", "=", fromUser)));
        if (toUser > 0)
            re.put("countT", dao.func(BeePayment.class, "sum", "transaction_fee", Cnd.where("trade_success", "=", true).and("toUser", "=", toUser)));
        
        return re;
    }

    @POST
    @RequiresUser
    @Ok("json")
    @At
    public Object create(@Param("to")int toUserId, 
                         @Param("title")String title, 
                         @Param("amount")int amount,
                         @ReqHeader("Referer")String referer) {
        if (amount == 0) {
            amount = R.random(88, 1088);
        }
        else if (amount < 88)
            amount = 188;
        String id = R.UU32();
        if (Strings.isBlank(title)) {
            UserProfile profile = dao.fetch(UserProfile.class, toUserId);
            title = String.format("打赏给%s %s", profile.getDisplayName(), id);
            if (title.length() > 16)
                title = title.substring(0, 16);
        }
        return _createPay(toUserId, title, amount, referer, id);
    }
    
    @POST
    @RequiresUser
    @Ok("json")
    @At
    public Object create2(@Param("to")int toUserId, 
                         @Param("title")String title, 
                         @Param("amount")int amount,
                         @ReqHeader("Referer")String referer) {
        if (amount < 999)
            amount = 999;// 土豪就是任性
        if (Strings.isBlank(title))
            title = "土豪,任性";
        return _createPay(toUserId, title, amount, referer, R.UU32());
    }
    
    protected Object _createPay(int toUserId, String title, int amount, String referer, String id) {
        String appid = conf.get("bc.appId");
        String appSecret = conf.get("bc.appSecret");
        String out_trade_no = "u"+toUserId+"z"+id;
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
        payment.setTransaction_fee(amount);
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
            payment.setTransaction_fee(indb.getTransaction_fee());
            payment.setFromUser(indb.getFromUser());
            payment.setToUser(indb.getToUser());
            payment.setCommet(indb.getCommet());
            payment.setOut_trade_no(out_trade_no);
            payment.setCreateTime(null);
            payment.setUpdateTime(new Date());
            log.info("BeePay: update out_trade_no="+out_trade_no);
            dao.updateIgnoreNull(payment);
            dao.fetchLinks(payment, null);
            // 提醒收款人
            String payfee = String.format("%.2f", payment.getTransaction_fee()/100.0);
            pushService.alert(payment.getToUser(), "您收到一笔打赏" + payfee+"元", "From: " + payment.getFromUserProfile().getDisplayName(), new HashMap<>());
            pushService.alert(payment.getFromUser(), "您成功打赏了" + payfee+"元", "To: " + payment.getToUserProfile().getDisplayName(), new HashMap<>());
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
    
    @Ok("pdf:pdftmpl/beepay_one.pdf")
    @Fail("void")
    @At({"/pdf/?"})
    public Object downloadPayPdf(String out_trace_no, HttpServletRequest req) {
        return doPdf(out_trace_no, false, req);
    }
    
    @Ok("pdf:pdftmpl/beepay_one.pdf")
    @Fail("void")
    @At({"/pdfview/?"})
    public Object viewPayPdf(String out_trace_no, HttpServletRequest req) {
        return doPdf(out_trace_no, true, req);
    }
    
    protected Object doPdf(String out_trace_no, boolean viewOnly, HttpServletRequest req) {
        BeePayment payment = dao.fetch(BeePayment.class, out_trace_no);
        if (payment == null)
            return new HttpStatusView(404);
        Context cnt = Lang.context();
        dao.fetchLinks(payment, null);
        UserProfile from = payment.getFromUserProfile();
        UserProfile to = payment.getToUserProfile();
        cnt.set("*viewOnly", viewOnly);
        cnt.set("payment_fromUser", from.getDisplayName());
        cnt.set("payment_toUser", to.getDisplayName());
        cnt.set("payment_fee", String.format("%.2f", payment.getTransaction_fee()/100.0));
        cnt.set("payment_time", Times.sDT(payment.getUpdateTime()));
        cnt.set("filename", "nutzcn_tips_"+out_trace_no+".pdf");
        cnt.set("*callback", new Callback<PdfStamper>() {
            public void invoke(PdfStamper ps) {
                try {
                    BarcodeQRCode qrcode = new BarcodeQRCode(req.getRequestURL().toString().replace("view", ""), 1, 1, null);  
                    Image qrcodeImage = qrcode.getImage();  
                    qrcodeImage.setAbsolutePosition(350, 0);  
                    qrcodeImage.scalePercent(512); 
                    ps.getUnderContent(1).addImage(qrcodeImage);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return cnt;
    }
    
    protected byte[] buf;
    protected String jsEtag;
    
    @At("/js")
    @Ok("raw:js")
    public Object js(@ReqHeader("If-None-Match")String _etag, HttpServletResponse _resp) {
        if (_etag != null && jsEtag != null && _etag.equals(jsEtag))
            return HTTP_304;
        if (buf == null) {
            String url = "https://jspay.beecloud.cn/1/pay/jsbutton/returnscripts?appId=" + conf.get("bc.appId");
            Response resp = Http.get(url);
            if (resp.isOK()) {
                buf = Streams.readBytesAndClose(resp.getStream());
                jsEtag = Lang.md5(new ByteArrayInputStream(buf));
            }
        }
        if (buf == null)
            return HTTP_404;
        _resp.setHeader("ETag", jsEtag);
        return buf;
    }
}
