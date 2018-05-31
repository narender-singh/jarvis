package com.rocket.jdbc;

public abstract class RocketConnectionPoolFactory {

	private RocketConnectionPoolFactory() {
	}

	public enum DBs {
		MONGO, CASSANDRA, SQL_SERVER
	}

	public static RocketSqlServerConnectionPool getSqlServerDataSource() {
		return null;
	}

	public static RocketMongoDbConnectionPool getMongoDbDataSource() {
		return null;
	}

	public static RocketCassandraDbConnectionPool getCassandraDataSource() {
		return null;
	}

}
