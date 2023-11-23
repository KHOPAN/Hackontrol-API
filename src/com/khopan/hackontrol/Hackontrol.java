package com.khopan.hackontrol;

import java.util.List;

import com.khopan.hackontrol.exception.AwaitReadyException;
import com.khopan.hackontrol.exception.GuildNotFoundException;
import com.khopan.hackontrol.exception.NoTextChannelException;
import com.khopan.hackontrol.network.Request;
import com.khopan.hackontrol.network.Response;

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
	private final Request request;
	private final Response response;

	public Hackontrol() {
		this.bot = JDABuilder.createDefault(Token.BOT_TOKEN)
				.enableIntents(GatewayIntent.GUILD_MESSAGES)
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

		this.request = new Request(this.channel);
		this.response = new Response();
		this.request.statusQuery();
	}

	private class Listener extends ListenerAdapter {
		@Override
		public void onMessageReceived(MessageReceivedEvent Event) {
			String message = Event.getMessage().getContentDisplay();
			Hackontrol.this.response.parse(message);
		}
	}
}
