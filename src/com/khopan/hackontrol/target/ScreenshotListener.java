package com.khopan.hackontrol.target;

@FunctionalInterface
public interface ScreenshotListener {
	void screenshotTaken(byte[] image);
}
