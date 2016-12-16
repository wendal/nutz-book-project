package net.wendal.nutzbook.bean.justfuck;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

@Table("t_oschina_top10")
public class OschinaTop10 {
    
    @Column
    private long ct;
    
    @Column
    @ColDefine(width=10240)
    private String cnt;

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }
    
    
}

