package net.wendal.nutzbook.core.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_sys_configure")
public class SysConfigure {
    
    public SysConfigure() {
    }

    public SysConfigure(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    @Name
    @Column("k")
    protected String key;
    
    @Column("v")
    @ColDefine(width=1024)
    protected String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
