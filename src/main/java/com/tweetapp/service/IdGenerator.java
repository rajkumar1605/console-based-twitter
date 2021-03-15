package com.tweetapp.service;

import java.util.UUID;

public class IdGenerator {

	private String id;

	public IdGenerator() {
		super();
		this.id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

}
