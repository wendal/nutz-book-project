package net.wendal.nutzbook.bean;

import org.nutz.dao.entity.annotation.*;

@Table("t_nutz_dw")
public class DwRecord extends BasePojo {

    private static final long serialVersionUID = 1L;
    
    @Id(auto=false)
    @Prev(els=@EL("ig('t_nutz_dw')"))
    private int id;

    @Name
    protected String name;
    
    @Column("cnt")
    private String content;
    
    @Column
    private String md5;
    
    @Column("dcount")
    private int dcount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getDcount() {
        return dcount;
    }

    public void setDcount(int dcount) {
        this.dcount = dcount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}
