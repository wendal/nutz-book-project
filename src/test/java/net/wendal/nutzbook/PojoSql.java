package net.wendal.nutzbook;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Next;
import org.nutz.dao.entity.annotation.SQL;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.JsonField;

@Table("t_pojo_sql")
public class PojoSql {

    @Id
    private int id;
    
    @Name
    private String name;
    
    @Next(@SQL("select name from t_pojo_sql where id=@id"))
    private String nickname;
    
    private int age;
    
    private long ct;
    
    @JsonField(dataFormat="yyyy-MM-dd'T'HH:mm:ss.000Z")
    private java.util.Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }
    
    
}
