package net.wendal.nutzbook.bean.yvr;

import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import net.wendal.nutzbook.bean.BasePojo;

@Table("t_sub_forum")
public class SubForum extends BasePojo {

    private static final long serialVersionUID = 5818719267870936119L;
    
    @Column
    private String display;

    @Name
    private String tagname;
    
    @Column
    @ColDefine(width=1024)
    private List<String> masters;

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public List<String> getMasters() {
        return masters;
    }

    public void setMasters(List<String> masters) {
        this.masters = masters;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
