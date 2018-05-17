package com.rocket.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.IntStream;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

import org.apache.commons.lang.IncompleteArgumentException;

import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;

public final class JarvisSqlServerPoolDataSource {

	private final int initialPoolsize;
	private final int maxPoolSize;
	@SuppressWarnings("unused")
	private final int inactiveConnectionTimeout;
	@SuppressWarnings("unused")
	private final int timeoutCheckInterval;
	private final String poolName;
	private final String userName;
	private final String serverName;
	private final int portNo;
	private final String password;
	private final String dbName;
	private final Properties poolProperties;
	private SQLServerConnectionPoolDataSource sqlPoolConnectionSource;
	private Map<PooledConnection, Boolean> poolMap;
	private Object lockObject = new Object();

	private static final DBSourceBuilder BUILDER_INSTANCE = new DBSourceBuilder();

	private JarvisSqlServerPoolDataSource(String poolName, String serverName, int portNo, String user, String passwd,
			String db) {
		this(poolName, serverName, portNo, user, passwd, db, 1, 5, 600000, 600000);
	}

	private JarvisSqlServerPoolDataSource(String poolName, String serverName, int portNo, String user, String passwd,
			String db, int initialPoolsiz, int maxPoolsize, int inactiveTimeout, int checkTimeout) {
		this.poolName = poolName;
		this.serverName = serverName;
		this.portNo = portNo;
		this.userName = user;
		this.password = passwd;
		this.dbName = db;
		this.initialPoolsize = initialPoolsiz;
		this.maxPoolSize = maxPoolsize;
		this.inactiveConnectionTimeout = inactiveTimeout;
		this.timeoutCheckInterval = checkTimeout;
		poolProperties = new Properties();
		sqlPoolConnectionSource = new SQLServerConnectionPoolDataSource();
		poolMap = new HashMap<>();
		initilizeDataSource();
	}

	public static DBSourceBuilder builder() {
		return BUILDER_INSTANCE;
	}

	private void initilizeDataSource() {
		if (null != sqlPoolConnectionSource) {
			sqlPoolConnectionSource.setApplicationName("jarvis");
			sqlPoolConnectionSource.setDatabaseName(dbName);
			sqlPoolConnectionSource.setPassword(this.password);
			sqlPoolConnectionSource.setServerName(serverName);
			sqlPoolConnectionSource.setPortNumber(portNo);
			sqlPoolConnectionSource.setUser(userName);
		}
		IntStream.range(1, initialPoolsize).forEach(x -> {
			try {
				PooledConnection conn = sqlPoolConnectionSource.getPooledConnection();
				conn.addConnectionEventListener(new ConnectionEventListener() {

					@Override
					public void connectionErrorOccurred(ConnectionEvent event) {
						// TODO Auto-generated method stub
					}

					@Override
					public void connectionClosed(ConnectionEvent event) {
						poolMap.put((PooledConnection) event.getSource(), true);
						synchronized (lockObject) {
							lockObject.notify();	
						}						
					}
				});
				poolMap.put(conn, true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public void setPoolProperty(String key, String value) {
		poolProperties.setProperty(key, value);
	}

	public String getPoolName() {
		return poolName;
	}

	public synchronized Connection getConnection() throws SQLException, InterruptedException {
		Connection isAvail = getConnectionInternal();
		if (isAvail != null)
			return isAvail;
		if (poolMap.size() < maxPoolSize) {
			PooledConnection newCnn = sqlPoolConnectionSource.getPooledConnection();
			newCnn.addConnectionEventListener(new ConnectionEventListener() {

				@Override
				public void connectionErrorOccurred(ConnectionEvent event) {
					// TODO Auto-generated method stub
				}

				@Override
				public void connectionClosed(ConnectionEvent event) {
					poolMap.put((PooledConnection) event.getSource(), true);
					synchronized (lockObject) {
						lockObject.notify();	
					}
				}
			});
			poolMap.put(newCnn, false);
			return newCnn.getConnection();
		}
		do {
			lockObject.wait();
		} while ((isAvail = getConnectionInternal()) != null);
		return isAvail;
	}

	private synchronized Connection getConnectionInternal() throws SQLException {
		for (PooledConnection current : poolMap.keySet()) {
			if (poolMap.get(current))
				return current.getConnection();
		}
		return null;
	}

	public static class DBSourceBuilder {
		private int portNo = 1433;
		private int initialPoolsize = 1;
		private int maxPoolSize = 5;
		private int inactiveConnectionTimeout = 600000;
		private int timeoutCheckInterval = 600000;
		private String poolName = "DEFAULT";
		private String userName = "sa";
		private String serverName;
		private String password;
		private String dbName = "master";

		private DBSourceBuilder() {

		}

		public DBSourceBuilder setUser(String user) {
			this.userName = user;
			return this;
		}

		public DBSourceBuilder setPoolName(String poolName) {
			this.poolName = poolName;
			return this;
		}

		public DBSourceBuilder setServerName(String name) {
			this.serverName = name;
			return this;
		}

		public DBSourceBuilder setPortNo(int portNo) {
			this.portNo = portNo;
			return this;
		}

		public DBSourceBuilder setPassword(String password) {
			this.password = password;
			return this;
		}

		public DBSourceBuilder setDbName(String db) {
			this.dbName = db;
			return this;
		}

		public DBSourceBuilder setInitialPoolSize(int size) {
			this.initialPoolsize = size;
			return this;
		}

		public DBSourceBuilder setMaxPoolSize(int size) {
			this.maxPoolSize = size;
			return this;
		}

		public JarvisSqlServerPoolDataSource build() throws IncompleteArgumentException {
			if (null == serverName)
				throw new IncompleteArgumentException("userName is must for the connection");
			if (null == password)
				throw new IncompleteArgumentException("userName is must for the connection");
			return new JarvisSqlServerPoolDataSource(poolName, serverName, portNo, userName, password, dbName,
					initialPoolsize, maxPoolSize, inactiveConnectionTimeout, timeoutCheckInterval);
		}
	}

}
