package net.wendal.nutzbook.core.bean;

import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Prev;

/**
 * 共享字段,免得每个Pojo类都加创建时间和生成时间
 * @author wendal
 *
 */
public abstract class IdentityPojo extends BasePojo {
	
	private static final long serialVersionUID = 1L;
	
	@Id(auto=false)
    @Prev(els=@EL("ig(view.tableName)"))
    protected long id;
	
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
