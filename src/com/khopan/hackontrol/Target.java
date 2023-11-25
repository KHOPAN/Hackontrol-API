package com.khopan.hackontrol;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.entities.Message.Attachment;

public class Target {
	private final Hackontrol hackontrol;
	private final MachineId identifier;

	boolean connected;

	private volatile BufferedImage image;

	public Target(Hackontrol hackontrol, MachineId identifier) {
		this.hackontrol = hackontrol;
		this.identifier = identifier;
		this.connected = true;
	}

	public MachineId getMachineIdentifier() {
		return this.identifier;
	}

	public BufferedImage screenshot() {
		this.check();
		this.image = null;
		this.hackontrol.request.screenshot();
		while(this.image == null) {}
		return this.image;
	}

	void screenshotTaken(Attachment attachment) {
		new Thread(() -> {
			BufferedImage image;

			try {
				InputStream stream = attachment.getProxy().download().get();
				image = ImageIO.read(stream);
			} catch(Throwable ignored) {
				ignored.printStackTrace();
				return;
			}

			this.image = image;
		}).start();
	}

	private void check() {
		if(!this.connected) {
			throw new IllegalStateException("Target was disconnected!");
		}
	}
}
