package net.wendal.nutzbook;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.nutz.lang.Lang;

public class DyDataSorce implements DataSource{
	
	public static ThreadLocal<DataSource> th = new ThreadLocal<>();
	
	public Connection getConnection() throws SQLException {
		return th.get().getConnection();
	}
	
	// ---------------------------------
	// ---- 其他方法没用
	// ---------------------------------
	
	public PrintWriter getLogWriter() throws SQLException {
		throw Lang.noImplement();
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		throw Lang.noImplement();
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		throw Lang.noImplement();
	}

	public int getLoginTimeout() throws SQLException {
		throw Lang.noImplement();
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw Lang.noImplement();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw Lang.noImplement();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw Lang.noImplement();
	}

	public Connection getConnection(String username, String password) throws SQLException {
		throw Lang.noImplement();
	}

}
