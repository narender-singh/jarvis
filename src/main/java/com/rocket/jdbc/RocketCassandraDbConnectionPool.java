package com.rocket.jdbc;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.ExponentialReconnectionPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;
import com.datastax.driver.core.policies.RetryPolicy;

public class RocketCassandraDbConnectionPool {

	private static final PoolBuilder BUILDER_INSTANCE = new PoolBuilder();

	private final Cluster cassandraCluster;
	private final Session cassandraSession;

	private RocketCassandraDbConnectionPool(Cluster cluster) {
		cassandraCluster = cluster;
		cassandraSession = cassandraCluster.connect();
	}

	public Session getsession() {
		return cassandraSession;
	}

	public static PoolBuilder builder() {
		return BUILDER_INSTANCE;
	}

	public static class PoolBuilder {

		private String clusterName = "test_cluster";
		private int port = ProtocolOptions.DEFAULT_PORT;
		private ProtocolVersion protocolVersion = ProtocolVersion.NEWEST_SUPPORTED;
		private ReconnectionPolicy reconnectPolicy = new ExponentialReconnectionPolicy(0, 120000);
		private RetryPolicy retryPolicy = DefaultRetryPolicy.INSTANCE;
		private String userName = null;
		private String password = null;
		private PoolingOptions poolOptions = new PoolingOptions() {
			{
				setConnectionsPerHost(HostDistance.LOCAL, 1, 10);
				setHeartbeatIntervalSeconds(60);
				setCoreConnectionsPerHost(HostDistance.REMOTE, 1);
				setMaxConnectionsPerHost(HostDistance.REMOTE, 4);
			}
		};

		private PoolBuilder() {

		}

		public PoolBuilder withClusterName(String name) {
			this.clusterName = name;
			return this;
		}

		public PoolBuilder withPort(int port) {
			this.port = port;
			return this;
		}

		public PoolBuilder withProtocolVersion(ProtocolVersion version) {
			this.protocolVersion = version;
			return this;
		}

		public PoolBuilder withReconnectPolicy(ReconnectionPolicy policy) {
			this.reconnectPolicy = policy;
			return this;
		}

		public PoolBuilder withRetryPolicy(RetryPolicy policy) {
			this.retryPolicy = policy;
			return this;
		}

		public PoolBuilder withCredential(String user, String password) {
			this.userName = user;
			this.password = password;
			return this;
		}

		public PoolBuilder withPoolingOptions(PoolingOptions option) {
			this.poolOptions = option;
			return this;
		}

		public RocketCassandraDbConnectionPool build() {

			Cluster cluster = Cluster.builder().withClusterName(clusterName).withPort(port)
					.withProtocolVersion(protocolVersion).withReconnectionPolicy(reconnectPolicy)
					.withRetryPolicy(retryPolicy).withCredentials(userName, password).withPoolingOptions(poolOptions)
					.build();
			return new RocketCassandraDbConnectionPool(cluster);
		}
	}

}
