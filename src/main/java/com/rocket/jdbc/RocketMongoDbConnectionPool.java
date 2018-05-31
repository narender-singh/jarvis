package com.rocket.jdbc;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

public class RocketMongoDbConnectionPool {

	private static final PoolBuilder BUILDER_INSTANCE = new PoolBuilder();

	MongoClient mClient;

	private RocketMongoDbConnectionPool(MongoClient client) {
		mClient = client;
	}

	public static PoolBuilder builder() {
		return BUILDER_INSTANCE;
	}

	public static class PoolBuilder {

		private MongoClientOptions.Builder mongoBuilder =  MongoClientOptions.builder();
		 
		private PoolBuilder() {
			mongoBuilder.applicationName("rocket");
		}
		
		public PoolBuilder withApplicationName(String name){
			mongoBuilder.applicationName(name);
			return this;
		}
		
		public PoolBuilder withMaxConnectionPerHost(int max){
			mongoBuilder.connectionsPerHost(max);
			return this;
		}
		
		public PoolBuilder withRetryWrite(Boolean retryWrites){
			mongoBuilder.retryWrites(retryWrites);
			return this;
		}

		public RocketMongoDbConnectionPool build() {
			MongoClientOptions options = mongoBuilder.build();
			return new RocketMongoDbConnectionPool(new MongoClient("localhost", options));
		}

	}

}
