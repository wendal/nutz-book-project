package net.wendal.nutzbook.bean.qqbot;

import net.wendal.nutzbook.bean.BasePojo;
import org.nutz.dao.entity.annotation.*;

/**
 * QQBot响应规则
 * Created by wendal on 2015/12/16.
 */
@Table("t_qqbot_role")
public class QQBotRole extends BasePojo {
    private static final long serialVersionUID = -6181843480105526784L;
	@Id
    public int id;
    @Name
    @Column("nm")
    public String name;
    @Column("pri")
    public long priority;
    @Column("mt")
    public String matchType;
    @Column("mv")
    @ColDefine(width = 1024)
    public String matchValue;
    @Column("et")
    public String executeType;
    @Column("ep")
    public String executeParams;
    @Column("eb")
    @ColDefine(width = 20000)
    public String executeBody;
    @Column("ht")
    public String helpText;

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof QQBotRole))
            return false;
        return this.id == ((QQBotRole)obj).id;
    }
}
