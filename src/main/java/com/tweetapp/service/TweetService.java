package com.tweetapp.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.tweetapp.dao.ConnectDB;
import com.tweetapp.dao.TweetDao;
import com.tweetapp.dao.UserDao;
import com.tweetapp.domain.CustomError;
import com.tweetapp.domain.Tweet;
import com.tweetapp.domain.TweetResponse;
import com.tweetapp.domain.User;
import com.tweetapp.domain.UserResponse;

public class TweetService {

	private TweetDao tweetRepo;
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public TweetService(TweetDao tweetRepo) {
		super();
		this.tweetRepo = tweetRepo;
	}

	public void dashboard(UserResponse userResponse) {
		User user = userResponse.getUser();

		int choice = 0;
		System.out.println("\nHello " + user.getFirstName() + "! You have logged in successfully.");
		try {
			do {
				choice = 0;
				System.out.println(
						"\nNavigation: \n1-Post a tweet \n2-View my tweets \n3-View all tweets \n4-Reset Password \n5-Logout");
				choice = Integer.parseInt(reader.readLine());
				switch (choice) {
				case 1:
					postTweet(user, new TweetResponse());
					break;
				case 2:
					viewMyTweets(user);
					break;
				case 3:
					viewAllTweets();
					break;
				case 4:
					ConnectDB db = new ConnectDB();
					UserDao userRepo = new UserDao(db);
					UserService userService = new UserService(userRepo);
					userService.resetPassword(userResponse);
					break;
				case 5:
					System.out.println("\nThank you for using twitter!!! See you soon...");
					break;
				default:
					System.out.println("\nPlease select the correct option");
					break;
				}
			} while (choice != 5);
		} catch (Exception e) {
			System.out.println(
					"\nExpected a number for selection. Cannot process your request further, 5-Logout initiated... You have to Login to try again.");
		}
	}

	public void postTweet(User user, TweetResponse tweetResponse) {
		int choice = 0;
		try {
			do {
				choice = 0;
				String message;
				tweetResponse.setErrors(null);

				System.out.println("\n//***** Post a tweet *****//");
				System.out.print("Enter the message to post (255 characters): ");
				message = reader.readLine();
				if (isNullOrEmpty(message))
					errorBuilder(tweetResponse, 422, "Tweet message cannot be empty.");
				else if (message.length() > 255)
					errorBuilder(tweetResponse, 422,
							"Tweet message length must be lesser than or equal to 255 characters.");

				if (tweetResponse.getErrors() != null) {
					if (!tweetResponse.getErrors().isEmpty()) {
						System.out.println("\nTweet message validation: ");
						tweetResponse.getErrors().forEach((err) -> System.out.println(err.getMessage()));

						System.out.println("\nDo you want to try posting again? \n1-Yes \n2-No (Go Back)");
						choice = Integer.parseInt(reader.readLine());
					}
				} else {
					Tweet tweet = tweetResponse.getTweet();
					tweet.setUserId(user.getId());
					tweet.setMessage(message);
					tweet.setId(new IdGenerator().getId());

					tweetRepo.save(tweet);
				}
			} while (choice == 1);
		} catch (Exception e) {
			System.out.println(
					"\nExpected a number for selection. Cannot process your request further, Please try to post the tweet again.");
		}

	}

	public void viewMyTweets(User user) {
		List<Tweet> tweetList = tweetRepo.findByUserId(user.getId());
		if (tweetList != null) {
			if (!tweetList.isEmpty()) {
				listTweets(tweetList);
			}
		} else {
			System.out.println("\nSorry! You have not posted any tweet so far!");
		}
	}

	public void viewAllTweets() {
		List<Tweet> tweetList = tweetRepo.findAll();
		if (tweetList != null) {
			if (!tweetList.isEmpty()) {
				listTweets(tweetList);
			}
		} else {
			System.out.println("\nSorry! No tweets posted so far!");
		}
	}

//	Helper
	private void errorBuilder(TweetResponse tweetResponse, int code, String message) {
		CustomError error = new CustomError();
		error.setCode(code);
		error.setMessage(message);
		if (tweetResponse.getErrors() == null || tweetResponse.getErrors().isEmpty()) {
			List<CustomError> errorList = new ArrayList<>();
			errorList.add(error);
			tweetResponse.setErrors(errorList);
		} else {
			tweetResponse.getErrors().add(error);
		}
	}

	private boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
	}

	private void listTweets(List<Tweet> tweetList) {
		System.out.println("\nTweets");
		System.out.println("------------------------------------------------------------------------------------");
		tweetList.forEach((tweet) -> System.out.println(tweet.getMessage() + "\n" + tweet.getPostedTime() + "\n"));
		System.out.println("------------------------------------------------------------------------------------");
	}

}
