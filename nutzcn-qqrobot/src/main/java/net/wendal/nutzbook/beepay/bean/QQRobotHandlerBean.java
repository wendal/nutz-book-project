package net.wendal.nutzbook.beepay.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import net.wendal.nutzbook.core.bean.IdentityPojo;

@Table("t_qq_robot_handler")
public class QQRobotHandlerBean extends IdentityPojo {

    private static final long serialVersionUID = -932039999573834899L;
    /**
     * 每个处理器都有一个唯一的名称
     */
    @Name
    private String name;
    /**
     * 需要匹配的正则表达式,未使用
     */
    @Column("mt")
    private String match;
    /**
     * content的类型, 可以是text, 也可以是js
     */
    @Column("ctp")
    private String ctype;
    /**
     * 文本或脚本
     */
    @Column("cnt")
    @ColDefine(width=10240)
    private String content;
    /**
     * 优先级配置
     */
    @Column("pri")
    private Integer priority;
    
    /**
     * 是否启用
     */
    @Column("enb")
    private boolean enable;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMatch() {
        return match;
    }
    public void setMatch(String match) {
        this.match = match;
    }
    public String getCtype() {
        return ctype;
    }
    public void setCtype(String ctype) {
        this.ctype = ctype;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    } 
    
}
