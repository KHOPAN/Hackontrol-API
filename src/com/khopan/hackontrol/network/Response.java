package com.khopan.hackontrol.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Response {
	private final ObjectMapper mapper;

	public Response() {
		this.mapper = new ObjectMapper();
	}

	public void parse(String response) {
		if(response == null) {
			return;
		}

		JsonNode node;

		try {
			node = this.mapper.readTree(response);
		} catch(Throwable ignored) {
			return;
		}

		// TODO: Do something
	}
}
