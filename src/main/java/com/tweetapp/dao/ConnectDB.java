package com.tweetapp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectDB {

	public ConnectDB() {
		super();
	}

	public Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/twitter", "root", "root");
			return con;
		} catch (Exception e) {
			System.out.println("Unable to create database connection. Try again later.");
		}
		return null;
	}

	public void closeConnection(Statement stmt, Connection con) {
		try {
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Unable to close the database connection. Waiting for automatic closing...");
		}
	}

}
