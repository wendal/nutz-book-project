package net.wendal.nutzbook.weixin.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableMeta;

import net.wendal.nutzbook.core.bean.BasePojo;

@Table("t_weixin_user")
@TableMeta("{mysql-charset:'utf8mb4'}")
public class WeixinUser extends BasePojo {

    private static final long serialVersionUID = 1L;
    @Column("u_id")
    private long userId;
    @Column
    @ColDefine(width=128)
    private String openid;
    @Column
    @ColDefine(width=128)
    private String unionid;
    @Column
    private  String nickname;
    @Column
    private  int sex;
    @Column
    private  String language; 
    @Column
    private  String city;
    @Column
    private  String province;
    @Column
    private  String country; 
    @Column
    @ColDefine(width=1024)
    private  String headimgurl;
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getOpenid() {
        return openid;
    }
    public void setOpenid(String openid) {
        this.openid = openid;
    }
    public String getUnionid() {
        return unionid;
    }
    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public int getSex() {
        return sex;
    }
    public void setSex(int sex) {
        this.sex = sex;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getHeadimgurl() {
        return headimgurl;
    }
    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }
}
