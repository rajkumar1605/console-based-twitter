package com.tweetapp.domain;

import java.util.List;

public class TweetResponse {

	private List<CustomError> errors;
	private Tweet tweet;

	public TweetResponse() {
		super();
		this.tweet = new Tweet();
	}

	public List<CustomError> getErrors() {
		return errors;
	}

	public void setErrors(List<CustomError> errors) {
		this.errors = errors;
	}

	public Tweet getTweet() {
		return tweet;
	}

	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}

}
