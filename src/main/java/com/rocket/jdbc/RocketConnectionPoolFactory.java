package com.rocket.jdbc;

public abstract class RocketConnectionPoolFactory {

	private RocketConnectionPoolFactory() {
	}

	public enum DBs {
		MONGO, CASSANDRA, SQL_SERVER
	}

	public static RocketSqlServerConnectionPool getSqlServerDataSource(String server, String user, String password) {
		return getSqlServerDataSource(server, 1433, user, password);
	}

	public static RocketSqlServerConnectionPool getSqlServerDataSource(String server, int port, String user,
			String password) {
		return RocketSqlServerConnectionPool.builder().setDbName("master").setInitialPoolSize(1).setMaxPoolSize(100)
				.setServerName(server).setPoolName("rocket-mssql-pool").setPortNo(port).setUser(user)
				.setPassword(password).build();
	}

	public static RocketMongoDbConnectionPool getMongoDbDataSource(String server, String user, String password) {
		return getMongoDbDataSource(server, 27017, user, password);
	}

	public static RocketMongoDbConnectionPool getMongoDbDataSource(String server, int port, String user,
			String password) {
		return RocketMongoDbConnectionPool.builder().withApplicationName("rocket").withMaxConnectionPerHost(100)
				.withRetryWrite(true).withServer(server, port).withUserName(user).withPassword(password).build();
	}

	public static RocketCassandraDbConnectionPool getCassandraDataSource(String user, String password,
			String... servers) {
		return getCassandraDataSource(user, password, 7199, servers);
	}

	public static RocketCassandraDbConnectionPool getCassandraDataSource(String user, String password, int port,
			String... servers) {
		return RocketCassandraDbConnectionPool.builder().withContactPoints(servers).withPort(port)
				.withCredential(user, password).build();
	}
}