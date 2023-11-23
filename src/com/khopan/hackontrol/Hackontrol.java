package com.khopan.hackontrol;

import com.khopan.hackontrol.exception.AwaitReadyException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Hackontrol {
	private final JDA bot;

	public Hackontrol() {
		this.bot = JDABuilder.createDefault(Token.BOT_TOKEN)
				.enableIntents(GatewayIntent.GUILD_MESSAGES)
				.build();

		try {
			this.bot.awaitReady();
		} catch(InterruptedException | IllegalStateException Errors) {
			throw new AwaitReadyException("Error while awaitReady()", Errors);
		}
	}
}
