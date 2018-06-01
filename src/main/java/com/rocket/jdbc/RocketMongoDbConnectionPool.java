package com.rocket.jdbc;

import java.util.LinkedList;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public final class RocketMongoDbConnectionPool {

	private static final PoolBuilder BUILDER_INSTANCE = new PoolBuilder();

	MongoClient mClient;

	private RocketMongoDbConnectionPool(MongoClient client) {
		mClient = client;
	}

	public static PoolBuilder builder() {
		return BUILDER_INSTANCE;
	}

	public static class PoolBuilder {

		private MongoClientOptions.Builder mongoBuilder = MongoClientOptions.builder();
		private List<ServerAddress> serverAddress = new LinkedList<>();
		private int port = 27017;
		private String host = "localhost";
		private String userName;
		private char[] password;
		private String db = "admin";

		private PoolBuilder() {
			mongoBuilder.applicationName("rocket");
		}

		public PoolBuilder withServer(String host, int port) {
			serverAddress.add(new ServerAddress(host, port));
			return this;
		}

		public PoolBuilder withHost(String host) {
			this.host = host;
			return this;
		}

		public PoolBuilder withPort(int port) {
			this.port = port;
			return this;
		}

		public PoolBuilder withUserName(String user) {
			this.userName = user;
			return this;
		}

		public PoolBuilder withDB(String db) {
			this.db = db;
			return this;
		}

		public PoolBuilder withPassword(String password) {
			this.password = password.toCharArray();
			return this;
		}

		public PoolBuilder withApplicationName(String name) {
			mongoBuilder.applicationName(name);
			return this;
		}

		public PoolBuilder withMaxConnectionPerHost(int max) {
			mongoBuilder.connectionsPerHost(max);
			return this;
		}

		public PoolBuilder withRetryWrite(Boolean retryWrites) {
			mongoBuilder.retryWrites(retryWrites);
			return this;
		}

		public RocketMongoDbConnectionPool build() {
			MongoClientOptions options = mongoBuilder.build();
			MongoCredential credential = null;
			if (userName != null)
				credential = MongoCredential.createCredential(userName, db, password);
			if (serverAddress.size() < 0) {
				if (host != null)
					serverAddress.add(new ServerAddress(host, port));
				else
					serverAddress.add(new ServerAddress());
			}
			if (credential != null)
				return new RocketMongoDbConnectionPool(new MongoClient(serverAddress, credential, options));
			return new RocketMongoDbConnectionPool(new MongoClient(serverAddress, options));
		}
	}
}
