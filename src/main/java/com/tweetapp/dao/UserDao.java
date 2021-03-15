package com.tweetapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tweetapp.domain.Gender;
import com.tweetapp.domain.User;

public class UserDao {

	private ConnectDB db;

	public UserDao(ConnectDB db) {
		super();
		this.db = db;
	}

	public boolean save(User user) {
		Connection con = db.getConnection();
		if (con != null) {
			String query = "insert into user values(?,?,?,?,?,?,?,?)";
			try {
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setString(1, user.getId());
				stmt.setString(2, user.getEmail());
				stmt.setString(3, user.getPassword());
				stmt.setString(4, user.getFirstName());
				stmt.setString(5, user.getLastName());
				stmt.setString(6, user.getGender().name());
				stmt.setString(7, user.getDob());
				stmt.setBoolean(8, user.isActive());
				stmt.execute();
				db.closeConnection(stmt, con);
				System.out.println("\nUser registered successfully!!!");
				return true;
			} catch (SQLException e) {
				System.out.println("User registration failed. Please try again later.");
			}
		}
		return false;
	}

	public List<User> findByEmail(String email) {
		Connection con = db.getConnection();
		if (con != null) {
			List<User> userList = new ArrayList<User>();
			String query = "select distinct * from user where email = ?";
			try {
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setString(1, email);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					User user = new User();
					user.setId(rs.getString(1));
					user.setEmail(rs.getString(2));
					user.setPassword(rs.getString(3));
					user.setFirstName(rs.getString(4));
					user.setLastName(rs.getString(5));
					user.setGender(Gender.valueOf(rs.getString(6)));
					user.setDob(rs.getString(7));
					user.setActive(rs.getBoolean(8));
					userList.add(user);
				}
				db.closeConnection(stmt, con);
				return userList;
			} catch (Exception e) {
				System.out.println("Error occured when finding a user by email id.");
			}
		}
		return null;
	}

	public boolean update(User user) {
		Connection con = db.getConnection();
		if (con != null) {
			String query = "update user set password = ? where id = ?";
			try {
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setString(1, user.getPassword());
				stmt.setString(2, user.getId());
				int i = stmt.executeUpdate();
				db.closeConnection(stmt, con);
				if (i > 0)
					System.out.println("\nPassword reset success!!!");
				else
					System.out.println("\nPassword reset failed. Please try again later.");
				return true;
			} catch (SQLException e) {
				System.out.println("\nPassword reset failed. Please try again later.");
			}
		}
		return false;
	}

	public boolean updateStatus(User user) {
		Connection con = db.getConnection();
		if (con != null) {
			String query = "update user set active = ? where id = ?";
			try {
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setBoolean(1, user.isActive());
				stmt.setString(2, user.getId());
				int i = stmt.executeUpdate();
				db.closeConnection(stmt, con);
				if (i > 0)
					return true;
				else
					return false;
			} catch (SQLException e) {
				System.out.println("\nStatus update failed... Logging you out. Please try again later.");
			}
		}
		return false;
	}

}
