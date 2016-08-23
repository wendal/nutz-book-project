package net.wendal.nutzbook.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.nutz.dao.DaoException;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.CachedNutDaoExecutor;
import org.nutz.trans.Trans;

/**
 * 演示主从数据库. 继承CachedNutDaoExecutor是为了daocache缓存,不需要的话,直接继承NutDaoExecutor即可.
 * 
 * @author wendal
 *
 */
public class MasterSlaveDaoExecutor extends CachedNutDaoExecutor {

    protected static Log log = Logs.get();

    protected DataSource slave; // TODO 变成数组来支持多个slave?

    public void exec(Connection conn, DaoStatement st) {
        // 事务内,不可以Slave
        if (!Trans.isTransactionNone() || !(st.isSelect() || st.isForceExecQuery())) {
            super.exec(conn, st);
            return;
        }
        //if (log.isDebugEnabled())
        //    log.debug("exec at slave DataSource >> " + st.toPreparedStatement());
        // 重头戏开始了
        Connection _conn = null;
        try {
            _conn = slave.getConnection();
            super.exec(_conn, st);
        }
        catch (SQLException e) {
            throw new DaoException("slave fail", e);
        }
        finally {
            if (_conn != null)
                try {
                    _conn.close();
                }
                catch (SQLException e) {
                    log.warn("fail to close slave connection!!!!", e);
                }
        }
    }
}
