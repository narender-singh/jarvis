package com.rocket.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.rocket.db.JarvisSqlServerPoolDataSource;

public class DBService implements IService {

	private static final JarvisSqlServerPoolDataSource dSource;

	static {
		dSource = JarvisSqlServerPoolDataSource.builder().setServerName("localhost").setPortNo(1433).setUser("sa")
				.setPassword("Qwerty@123").setDbName("TutorialDB").build();
	}

	public int query(String query) {

		Connection conn;
		try {
			conn = dSource.getConnection();
			Statement stmt = conn.createStatement();
			int res = stmt.executeUpdate(query);
			conn.close();
			return res;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
