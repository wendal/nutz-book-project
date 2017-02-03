package net.wendal.nutzbook.core.bean;

import java.io.Serializable;
import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.random.R;

public class BasePojo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Prev(els=@EL("$me.now()"))
    @Column("ct")
    protected Date createTime;
    @Prev(els=@EL("$me.now()"))
    @Column("ut")
    protected Date updateTime;
    
    public String toString() {
        return Json.toJson(this, JsonFormat.compact().setQuoteName(true).setIgnoreNull(false));
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public Date now() {
        return new Date();
    }
    
    public String uuid() {
        return R.UU32().toLowerCase();
    }
}
