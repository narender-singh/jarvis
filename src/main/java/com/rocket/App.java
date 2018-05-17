package com.rocket;

import com.rocket.core.Rocket;
import com.rocket.core.configuration.CamelConfiguration;

public class App {
	public static void main(String[] args) {

		Rocket.build().withProperty("portNo", "8080").withClass(CamelConfiguration.class)
				.withClass(JarvisConfiguration.class).withClass(JarvisRoutes.class).initialize().launchAndWait();

		// DBService db = new DBService();
		// String query = "INSERT INTO [User] ([Name],[Hobby]) VALUES
		// ('Narender','Programming is my passion')";
		// int response = db.query(query);
		// System.out.println("Record Inserted " + (response));

	}
}
