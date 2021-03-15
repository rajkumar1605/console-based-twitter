package com.tweetapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AuthService {

	private UserService userService;
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public AuthService(UserService userService) {
		super();
		this.userService = userService;
	}

	public boolean performUserAuth() {
		try {
			int choice = 0;
			System.out.println("\nPlease select an option: \n1-Register \n2-Login \n3-Forgot Password");
			choice = Integer.parseInt(reader.readLine());
			switch (choice) {
			case 1:
				userService.register();
				break;
			case 2:
				userService.login();
				break;
			case 3:
				userService.forgotPassword();
				break;
			default:
				System.out.println("\nPlease select the correct option");
				break;
			}
			return true;
		} catch (NumberFormatException | IOException e) {
			System.out.println("\nPlease enter a number for selection, it must not contain any symbols or letters.");
			return true;
		} catch (Exception e) {
			System.out.println("\nUnable to reach the servers.");
			return false;
		}
	}

}
