package com.tweetapp.twitter;

import com.tweetapp.dao.ConnectDB;
import com.tweetapp.dao.UserDao;
import com.tweetapp.service.AuthService;
import com.tweetapp.service.UserService;

/**
 * Twitter!
 *
 */
public class App {
	public static void main(String[] args) {
		boolean authenticate = false;

		ConnectDB db = new ConnectDB();
		UserDao userRepo = new UserDao(db);
		UserService userService = new UserService(userRepo);
		AuthService auth = new AuthService(userService);

		System.out.println("\n//**************** Welcome to Twitter! ****************//");
		do {
			authenticate = auth.performUserAuth();
		} while (authenticate);
		System.out.println("Please restart the application to use again.");
	}
}
