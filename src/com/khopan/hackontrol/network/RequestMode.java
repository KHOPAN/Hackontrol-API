package com.khopan.hackontrol.network;

public interface RequestMode {
	public static final int STATUS_QUERY = 0;
	public static final int TAKE_SCREENSHOT = 1;
	public static final int EXECUTE_COMMAND = 2;
	public static final int SHOW_MESSAGE = 3;
}
