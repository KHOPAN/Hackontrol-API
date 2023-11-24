package com.khopan.hackontrol;

import java.util.ArrayList;
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
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Hackontrol {
	private final JDA bot;
	private final Guild guild;
	private final TextChannel channel;
	private final List<Target> targetList;
	private final Request request;
	private final Response response;

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
		Target target = new Target(identifier);
		this.targetList.add(target);

		if(this.listener != null) {
			this.listener.onTargetConnected(target);
		}
	}

	private void removeTarget(MachineId identifier) {
		for(int i = 0; i < this.targetList.size(); i++) {
			Target target = this.targetList.get(i);

			if(target.getMachineIdentifier().equals(identifier)) {
				this.targetList.remove(i);
				target.connected = false;

				if(this.listener != null) {
					this.listener.onTargetDisconnected(target);
				}

				return;
			}
		}
	}

	private class Listener extends ListenerAdapter {
		@Override
		public void onMessageReceived(MessageReceivedEvent Event) {
			String message = Event.getMessage().getContentDisplay();
			Hackontrol.this.response.parse(message);
		}
	}
}
