package com.banana.jarvis;

import com.banana.jarvis.service.DBService;

public class App {
	public static void main(String[] args) {
		DBService db = new DBService();
		String query = "INSERT INTO [User] ([Name],[Hobby]) VALUES ('Narender','Programming is my passion')";		
		int response = db.query(query); 
		System.out.println("Record Inserted " + (response));

	}
}
