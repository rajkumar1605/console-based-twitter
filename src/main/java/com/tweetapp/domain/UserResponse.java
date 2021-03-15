package com.tweetapp.domain;

import java.util.List;

public class UserResponse {

	private List<CustomError> errors;
	private User user;

	public UserResponse() {
		super();
		this.user = new User();
	}

	public List<CustomError> getErrors() {
		return errors;
	}

	public void setErrors(List<CustomError> errors) {
		this.errors = errors;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
