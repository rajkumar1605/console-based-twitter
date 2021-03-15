package com.tweetapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.tweetapp.domain.Tweet;

public class TweetDao {

	private ConnectDB db;

	public TweetDao(ConnectDB db) {
		super();
		this.db = db;
	}

	public boolean save(Tweet tweet) {
		Connection con = db.getConnection();
		if (con != null) {
			String query = "insert into tweet values(?,?,?,?)";
			try {
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setString(1, tweet.getId());
				stmt.setString(2, tweet.getUserId());
				stmt.setString(3, tweet.getMessage());
				stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				stmt.execute();
				db.closeConnection(stmt, con);
				System.out.println("\nYour tweet has been posted successfully!!!");
				return true;
			} catch (SQLException e) {
				System.out.println("Failed to post your tweet. Please try again later.");
			}
		}
		return false;
	}

	public List<Tweet> findByUserId(String id) {
		Connection con = db.getConnection();
		if (con != null) {
			List<Tweet> tweetList = new ArrayList<Tweet>();
			String query = "select * from tweet where user_id = ?";
			try {
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setString(1, id);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Tweet tweet = new Tweet();
					tweet.setId(rs.getString(1));
					tweet.setUserId(rs.getString(2));
					tweet.setMessage(rs.getString(3));
					tweet.setPostedTime(rs.getTimestamp(4));
					tweetList.add(tweet);
				}
				db.closeConnection(stmt, con);
				return tweetList;
			} catch (Exception e) {
				System.out.println("Error occured when finding tweets by user id.");
			}
		}
		return null;
	}

	public List<Tweet> findAll() {
		Connection con = db.getConnection();
		if (con != null) {
			List<Tweet> tweetList = new ArrayList<Tweet>();
			String query = "select * from tweet";
			try {
				PreparedStatement stmt = con.prepareStatement(query);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Tweet tweet = new Tweet();
					tweet.setId(rs.getString(1));
					tweet.setUserId(rs.getString(2));
					tweet.setMessage(rs.getString(3));
					tweet.setPostedTime(rs.getTimestamp(4));
					tweetList.add(tweet);
				}
				db.closeConnection(stmt, con);
				return tweetList;
			} catch (Exception e) {
				System.out.println("Error occured when finding all tweets.");
			}
		}
		return null;
	}

}
